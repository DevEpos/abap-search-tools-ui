package com.devepos.adt.saat.internal.search.ui;

import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.SearchEngine;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.devepos.adt.saat.internal.search.contentassist.SearchPatternProvider;
import com.devepos.adt.saat.internal.search.model.ObjectSearchQuery;
import com.devepos.adt.saat.internal.search.model.SearchType;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.AbapProjectProxy;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.TextControlUtil;
import com.devepos.adt.saat.search.model.IObjectSearchQuery;
import com.sap.adt.tools.core.project.IAbapProject;

public class ObjectSearchPage extends DialogPage implements ISearchPage {
	public static final String LAST_PROJECT_DESTINATION_PREF = "com.devepos.adt.saat.dbobjectexplorer.lastSelectedProject";
	private static final int LABEL_X_HINT = 45;
	@Inject
	IWorkbench workbench;
	private ComboViewer projectViewer;
	private ComboViewer searchTypeViewer;
	private Text searchInput;
	private Label searchStatusImageLabel;
	private SearchPatternProvider searchPatternProvider;
	final AbapProjectProxy projectProvider;
	private Label searchStatusTextLabel;
	private IResourceChangeListener projectChangeListener;

	private Composite statusArea;
	private IAction andFilterToggleAction;
	private boolean andFilterOption;
	private Composite searchComposite;
	private boolean controlsEnabled = true;
	private boolean updateLastProjectPreference;
	private final IPreferenceStore prefStore;
	private final IPropertyChangeListener preferenceListener;

	public ObjectSearchPage() {
		this.prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
		this.prefStore.setDefault(LAST_PROJECT_DESTINATION_PREF, ""); //$NON-NLS-1$
		this.updateLastProjectPreference = this.prefStore.getBoolean(IPreferences.REMEMBER_LAST_PROJECT_IN_OBJ_EXPLORER);
		this.preferenceListener = (event) -> {
			if (event.getProperty().equals(IPreferences.REMEMBER_LAST_PROJECT_IN_OBJ_EXPLORER)) {
				this.updateLastProjectPreference = (boolean) event.getNewValue();
			}
		};
		this.prefStore.addPropertyChangeListener(this.preferenceListener);
		this.projectProvider = new AbapProjectProxy(null);
	}

	@Override
	public void createControl(final Composite parent) {
		GridLayoutFactory.swtDefaults().margins(5, 5).numColumns(3).applyTo(parent);

		createProjectChooserInput(parent);
		createSearchComposite(parent);
		createStatusArea(parent);

		this.searchPatternProvider = new SearchPatternProvider(this.projectProvider,
			(SearchType) this.searchTypeViewer.getStructuredSelection().getFirstElement());
		this.searchPatternProvider.addContentAssist(this.searchInput);

		if (loadProjectList(false)) {
			// load last project if available
			if (!loadLastUsedProject()) {
				// no last project was found so use the current "selected" project
				final IProject currentABAPProject = AdtUtil.getCurrentABAPProject();
				if (currentABAPProject != null) {
					this.projectViewer.setSelection(new StructuredSelection(currentABAPProject));
				} else {
					selectFirstProject();
				}
			}
		} else {
			setControlInputEnabledState(false);
		}
		SearchAndAnalysisPlugin.getDefault().getHistory().setActiveEntry(null);
		setControl(parent);
	}

	@Override
	public boolean performAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setContainer(final ISearchPageContainer container) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the given project as the currently selected project in the project
	 * chooser dropdown
	 *
	 * @param project the project to set as the active one in the project chooser
	 */
	public void setProject(final IProject project) {
		if (this.projectViewer == null || project == null) {
			return;
		}
		if (this.projectViewer.testFindItem(project) == null) {
			loadProjectList(false);
		}
		this.projectViewer.setSelection(new StructuredSelection(project), true);
	}

	@Override
	public void dispose() {
		if (this.projectChangeListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.projectChangeListener);
		}
		if (this.preferenceListener != null) {
			this.prefStore.removePropertyChangeListener(this.preferenceListener);
		}
		if (this.searchPatternProvider != null) {
			this.searchPatternProvider.dispose();
			this.searchPatternProvider = null;
		}

		super.dispose();
	}

	/**
	 * Shows the DB Object Explorer
	 */
	public void showExplorer() {
		if (this.searchInput != null) {
			final boolean setCursorToEnd = this.prefStore.getBoolean(IPreferences.CURSOR_AT_END_OF_SEARCH_INPUT);
			if (!setCursorToEnd) {
				this.searchInput.selectAll();
			} else {
				final String currentInput = this.searchInput.getText();
				final int length = currentInput != null ? currentInput.length() : 0;
				this.searchInput.setSelection(length);
			}
			this.searchInput.setFocus();
		}
	}

	/*
	 * Loads the last used project at start up of View
	 */
	private boolean loadLastUsedProject() {
		final boolean useLastProject = this.prefStore.getBoolean(IPreferences.REMEMBER_LAST_PROJECT_IN_OBJ_EXPLORER);
		if (!useLastProject) {
			return false;
		}
		final String destinationId = this.prefStore.getString(ObjectExplorerView.LAST_PROJECT_DESTINATION_PREF);
		if (destinationId == null || destinationId.isEmpty()) {
			return false;
		}
		// find project with this id
		final IProject[] projects = (IProject[]) this.projectViewer.getInput();
		for (final IProject project : projects) {
			final IAbapProject abapProject = project.getAdapter(IAbapProject.class);
			if (abapProject != null && destinationId.equals(abapProject.getDestinationId())) {
				this.projectViewer.setSelection(new StructuredSelection(project));
				return true;
			}
		}
		// project obviously could not be found so set the stored preference to ""
		this.prefStore.setToDefault(LAST_PROJECT_DESTINATION_PREF);
		return false;
	}

	/**
	 * Creates control for choosing the current project to be used
	 *
	 * @param parent the composite control
	 */
	private void createProjectChooserInput(final Composite parent) {
		final Label projectComboLabel = new Label(parent, SWT.NONE);
		GridDataFactory.swtDefaults().hint(LABEL_X_HINT, SWT.DEFAULT).applyTo(projectComboLabel);
		projectComboLabel.setText(Messages.ObjectSearch_ProjectInput_xfld);

		this.projectViewer = new ComboViewer(parent, SWT.READ_ONLY);
		final GridData projectViewerGridData = new GridData(150, SWT.DEFAULT);
		projectViewerGridData.horizontalSpan = 2;
		this.projectViewer.getControl().setLayoutData(projectViewerGridData);

		this.projectViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.projectViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof IProject) {
					final IProject project = (IProject) element;
					return project.getName();
				}
				return super.getText(element);
			}
		});
		this.projectViewer.addSelectionChangedListener((evt) -> {
			final IStructuredSelection selection = evt.getStructuredSelection();
			if (selection != null) {
				final IProject newProjectSelection = (IProject) selection.getFirstElement();
				this.projectProvider.setProject(newProjectSelection);
				if (this.updateLastProjectPreference) {
					this.prefStore.setValue(ObjectExplorerView.LAST_PROJECT_DESTINATION_PREF,
						this.projectProvider.getAbapProject().getDestinationId());
				}
			}
		});
	}

	/**
	 * Handle the <strong>enabled</strong> state of the input controls
	 *
	 * @param enable
	 */
	private void setControlInputEnabledState(final boolean enable) {
		if (this.controlsEnabled == enable) {
			return;
		}
		this.controlsEnabled = enable;

		if (this.searchInput != null && !this.searchInput.isDisposed()) {
			this.searchInput.setEnabled(enable);
		}
		if (this.projectViewer != null && !this.projectViewer.getControl().isDisposed()) {
			this.projectViewer.getControl().setEnabled(enable);
		}
		if (this.searchTypeViewer != null && !this.searchTypeViewer.getControl().isDisposed()) {
			this.searchTypeViewer.getControl().setEnabled(enable);
		}

		if (!enable) {
//			updateStatus(IStatus.ERROR, Messages.ObjectSearch_NoProjectAvailable_xmsg);
		} else {
//			updateStatus(-1, ""); //$NON-NLS-1$
		}
	}

	private void updateViewFromQuery(final IObjectSearchQuery query) {
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
			.getProviderForDestination(query.getDestinationId());
		this.projectViewer.setSelection(new StructuredSelection(projectProvider.getProject()));

		this.searchTypeViewer.setSelection(new StructuredSelection(query.getSearchType()));
		this.searchInput.setText(query.getQuery());
		this.andFilterToggleAction.setChecked(query.isAndSearchActive());
		this.andFilterOption = query.isAndSearchActive();
	}

	/**
	 * Creates new composite to hold the controls for performing a search
	 *
	 * @param parent the parent composite
	 */
	private void createSearchComposite(final Composite parent) {
		this.searchComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(3).applyTo(this.searchComposite);
		GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(this.searchComposite);

		createSearchTypeInput(this.searchComposite);
		createSearchField(this.searchComposite);
	}

	/**
	 * Create control for selecting the current search type
	 *
	 * @param parent the composite control for the control
	 */
	private void createSearchTypeInput(final Composite parent) {
		final Label typeComboLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().hint(LABEL_X_HINT, SWT.DEFAULT).applyTo(typeComboLabel);
		typeComboLabel.setText(Messages.ObjectSearch_SearchTypeInput_xfld);

		this.searchTypeViewer = new ComboViewer(parent, SWT.READ_ONLY);
		this.searchTypeViewer.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
		this.searchTypeViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.searchTypeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof SearchType) {
					return ((SearchType) element).toString();
				}
				return super.getText(element);
			}
		});
		this.searchTypeViewer.setInput(SearchType.values());
		this.searchTypeViewer.setSelection(new StructuredSelection(SearchType.CDS_VIEW));
		this.searchTypeViewer.addSelectionChangedListener(event -> {
			final SearchType selectedSearchType = (SearchType) event.getStructuredSelection().getFirstElement();
			this.searchPatternProvider.setSearchType(selectedSearchType);
		});
	}

	/**
	 * Creates search input field
	 *
	 * @param parent the composite control for the field
	 */
	private void createSearchField(final Composite parent) {
		final Label searchInputLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().span(3, 1).indent(0, 10).applyTo(searchInputLabel);
		searchInputLabel.setText(Messages.ObjectSearch_SearchInputPrompt_xfld);

		this.searchInput = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.searchInput);
		TextControlUtil.addWordSupport(this.searchInput);
		this.searchInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					refreshSearch();
				}
			}
		});
	}

	private void createStatusArea(final Composite parent) {
		this.statusArea = new Composite(parent, SWT.NONE);

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(this.statusArea);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this.statusArea);

		this.searchStatusImageLabel = new Label(this.statusArea, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(this.searchStatusImageLabel);

		this.searchStatusTextLabel = new Label(this.statusArea, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this.searchStatusTextLabel);
	}

	private void refreshSearch() {

		if (!this.projectProvider.hasProject()) {
			return;
		}

		// Trigger logon dialog if necessary
		if (!this.projectProvider.ensureLoggedOn()) {
//			updateStatus(IStatus.ERROR,
//				NLS.bind(Messages.ObjectSearch_ProjetLogonFailed_xmsg, this.projectProvider.getProjectName()));
			return;
		}

		final IObjectSearchQuery searchQuery = new ObjectSearchQuery(this.searchInput.getText(),
			(SearchType) this.searchTypeViewer.getStructuredSelection().getFirstElement(),
			this.projectProvider.getDestinationId());
		searchQuery.setAndSearchActice(this.andFilterOption);
		searchQuery.setCreateHistory(true);
		searchQuery.setReadApiState(true);
		SearchEngine.runObjectSearch(searchQuery);
	}

	/**
	 * Retrieves an image for the given status code
	 *
	 * @param  status
	 * @return        {@link Image}
	 */
	private Image getImageForStatus(final int status) {
		switch (status) {
		case IStatus.INFO:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		case IStatus.ERROR:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		case IStatus.OK:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		case IStatus.WARNING:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		default:
			return null;
		}
	}

	/**
	 * Loads project list into project combo
	 *
	 * @param  keepSelection pass <code>true</code> if the currently selected
	 *                       project should still be selected after refresh of the
	 *                       list
	 * @return               <code>true</code> if at least one project could be
	 *                       loaded
	 */
	private boolean loadProjectList(final boolean keepSelection) {
		if (this.projectViewer.getControl().isDisposed()) {
			return false;
		}
		IProject oldSelection = null;
		if (keepSelection) {
			oldSelection = (IProject) this.projectViewer.getStructuredSelection().getFirstElement();
		}
		final IProject[] abapProjects = AdtUtil.getABAPProjects();

		this.projectViewer.setInput(abapProjects);

		boolean selectFirst = false;
		if (keepSelection) {
			if (oldSelection == null && abapProjects.length > 0) {
				selectFirst = true;
			} else if (abapProjects != null && abapProjects.length > 0) {
				if (Arrays.asList(abapProjects).contains(oldSelection)) {
					this.projectViewer.setSelection(new StructuredSelection(oldSelection), true);
				} else {
					selectFirst = true;
				}
			}
		}

		if (selectFirst) {
			this.projectViewer.setSelection(new StructuredSelection(abapProjects[0]), true);
		}

		return abapProjects != null && abapProjects.length > 0;
	}

	/**
	 * Selects the first project in the project chooser dropdown
	 */
	private void selectFirstProject() {
		final IProject[] abapProjects = (IProject[]) this.projectViewer.getInput();
		if (abapProjects != null && abapProjects.length > 0) {
			final IProject project = abapProjects[0];
			this.projectViewer.setSelection(new StructuredSelection(project));
		}
	}

}
