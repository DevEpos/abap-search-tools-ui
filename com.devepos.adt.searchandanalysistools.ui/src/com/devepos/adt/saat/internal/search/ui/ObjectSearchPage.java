package com.devepos.adt.saat.internal.search.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.help.HelpContexts;
import com.devepos.adt.saat.internal.help.HelpUtil;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.devepos.adt.saat.internal.search.ObjectSearchUriDiscovery;
import com.devepos.adt.saat.internal.search.SearchType;
import com.devepos.adt.saat.internal.search.contentassist.SearchPatternProvider;
import com.devepos.adt.tools.base.ui.project.AbapProjectProxy;
import com.devepos.adt.tools.base.ui.project.IAbapProjectProvider;
import com.devepos.adt.tools.base.ui.project.ProjectUtil;
import com.devepos.adt.tools.base.ui.util.SelectionUtil;
import com.devepos.adt.tools.base.ui.util.StatusUtil;
import com.devepos.adt.tools.base.ui.util.TextControlUtil;
import com.sap.adt.tools.core.ui.AbapProjectProposalProvider;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;
import com.sap.adt.util.ui.SWTUtil;

/**
 * Describes the Page in the Search Dialog for the extended ABAP Object Search
 *
 * @author stockbal
 */
public class ObjectSearchPage extends DialogPage implements ISearchPage {
	public static final String LAST_PROJECT_PREF = "com.devepos.adt.saat.objectsearch.lastSelectedProject"; //$NON-NLS-1$

	private static final int MULTIPLIER = 50;
	private static final int BIG_MULTIPLIER = 500;
	private static final int SMALL_SCALE_LIMIT = 20;
	private static final int MAX_SCALE = 22;
	private static final int MIN_SCALE = 1;
	private static final int SMALL_SCALE_UPPER_BOUND = MULTIPLIER * SMALL_SCALE_LIMIT;
	private static final int MAX_RESULTS_UPPER_BOUND = SMALL_SCALE_UPPER_BOUND + (MAX_SCALE - SMALL_SCALE_LIMIT) * BIG_MULTIPLIER;
	private static final int STATUS_PROJECT = 100;
	private static final int STATUS_PARAMETERS = 200;
	private static final int STATUS_SEARCH_TYPE = 300;
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
	private int maxResults;
	private Text projectField;
	private IStatus currentStatus;
	private ObjectSearchUriDiscovery uriDiscovery;
	private Scale maxResultsScale;

	private final ObjectSearchRequest searchRequest;
	private ObjectSearchQuery previousQuery;
	private Button andOptionCheck;

	public ObjectSearchPage() {
		this.prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
		this.prefStore.setDefault(LAST_PROJECT_PREF, ""); //$NON-NLS-1$
		this.projectProvider = new AbapProjectProxy(null);
		this.searchRequest = new ObjectSearchRequest();
		this.searchRequest.setProjectProvider(this.projectProvider);
		this.searchRequest.setReadApiState(true);
		this.searchRequest.setReadPackageHierarchy(true);
	}

	@Override
	public void createControl(final Composite parent) {
		initializeDialogUnits(parent);
		this.mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(3).spacing(10, 5).equalWidth(false).applyTo(this.mainComposite);
		setControl(this.mainComposite);

		HelpUtil.setHelp(this.mainComposite, HelpContexts.OBJECT_SEARCH);

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
		ObjectSearchQuery query = null;
		if (this.previousQuery != null) {
			query = this.previousQuery;
			query.setSearchRequest(this.searchRequest);
		} else {
			query = new ObjectSearchQuery(this.searchRequest);
		}

		NewSearchUI.runQueryInBackground(query);

		return true;
	}

	/**
	 * Sets the search type drop down to the given search type
	 *
	 * @param searchType the search type to be selected in the dialog
	 */
	public void setSearchType(final SearchType searchType) {
		if (this.searchTypeViewer == null || this.searchTypeViewer.getControl().isDisposed()) {
			return;
		}

		this.searchTypeViewer.setSelection(new StructuredSelection(searchType));
	}

	/**
	 * Sets control input from the given {@link ObjectSearchRequest}
	 *
	 * @param request the Object Search Request to be used
	 */
	public void setInputFromSearchQuery(final ObjectSearchQuery query) {
		final ObjectSearchRequest request = query.getSearchRequest();
		final boolean doSetCursorToEnd = this.prefStore.getBoolean(IPreferences.CURSOR_AT_END_OF_SEARCH_INPUT);
		final IAbapProjectProvider projectProvider = request.getProjectProvider();
		final String searchTerm = request.getSearchTerm();
		this.searchInput.setText(searchTerm);
		if (projectProvider != null) {
			this.projectField.setText(projectProvider.getProjectName());
		}
		this.searchTypeViewer.setSelection(new StructuredSelection(request.getSearchType()));
		final String parametersString = request.getParametersString();
		this.parametersInput.setText(parametersString);
		updateMaxResultsScaleFromNumber(request.getMaxResults());
		this.andOptionCheck.setSelection(request.isAndSearchActive());
		this.searchRequest.setAndSearchActive(request.isAndSearchActive());
		updateMaxResults();

		if (doSetCursorToEnd) {
			this.searchInput.setSelection(searchTerm.length());
			this.parametersInput.setSelection(parametersString.length());
		} else {
			this.searchInput.selectAll();
			this.parametersInput.selectAll();
		}

		// use previous query if overwrite preference is true
		if (this.prefStore.getBoolean(IPreferences.OVERWRITE_OPENED_SEARCH_QUERY)) {
			this.previousQuery = query;
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

		// set initial search type
		final String defaultSearchTypeId = this.prefStore.getString(IPreferences.DEFAULT_SEARCH_TYPE);

		try {
			final SearchType defaultSearchType = SearchType.valueOf(defaultSearchTypeId);
			this.searchTypeViewer.setSelection(new StructuredSelection(defaultSearchType));
		} catch (final IllegalArgumentException e) {
		}
		// set initial max result values
		updateMaxResultsScaleFromNumber(this.prefStore.getInt(IPreferences.MAX_SEARCH_RESULTS));
		updateMaxResults();

		if (this.prefStore.getBoolean(IPreferences.TAKE_TEXT_SELECTION_INTO_SEARCH)) {
			final ISelection selection = SelectionUtil.getSelection();
			if (selection != null && selection instanceof ITextSelection) {
				final String selectedText = ((ITextSelection) selection).getText();
				this.searchInput.setText(selectedText);
			}
		}
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
			// check if the selected search type is available in the selected project
			this.searchPatternProvider.setSearchType(selectedSearchType);
			this.searchRequest.setSearchType(selectedSearchType);
			if (validateAndSetStatus(validateSearchType(selectedSearchType))) {
				validateParameterPattern();
			}
			updateOKStatus();
		});
	}

	private IStatus validateSearchType(final SearchType selectedSearchType) {
		if (this.uriDiscovery != null && this.projectProvider != null && this.projectProvider.hasProject()) {
			if (this.uriDiscovery.getObjectSearchTemplate(selectedSearchType) == null) {
				return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_SEARCH_TYPE,
					NLS.bind(Messages.ObjectSearch_SearchTypeNotSupported_xmsg, selectedSearchType,
						this.projectProvider.getProjectName()),
					null);
			}
		}
		return new Status(IStatus.OK, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_SEARCH_TYPE, null, null);
	}

	private void createObjectNameInput(final Composite parent) {
		final Label searchInputLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(searchInputLabel);
		searchInputLabel.setText(Messages.ObjectSearch_ObjectNameInput_xfld);

		this.searchInput = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).applyTo(this.searchInput);
		SWTUtil.addTextEditMenu(this.searchInput);
		this.searchInput.addModifyListener(e -> {
			ObjectSearchPage.this.searchRequest.setSearchTerm(ObjectSearchPage.this.searchInput.getText().trim());
			updateOKStatus();
		});

		final ControlDecoration decorator = new ControlDecoration(this.searchInput, 16512);
		final Image decoratorImage = FieldDecorationRegistry.getDefault().getFieldDecoration("DEC_INFORMATION").getImage();
		decorator.setMarginWidth(2);
		decorator.setImage(decoratorImage);
		final StringBuilder decoratorText = new StringBuilder();
		final String newLine = System.lineSeparator();

		decoratorText.append(Messages.ObjectSearch_InfoSearchString_xmsg);
		decoratorText.append(newLine);
		decoratorText.append(newLine);
		decoratorText.append(NLS.bind(Messages.ObjectSearch_InfoSearchString_Asterisk_xmsg, "*")); //$NON-NLS-1$
		decoratorText.append(newLine);
		decoratorText.append(NLS.bind(Messages.ObjectSearch_InfoSearchString_Question_xmsg, "?")); //$NON-NLS-1$
		decoratorText.append(newLine);
		decoratorText.append(NLS.bind(Messages.ObjectSearch_InfoSearchString_LessThan_xmsg, "<")); //$NON-NLS-1$
		decoratorText.append(newLine);
		decoratorText.append(NLS.bind(Messages.ObjectSearch_InfoSearchString_Negation_xmsg, "!")); //$NON-NLS-1$
		decoratorText.append(newLine);
		decoratorText.append(newLine);
		decoratorText.append(Messages.ObjectSearch_InfoSearchString_Notes_xmsg);
		decorator.setDescriptionText(decoratorText.toString());
	}

	private void createParametersInput(final Composite parent) {
		final Label parametersLabel = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(parametersLabel);
		parametersLabel.setText(Messages.ObjectSearch_SearchFiltersInput_xfld);

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
				this.searchRequest.setParameters(null, ""); //$NON-NLS-1$
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
				ObjectSearchPage.this.searchRequest.setAndSearchActive(ObjectSearchPage.this.andOptionCheck.getSelection());
			}
		});
	}

	private void createMaxResultsScale(final Composite parent) {
		final Label maxResultsLabel = new Label(parent, SWT.NONE);
		maxResultsLabel.setText(Messages.ObjectSearch_MaxNumberOfResultsScale_xfld);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(maxResultsLabel);

		this.maxResultsScale = new Scale(parent, SWT.HORIZONTAL);
		this.maxResultsScale.setIncrement(1);
		this.maxResultsScale.setMinimum(MIN_SCALE);
		this.maxResultsScale.setMaximum(MAX_SCALE);
		this.maxResultsScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				calculateMaxResultsByScale(ObjectSearchPage.this.maxResultsScale.getSelection());
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
		projectBrowseButton.setText(Messages.ObjectSearch_ProjectBrowseButton_xbut);
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

			if (validateAndSetStatus(validateProject(projectName))) {
				if (validateAndSetStatus(validateSearchType(this.searchRequest.getSearchType()))) {
					validateParameterPattern();
				}
			}

			updateOKStatus();
		});

	}

	/*
	 * Updates the project provider with the given project name. If no project for
	 * the given name can be found the project of the provider will be set to null
	 */
	private void setProject(final String projectName) {
		if (projectName == null || "".equals(projectName)) { //$NON-NLS-1$
			this.projectProvider.setProject(null);
		} else {
			// check if there is an ABAP project which matches the entered name
			final IProject[] abapProjects = ProjectUtil.getAbapProjects();
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

		final IProject currentAbapProject = ProjectUtil.getCurrentAbapProject();
		if (currentAbapProject != null) {
			projectName = currentAbapProject.getName();
		}
		if (projectName == null || projectName.isEmpty()) {
			projectName = this.prefStore.getString(LAST_PROJECT_PREF);
		}

		this.projectField.setText(projectName == null ? "" : projectName); //$NON-NLS-1$
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
				return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, STATUS_PROJECT,
					Messages.ObjectSearch_ProjectDoesNotExistError_xmsg, null);
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
				this.searchStatusTextLabel.setText(""); //$NON-NLS-1$
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

	private boolean validateAndSetStatus(final IStatus status) {
		final IStatus validatedStatus = validateStatus(status);
		setStatus(validatedStatus);
		return validatedStatus == null || validatedStatus.isOK();
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
		return status;
	}

	private void updateMaxResults() {
		this.searchRequest.setReadAllEntries(false);
		this.searchRequest.setMaxResults(this.maxResults);
		this.maxResultsLabel.setText(NLS.bind(Messages.ObjectSearch_FoundResultsLabel_xmsg, this.maxResults));
	}

	private void updateMaxResultsScaleFromNumber(final int maxResults) {
		if (maxResults > 0) {
			if (maxResults >= MAX_RESULTS_UPPER_BOUND) {
				this.maxResults = MAX_RESULTS_UPPER_BOUND;
				this.maxResultsScale.setSelection(MAX_SCALE);
			} else if (maxResults >= SMALL_SCALE_UPPER_BOUND && maxResults < MAX_RESULTS_UPPER_BOUND) {
				this.maxResultsScale.setSelection(SMALL_SCALE_LIMIT + (maxResults - SMALL_SCALE_UPPER_BOUND) / BIG_MULTIPLIER);
				this.maxResults = maxResults;
			} else {
				int maxResultScalePref = maxResults / MULTIPLIER;
				if (maxResultScalePref > MAX_SCALE) {
					maxResultScalePref = MAX_SCALE;
				}
				this.maxResultsScale.setSelection(maxResultScalePref);
				this.maxResults = maxResults;
			}
		} else {
			this.maxResultsScale.setSelection(1);
			this.maxResults = 1 * MULTIPLIER;
		}
	}

	private void calculateMaxResultsByScale(final int selectedScale) {
		if (selectedScale >= SMALL_SCALE_LIMIT) {
			this.maxResults = SMALL_SCALE_UPPER_BOUND;
			this.maxResults += (selectedScale - SMALL_SCALE_LIMIT) * BIG_MULTIPLIER;
		} else {
			this.maxResults = selectedScale * MULTIPLIER;
		}
	}

	/**
	 * Sets focus to first input field
	 */
	public void setFocusToFirstInput() {
		if (this.searchInput != null && !this.searchInput.isDisposed()) {
			this.searchInput.setFocus();
		}
	}

	private void setFocus() {
		final boolean focusOnSearchType = this.prefStore.getBoolean(IPreferences.FOCUS_ON_SEARCH_TYPE);

		if (focusOnSearchType) {
			if (this.searchTypeViewer != null && !this.searchTypeViewer.getControl().isDisposed()) {
				this.searchTypeViewer.getControl().setFocus();
			}
		} else {
			setFocusToFirstInput();
		}

	}

}