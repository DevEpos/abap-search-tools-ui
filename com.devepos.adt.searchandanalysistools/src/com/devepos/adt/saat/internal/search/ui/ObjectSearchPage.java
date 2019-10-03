package com.devepos.adt.saat.internal.search.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.devepos.adt.saat.internal.search.contentassist.SearchPatternProvider;
import com.devepos.adt.saat.internal.search.model.SearchType;
import com.devepos.adt.saat.internal.util.AbapProjectProxy;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.StatusUtil;
import com.devepos.adt.saat.internal.util.TextControlUtil;
import com.sap.adt.tools.core.ui.AbapProjectProposalProvider;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;
import com.sap.adt.util.ui.SWTUtil;

public class ObjectSearchPage extends DialogPage implements ISearchPage {
	public static final String LAST_PROJECT_PREF = "com.devepos.adt.saat.objectsearch.lastSelectedProject";
	private static final int MULTIPLIER = 50;
	private static final int MAX_SCALE = 20;
	private static final int MIN_SCALE = 1;
	private static final int STATUS_PROJECT = 100;
	private static final int STATUS_PARAMETERS = 200;
	private ComboViewer searchTypeViewer;
	private Text searchInput;
	private Text parametersInput;
	private SearchPatternProvider searchPatternProvider;
	private final IAbapProjectProvider projectProvider;

	private final IPreferenceStore prefStore;
	private Composite mainComposite;
	private ISearchPageContainer pageContainer;
	private Composite statusArea;
	private Label searchStatusImageLabel;
	private Label searchStatusTextLabel;
	private Label maxResultsLabel;
	private boolean allResults;
	private int maxResults;
	private Text projectField;
	private IStatus currentStatus;
	private ObjectSearchUriDiscovery uriDiscovery;
	private Scale maxResultsScale;

	private final ObjectSearchRequest searchRequest;
	private Button andOptionCheck;

	public ObjectSearchPage() {
		this.prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
		this.prefStore.setDefault(LAST_PROJECT_PREF, ""); //$NON-NLS-1$
		this.projectProvider = new AbapProjectProxy(null);
		this.searchRequest = new ObjectSearchRequest();
		this.searchRequest.setProjectProvider(this.projectProvider);
		this.searchRequest.setReadApiState(true);
	}

	@Override
	public void createControl(final Composite parent) {
		initializeDialogUnits(parent);
		this.mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(3).spacing(10, 5).equalWidth(false).applyTo(this.mainComposite);
		setControl(this.mainComposite);

		createSearchTypeInput(this.mainComposite);
		createObjectNameInput(this.mainComposite);
		createParametersInput(this.mainComposite);
		createAndOptionCheckbox(this.mainComposite);
		createMaxResultsScale(this.mainComposite);
		createSeparator(this.mainComposite);
		createProjectInput(this.mainComposite);
		createStatusArea(parent);

		this.searchPatternProvider = new SearchPatternProvider(this.projectProvider,
			(SearchType) this.searchTypeViewer.getStructuredSelection().getFirstElement());
		this.searchPatternProvider.enableSearchTermInput(false);
		this.searchPatternProvider.addContentAssist(this.parametersInput);

		setInitialData();
		updateOKStatus();

		setFocus();
	}

	@Override
	public boolean performAction() {
		if (this.currentStatus != null && this.currentStatus.getSeverity() == IStatus.ERROR) {
			return false;
		}
		if (!isValidSearchData()) {
			return false;
		}
		// save current project in preferences
		this.prefStore.putValue(LAST_PROJECT_PREF, this.projectProvider.getProjectName());
		final ObjectSearchQuery query = new ObjectSearchQuery(this.searchRequest);

		NewSearchUI.runQueryInBackground(query);

		return true;
	}

	/**
	 * Sets control input from the given {@link ObjectSearchRequest}
	 *
	 * @param request the Object Search Request to be used
	 */
	public void setInputFromSearchRequest(final ObjectSearchRequest request) {
		final boolean doSetCursorToEnd = this.prefStore.getBoolean(IPreferences.CURSOR_AT_END_OF_SEARCH_INPUT);
		final IAbapProjectProvider projectProvider = request.getProjectProvider();
		final String searchTerm = request.getSearchTerm();
		this.searchInput.setText(searchTerm);
		if (projectProvider != null) {
			this.projectField.setText(projectProvider.getProjectName());
		}
		this.searchTypeViewer.setSelection(new StructuredSelection(request.getSearchType()));
		this.parametersInput.setText(request.getParametersString());
		this.maxResultsScale.setSelection(request.getMaxResults() / MULTIPLIER);
		this.maxResults = request.getMaxResults();
		this.andOptionCheck.setSelection(request.isAndSearchActive());
		updateMaxResults();

		if (doSetCursorToEnd) {
			this.searchInput.setSelection(searchTerm.length());
		} else {
			this.searchInput.selectAll();
		}
	}

	@Override
	public void setContainer(final ISearchPageContainer container) {
		this.pageContainer = container;
	}

	@Override
	public void dispose() {
		if (this.searchPatternProvider != null) {
			this.searchPatternProvider.dispose();
			this.searchPatternProvider = null;
		}

		super.dispose();
	}

	private void setInitialData() {
		// set the project
		setInitialProject();
		// set initial max result values
		final int maxResultPref = this.prefStore.getInt(IPreferences.MAX_SEARCH_RESULTS);
		if (maxResultPref > 0) {
			int maxResultScalePref = maxResultPref / MULTIPLIER;
			if (maxResultScalePref > MAX_SCALE) {
				maxResultScalePref = MAX_SCALE;
			}
			this.maxResultsScale.setSelection(maxResultScalePref);
			this.maxResults = maxResultPref;
		} else {
			this.maxResultsScale.setSelection(1);
			this.maxResults = 1 * MULTIPLIER;
		}
		updateMaxResults();
	}

	private void createSearchTypeInput(final Composite parent) {
		final Label typeComboLabel = new Label(this.mainComposite, SWT.NONE);
		GridDataFactory.fillDefaults().indent(SWT.DEFAULT, 5).applyTo(typeComboLabel);
		typeComboLabel.setText(Messages.ObjectSearch_SearchTypeInput_xfld);

		this.searchTypeViewer = new ComboViewer(this.mainComposite, SWT.READ_ONLY);
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
			this.searchRequest.setSearchType(selectedSearchType);
			validateParameterPattern();
		});
	}

	private void createObjectNameInput(final Composite parent) {
		final Label searchInputLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(searchInputLabel);
		searchInputLabel.setText("Object &Name:");

		this.searchInput = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).applyTo(this.searchInput);
		SWTUtil.addTextEditMenu(this.searchInput);
		this.searchInput.addModifyListener((event) -> {
			this.searchRequest.setSearchTerm(this.searchInput.getText());
			updateOKStatus();
		});
	}

	private void createParametersInput(final Composite parent) {
		final Label parametersLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(parametersLabel);
		parametersLabel.setText("Search &Filters:");

		this.parametersInput = new Text(parent, SWT.BORDER);
		TextControlUtil.addWordSupport(this.parametersInput);
		SWTUtil.addTextEditMenu(this.parametersInput);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).applyTo(this.parametersInput);

		this.parametersInput.addModifyListener((event) -> {
			validateParameterPattern();
		});
	}

	/**
	 * Validates the entered parameter pattern against the current object type and
	 * project
	 */
	private void validateParameterPattern() {
		if (this.searchPatternProvider != null && this.uriDiscovery != null) {
			final String parameterPattern = this.parametersInput.getText();
			try {
				this.searchPatternProvider.checkSearchParametersComplete(parameterPattern);
				this.searchRequest.setParameters(this.searchPatternProvider.getSearchParameters(parameterPattern),
					parameterPattern);
				validateAndSetStatus(new Status(IStatus.OK, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PARAMETERS, null, null));
			} catch (final CoreException e) {
				this.searchRequest.setParameters(null, "");
				validateAndSetStatus(
					new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PARAMETERS, e.getMessage(), e));
			}
			updateOKStatus();
		}
	}

	private void createAndOptionCheckbox(final Composite parent) {
		this.andOptionCheck = new Button(parent, SWT.CHECK);
		this.andOptionCheck.setText(Messages.ObjectSearch_UseAndFilter_xtol);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).span(3, 1).applyTo(this.andOptionCheck);

		this.andOptionCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				ObjectSearchPage.this.searchRequest.setAndSearchActice(ObjectSearchPage.this.andOptionCheck.getSelection());
			}
		});
	}

	private void createMaxResultsScale(final Composite parent) {
		final Label maxResultsLabel = new Label(parent, SWT.NONE);
		maxResultsLabel.setText("&Maximum number of results:");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(maxResultsLabel);

		this.maxResultsScale = new Scale(parent, SWT.HORIZONTAL);
		this.maxResultsScale.setIncrement(1);
		this.maxResultsScale.setMinimum(MIN_SCALE);
		this.maxResultsScale.setMaximum(MAX_SCALE);
		this.maxResultsScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				ObjectSearchPage.this.maxResults = ObjectSearchPage.this.maxResultsScale.getSelection() * MULTIPLIER;
				ObjectSearchPage.this.allResults = ObjectSearchPage.this.maxResultsScale.getSelection() == MAX_SCALE;
				updateMaxResults();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.maxResultsScale);

		this.maxResultsLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults()
			.align(SWT.LEAD, SWT.CENTER)
			.hint(convertHorizontalDLUsToPixels(50), SWT.DEFAULT)
			.applyTo(this.maxResultsLabel);
	}

	private void createSeparator(final Composite parent) {
		final Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setVisible(false);
		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 5).span(3, 1).grab(true, false).applyTo(separator);
	}

	private void createProjectInput(final Composite parent) {
		final Label projectInputLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(projectInputLabel);
		projectInputLabel.setText(Messages.ObjectSearch_ProjectInput_xfld);
		SWTUtil.setMandatory(projectInputLabel, true);

		this.projectField = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.projectField);
		// register project proposal provider
		new AbapProjectProposalProvider(this.projectField);
		SWTUtil.addTextEditMenu(this.projectField);

		final Button projectBrowseButton = new Button(parent, SWT.PUSH);
		projectBrowseButton.setText("&Browse...");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(projectBrowseButton);
		SWTUtil.setButtonWidthHint(projectBrowseButton);
		projectBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IProject project = ObjectSearchPage.this.projectProvider.getProject();
				final IProject resultProject = AbapProjectSelectionDialog.open(parent.getShell(), project);
				if (resultProject != null) {
					ObjectSearchPage.this.projectField.setText(resultProject.getName());
				}
			}
		});
		this.projectField.addModifyListener(e -> {
			final String projectName = this.projectField.getText();
			setProject(projectName);
			final IStatus projectStatus = validateProject(projectName);
			validateAndSetStatus(projectStatus);
			updateOKStatus();
		});

	}

	/*
	 * Updates the project provider with the given project name. If no project for
	 * the given name can be found the project of the provider will be set to null
	 */
	private void setProject(final String projectName) {
		if (projectName == null || "".equals(projectName)) {
			this.projectProvider.setProject(null);
		} else {
			// check if there is an ABAP project which matches the entered name
			final IProject[] abapProjects = AdtUtil.getAbapProjects();
			String availableProjectName = null;
			for (final IProject project : abapProjects) {
				if (project.getName().equalsIgnoreCase(projectName)) {
					availableProjectName = project.getName();
					break;
				}
			}
			if (availableProjectName != null) {
				this.projectProvider.setProject(ResourcesPlugin.getWorkspace().getRoot().getProject(availableProjectName));
			} else {
				this.projectProvider.setProject(null);
			}
		}

	}

	private void setInitialProject() {
		String projectName = null;
		final boolean useLastProject = this.prefStore.getBoolean(IPreferences.REMEMBER_LAST_PROJECT_IN_OBJ_EXPLORER);
		if (useLastProject) {
			projectName = this.prefStore.getString(LAST_PROJECT_PREF);
		}

		if (projectName == null || projectName.isEmpty()) {
			final IProject currentAbapProject = AdtUtil.getCurrentAbapProject();
			if (currentAbapProject != null) {
				projectName = currentAbapProject.getName();
			}
		}

		this.projectField.setText(projectName == null ? "" : projectName);
		if (projectName == null || projectName.isEmpty()) {
			this.prefStore.setToDefault(LAST_PROJECT_PREF);
		}
	}

	/*
	 * Validates the current project
	 */
	private IStatus validateProject(final String projectName) {
		if (projectName != null && !projectName.isEmpty()) {
			if (this.projectProvider.hasProject()) {
				// check if user is logged on to project
				if (!this.projectProvider.ensureLoggedOn()) {
					return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PROJECT,
						NLS.bind(Messages.ObjectSearch_ProjectLogonFailed_xmsg, this.projectProvider.getProjectName()), null);
				}
				// check if the project supports the object search
				this.uriDiscovery = new ObjectSearchUriDiscovery(this.projectProvider.getDestinationId());
				if (this.uriDiscovery.getObjectSearchUri() == null) {
					this.uriDiscovery = null;
					return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PROJECT,
						NLS.bind(Messages.ObjectSearch_SearchNotSupportedInProject_xmsg, this.projectProvider.getProjectName()),
						null);
				}
				return new Status(IStatus.OK, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PROJECT, null, null);
			} else {
				// project does not exist
				return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PROJECT, "Project does not exist",
					null);
			}
		}
		// no project name entered
		return new Status(IStatus.OK, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PROJECT, null, null);
	}

	private void createStatusArea(final Composite parent) {
		this.statusArea = new Composite(parent, SWT.NONE);

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(this.statusArea);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this.statusArea);

		this.searchStatusImageLabel = new Label(this.statusArea, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(this.searchStatusImageLabel);

		this.searchStatusTextLabel = new Label(this.statusArea, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this.searchStatusTextLabel);
	}

	private boolean isValidSearchData() {
		if (this.searchRequest.getQuery() == null || this.searchRequest.getQuery().isEmpty()) {
			return false;
		}

		if (!this.projectProvider.hasProject()) {
			return false;
		}

		return true;
	}

	private void setStatus(final IStatus status) {
		this.currentStatus = status;
		Display.getDefault().asyncExec(() -> {
			if (this.mainComposite.isDisposed() || this.searchStatusImageLabel == null || this.searchStatusTextLabel == null) {
				return;
			}
			if (status.getSeverity() == IStatus.OK) {
				this.searchStatusImageLabel.setImage(null);
				this.searchStatusTextLabel.setText("");
			} else {
				this.searchStatusImageLabel.setImage(StatusUtil.getImageForStatus(status.getSeverity()));
				this.searchStatusTextLabel.setText(status.getMessage());
				this.searchStatusTextLabel.setToolTipText(status.getMessage());
				this.searchStatusTextLabel.pack(true);
				this.searchStatusTextLabel.getParent().layout(true);
				getShell().pack(true);
			}
		});
	}

	private void updateOKStatus() {
		Display.getDefault().asyncExec(() -> {
			if (getControl().isDisposed()) {
				return;
			}
			boolean isError = false;
			if (this.currentStatus != null) {
				isError = IStatus.ERROR == this.currentStatus.getSeverity();
			}
			this.pageContainer.setPerformActionEnabled(isValidSearchData() && !isError);
		});
	}

	private void validateAndSetStatus(final IStatus status) {
		final IStatus validatedStatus = validateStatus(status);
		setStatus(validatedStatus);
	}

	private IStatus validateStatus(IStatus status) {
		if (status == null) {
			status = Status.OK_STATUS;
		}
		if (this.currentStatus == null) {
			return status;
		}
		if (status.getCode() == this.currentStatus.getCode()) {
			return status;
		}
		if (status.getSeverity() > this.currentStatus.getSeverity()) {
			return status;
		}
		return this.currentStatus;
	}

	private void updateMaxResults() {
		if (this.allResults) {
			this.maxResultsLabel.setText("All Results");
			this.searchRequest.setReadAllEntries(true);
		} else {
			this.searchRequest.setReadAllEntries(false);
			this.searchRequest.setMaxResults(this.maxResults);
			this.maxResultsLabel.setText(NLS.bind("{0} Results", this.maxResults));
		}
	}

	private void setFocus() {
		if (this.searchInput != null && !this.searchInput.isDisposed()) {
			this.searchInput.setFocus();
		}
	}
}