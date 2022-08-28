package com.devepos.adt.saat.internal.cdsanalysis.ui;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.devepos.adt.base.IAdtObjectTypeConstants;
import com.devepos.adt.base.destinations.DestinationUtil;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.base.ui.project.ProjectInput;
import com.devepos.adt.base.ui.project.ProjectUtil;
import com.devepos.adt.base.ui.search.AdtRisSearchUtil;
import com.devepos.adt.base.ui.search.IAdtRisSearchResultProxy;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisUriDiscovery;
import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.util.ui.swt.AdtSWTUtilFactory;
import com.sap.adt.util.ui.swt.IAdtSWTUtil;

/**
 * Dialog to select an ADT object for which to run a CDS analysis
 *
 * @author Ludwig Stockbauer-Muhr
 *
 */
public class RunNewCdsAnalysisDialog extends StatusDialog {

  private static final int RUN_IN_NEW_BUTTON = IDialogConstants.CLIENT_ID + 1;

  private ProjectInput projectInput;
  private Button runButton;
  private Button runInNewButton;
  private Combo typeCombo;
  private Button browseObjectsButton;
  private Text selectObjectText;

  private IAbapProjectProvider projectProvider;
  private CdsAnalysisUriDiscovery uriDiscovery;
  private final List<CdsAnalysisType> validTypesForObject;
  private final List<CdsAnalysisType> validTypesForProject;

  private boolean isRunInNew;
  private IAdtObjectReference selectedObject;
  private CdsAnalysisType selectedAnalysisType;

  /**
   * Creates dialog to run new CDS analysis for a given ADT object
   * 
   * @param parent the parent shell for the dialog
   */
  public RunNewCdsAnalysisDialog(final Shell parent) {
    super(parent);
    setTitle(Messages.RunNewCdsAnalysisDialog_dialog_xtit);
    validTypesForObject = new ArrayList<>();
    validTypesForProject = new ArrayList<>();
  }

  public IProject getProject() {
    return projectProvider.getProject();
  }

  public CdsAnalysisType getSelectedAnalysisType() {
    return selectedAnalysisType;
  }

  public IAdtObjectReference getSelectedObject() {
    return selectedObject;
  }

  public boolean isRunInNew() {
    return isRunInNew;
  }

  @Override
  protected void buttonPressed(int buttonId) {
    if (buttonId == RUN_IN_NEW_BUTTON) {
      isRunInNew = true;
      buttonId = IDialogConstants.OK_ID;
    }
    super.buttonPressed(buttonId);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    runButton = createButton(parent, IDialogConstants.OK_ID,
        Messages.RunNewCdsAnalysisDialog_run_xbtn, true);
    runInNewButton = createButton(parent, RUN_IN_NEW_BUTTON,
        Messages.RunNewCdsAnalysisDialog_runInNew_xbt, false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  @Override
  protected Control createContents(final Composite parent) {
    Control contents = super.createContents(parent);

    IProject project = ProjectUtil.getCurrentAbapProject();
    if (project != null) {
      projectInput.setProjectName(project.getName());
    }
    return contents;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite dialogArea = new Composite(parent, SWT.NONE);
    GridLayoutFactory.swtDefaults()
        .margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN),
            convertHorizontalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
        .numColumns(3)
        .applyTo(dialogArea);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(dialogArea);

    createProjectInput(dialogArea);
    createSelectObjectInput(dialogArea);
    createCdsAnalysisCommandControls(dialogArea);

    applyDialogFont(dialogArea);
    return dialogArea;
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings() {
    return SearchAndAnalysisPlugin.getDefault()
        .getDialogSettingsSection("DialogBounds_" + RunNewCdsAnalysisDialog.class.getSimpleName()); //$NON-NLS-1$
  }

  @Override
  protected int getDialogBoundsStrategy() {
    return DIALOG_PERSISTSIZE;
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected void updateButtonsEnableState(final IStatus status) {
    if (runButton == null || runButton.isDisposed()) {
      return;
    }
    boolean enabledByStatus = (status == null || !status.matches(IStatus.ERROR))
        && selectedObject != null && selectedAnalysisType != null;
    runButton.setEnabled(enabledByStatus);
    runInNewButton.setEnabled(enabledByStatus);
  }

  private void createCdsAnalysisCommandControls(final Composite parent) {
    final Label analysisTypeComboLabel = new Label(parent, SWT.NONE);
    GridDataFactory.fillDefaults().indent(SWT.DEFAULT, 5).applyTo(analysisTypeComboLabel);
    analysisTypeComboLabel.setText(Messages.RunNewCdsAnalysisDialog_analysisType_xlbl);
    AdtSWTUtilFactory.getOrCreateSWTUtil().setMandatory(analysisTypeComboLabel, true);

    typeCombo = new Combo(parent, SWT.READ_ONLY);
    GridDataFactory.fillDefaults()
        .align(SWT.BEGINNING, SWT.CENTER)
        .grab(true, false)
        .hint(convertWidthInCharsToPixels(30), SWT.DEFAULT)
        .span(2, 1)
        .applyTo(typeCombo);
    typeCombo.addSelectionListener(widgetSelectedAdapter(l -> {
      selectedAnalysisType = validTypesForObject.get(typeCombo.getSelectionIndex());
      updateStatus(null);
    }));
  }

  private void createProjectInput(final Composite root) {
    projectInput = new ProjectInput(true);
    projectProvider = projectInput.getProjectProvider();
    projectInput.setUseDedicatedComposite(false);
    projectInput.createControl(root);
    projectInput.addProjectValidator(project -> {
      uriDiscovery = new CdsAnalysisUriDiscovery(DestinationUtil.getDestinationId(project));
      if (!uriDiscovery.isResourceDiscoverySuccessful() || uriDiscovery
          .getCdsAnalysisUri() == null) {
        return new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID, NLS.bind(
            Messages.CdsAnalysis_FeatureIsNotSupported_xmsg, new Object[] { project.getName() }));
      }
      return Status.OK_STATUS;
    });
    projectInput.addStatusChangeListener(status -> {
      if (status == null || status.isOK()) {
        updateStatus(new Status(IStatus.INFO, SearchAndAnalysisPlugin.PLUGIN_ID,
            Messages.RunNewCdsAnalysisDialog_noObjectSelectedStatus_xmsg));
      } else {
        updateStatus(status != null && status.isOK() ? null : status);
      }
    });
    projectProvider.addPropertyChangeListener(l -> {
      validTypesForObject.clear();
      validTypesForProject.clear();
      selectedObject = null;
      selectObjectText.setText(""); //$NON-NLS-1$
      typeCombo.removeAll();
      boolean hasProject = l.getNewValue() != null;
      if (hasProject) {
        fillValidAnalysisTypesForProject((IProject) l.getNewValue());
      }
      browseObjectsButton.setEnabled(hasProject);
    });
  }

  private void createSelectObjectInput(final Composite parent) {
    IAdtSWTUtil swtUtil = AdtSWTUtilFactory.getOrCreateSWTUtil();
    Label selectObject = new Label(parent, SWT.NONE);
    selectObject.setText(Messages.RunNewCdsAnalysisDialog_selectedObject_xlbl);
    swtUtil.setMandatory(selectObject, true);

    selectObjectText = new Text(parent, SWT.BORDER);
    GridDataFactory.fillDefaults()
        .align(SWT.FILL, SWT.CENTER)
        .grab(true, false)
        .hint(convertWidthInCharsToPixels(40), SWT.DEFAULT)
        .applyTo(selectObjectText);
    selectObjectText.setEditable(false);

    browseObjectsButton = new Button(parent, SWT.PUSH);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(browseObjectsButton);
    swtUtil.setButtonWidthHint(browseObjectsButton);
    browseObjectsButton.setText(Messages.RunNewCdsAnalysisDialog_selectObject_xbtn);

    browseObjectsButton.addSelectionListener(widgetSelectedAdapter(e -> {
      final IAdtRisSearchResultProxy result = AdtRisSearchUtil.searchAdtObjectViaDialog(parent
          .getShell(), Messages.RunNewCdsAnalysisDialog_selectObjectDialog_xtit, this.getClass()
              .getCanonicalName() + ".dialog", false, Arrays.asList(
                  IAdtObjectTypeConstants.DATA_DEFINITION,
                  IAdtObjectTypeConstants.TABLE_DEFINITION_TYPE,
                  IAdtObjectTypeConstants.VIEW_DEFINITION_TYPE), projectProvider.getProject());
      if (result == null) {
        return;
      }

      selectedObject = result.getFirstResult();
      selectObjectText.setText(selectedObject.getName());

      if (!IAdtObjectTypeConstants.DATA_DEFINITION.equals(selectedObject.getType())) {
        validTypesForObject.clear();
        validTypesForObject.add(CdsAnalysisType.WHERE_USED);
        validTypesForObject.add(CdsAnalysisType.FIELD_ANALYSIS);
      } else {
        validTypesForObject.addAll(validTypesForProject);
      }

      typeCombo.removeAll();
      for (CdsAnalysisType type : validTypesForObject) {
        typeCombo.add(type.toString());
      }
      typeCombo.select(0);
      selectedAnalysisType = validTypesForObject.get(0);
      updateStatus(null);
    }));
  }

  private void fillValidAnalysisTypesForProject(final IProject project) {
    validTypesForProject.add(CdsAnalysisType.TOP_DOWN);
    validTypesForProject.add(CdsAnalysisType.WHERE_USED);
    if (uriDiscovery.isUsedEntitiesAnalysisAvailable()) {
      validTypesForProject.add(CdsAnalysisType.DEPENDENCY_TREE_USAGES);
    }
    validTypesForProject.add(CdsAnalysisType.FIELD_ANALYSIS);
  }

}
