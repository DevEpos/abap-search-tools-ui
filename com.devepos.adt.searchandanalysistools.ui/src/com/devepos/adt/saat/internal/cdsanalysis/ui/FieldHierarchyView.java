package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.part.PageBook;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.ui.IGeneralMenuConstants;
import com.devepos.adt.base.ui.action.ActionFactory;
import com.devepos.adt.base.ui.action.RadioActionGroup;
import com.devepos.adt.base.ui.tree.IAdtObjectReferenceNode;
import com.devepos.adt.base.ui.tree.ITreeNode;
import com.devepos.adt.base.ui.tree.LazyLoadingFolderNode;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsFieldTopDownElementInfoProvider;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsAnalysisConstants;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsFieldAnalysisSettings;
import com.devepos.adt.saat.internal.menu.SaatMenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.internal.util.NavigationUtil;

/**
 * View which consists of a {@link ViewForm} for the lower section of the
 * {@link FieldAnalysisView}
 *
 * @author stockbal
 */
public class FieldHierarchyView implements IDestinationProvider {
  static final String DIRECTION = "DIRECTION"; //$NON-NLS-1$
  static final String TOP_DOWN_ACTION = "topDown"; //$NON-NLS-1$
  static final String WHERE_USED_ACTION = "whereUsed"; //$NON-NLS-1$

  private RadioActionGroup actionToggleGroup;
  private Action searchCalcFieldsAction;
  private final FieldAnalysisView parentView;
  private final ViewForm hierarchyViewerViewForm;
  private final PageBook pageBook;
  private final Composite noFieldSelectionComposite;
  private final CLabel hierarchyViewerPaneLabel;
  private FieldHierarchyViewer hierarchyTreeViewer;

  private ITreeNode fieldNode;
  private FieldHierarchyViewerInput currentFieldInput;
  private Map<String, FieldHierarchyViewerInput> fieldInputMap;
  private ObjectType currentInputObjectType;
  private String currentEntityName;

  private IDestinationProvider destinationProvider;
  private ICdsFieldAnalysisSettings settings;

  /**
   * Creates new instance of the {@link FieldHierarchyView}
   */
  public FieldHierarchyView(final FieldAnalysisView parentView, final Composite parent) {
    this.parentView = parentView;
    fieldInputMap = new HashMap<>();
    pageBook = new PageBook(parent, SWT.NONE);
    noFieldSelectionComposite = createNoFieldSelectionComposite(pageBook);
    hierarchyViewerViewForm = new ViewForm(pageBook, SWT.NONE);
    final Control hierarchyViewerControl = createHierarchyViewerControl(hierarchyViewerViewForm);
    configureFieldHierarchyTree(hierarchyTreeViewer);
    hierarchyViewerViewForm.setContent(hierarchyViewerControl);

    hierarchyViewerPaneLabel = new CLabel(hierarchyViewerViewForm, SWT.NONE);
    hierarchyViewerViewForm.setTopLeft(hierarchyViewerPaneLabel);

    final ToolBar toolbar = new ToolBar(hierarchyViewerViewForm, SWT.FLAT | SWT.WRAP);
    hierarchyViewerViewForm.setTopCenter(toolbar);
    final ToolBarManager fieldTbm = new ToolBarManager(toolbar);

    createToolbarActions();
    fillToolbar(fieldTbm);

    fieldTbm.update(true);

    pageBook.showPage(noFieldSelectionComposite);
  }

  /**
   * Clears the cached nodes
   */
  public void clearInputCache() {
    fieldInputMap.clear();
  }

  @Override
  public String getDestinationId() {
    return destinationProvider.getDestinationId();
  }

  /**
   * Returns the current input cache
   *
   * @return the current input cache
   */
  public Map<String, FieldHierarchyViewerInput> getInputCache() {
    return fieldInputMap;
  }

  @Override
  public String getSystemId() {
    return destinationProvider.getSystemId();
  }

  public StructuredViewer getViewer() {
    return hierarchyTreeViewer;
  }

  /**
   * Returns <code>true</code> if this view is visible
   *
   * @return <code>true</code> if this view is visible
   */
  public boolean isVisible() {
    return hierarchyViewerViewForm.isVisible();
  }

  public void reloadFieldInput() {
    hierarchyTreeViewer.reloadInput(TOP_DOWN_ACTION.equals(actionToggleGroup.getToggledActionId()));
  }

  @Override
  public void setDestinationId(final String destinationId) {
    destinationProvider.setDestinationId(destinationId);
  }

  /**
   * Sets the entity information whose fields should be analyzed
   *
   * @param entityName    the name of a database entity
   * @param destinationId the destination id of the ABAP project
   * @param entityType    the type of the database entity
   */
  public void setEntityInformation(final String entityName,
      final IDestinationProvider destinationProvider, final ObjectType entityType) {
    currentInputObjectType = entityType;
    currentEntityName = entityName;
    this.destinationProvider = destinationProvider;
  }

  /**
   * Updates the input of the tree viewer for a single field
   *
   * @param node the newly chosen field
   */
  public void setFieldHierarchyInput(final ITreeNode node) {
    fieldNode = node;
    final String fieldName = node.getName();
    FieldHierarchyViewerInput input = fieldInputMap.get(fieldName);
    if (input == null) {
      // check if top down is possible
      // create new input
      LazyLoadingFolderNode topDownNode = null;
      if (parentView.uriDiscovery.isHierarchyAnalysisAvailable()
          && currentInputObjectType == ObjectType.DATA_DEFINITION) {
        topDownNode = new LazyLoadingFolderNode(currentEntityName, currentEntityName,
            new CdsFieldTopDownElementInfoProvider(getDestinationId(), currentEntityName,
                fieldName), node.getParent().getImage(), null, null);
        topDownNode.getProperties().put(ICdsAnalysisConstants.FIELD_PROP, node.getDisplayName());
      }
      input = new FieldHierarchyViewerInput(hierarchyTreeViewer, topDownNode, currentEntityName,
          fieldName, this);
      input.createWhereUsedNode(settings);

      fieldInputMap.put(fieldName, input);
    }
    currentFieldInput = input;

    boolean isTopDownPossible = input.getTopDownNode().hasContent();
    boolean isTopDown = settings.isTopDown();
    if (!isTopDownPossible && isTopDown) {
      settings.setTopDown(false);
      isTopDown = false;
    }

    searchCalcFieldsAction.setChecked(settings.isSearchInCalcFields());
    actionToggleGroup.enableAction(TOP_DOWN_ACTION, isTopDownPossible);
    actionToggleGroup.setActionChecked(isTopDown ? TOP_DOWN_ACTION : WHERE_USED_ACTION);
    hierarchyTreeViewer.setInput(input, isTopDown);
    searchCalcFieldsAction.setEnabled(!isTopDown);

    updateToolbarLabel(isTopDown);
  }

  /**
   * Updates the field to hierarchy viewer input cache
   *
   * @param inputCache the field hierarchy viewer cache
   */
  public void setInputCache(final Map<String, FieldHierarchyViewerInput> inputCache) {
    if (inputCache == null) {
      fieldInputMap = new HashMap<>();
    } else {
      fieldInputMap = inputCache;
    }
  }

  public void setSettings(ICdsFieldAnalysisSettings settings) {
    this.settings = settings;
  }

  /**
   * Sets the visibility of the view form which holds the hierarchy viewer
   *
   * @param visible if <code>true</code> the view will be made visible
   */
  public void setVisible(final boolean visible) {
    if (visible) {
      pageBook.showPage(hierarchyViewerViewForm);
    } else {
      pageBook.showPage(noFieldSelectionComposite);
    }
  }

  /*
   * Perform some configuration tasks on the fields hierarchy viewer
   */
  private void configureFieldHierarchyTree(final FieldHierarchyViewer hierarchyTreeViewer) {
    hierarchyTreeViewer.initContextMenu(menu -> {
      parentView.fillContextMenu(menu);
      if (menu.find(ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS) != null) {
        SaatMenuItemFactory.addCdsAnalyzerCommandItem(menu,
            IContextMenuConstants.GROUP_CDS_ANALYSIS, ICommandConstants.FIELD_ANALYSIS);
      }
      contributeToHierarchyViewerContextMenu(menu);
    }, null, null);
    // register field navigation as double click and ENTER action
    hierarchyTreeViewer.addDoubleClickListener(event -> {
      final IAdtObjectReferenceNode adtObjRefNode = getAdtObjRefFromSelection();
      if (adtObjRefNode == null) {
        return;
      }
      final String entityName = adtObjRefNode.getDisplayName();
      final String fieldName = adtObjRefNode.getPropertyValue(ICdsAnalysisConstants.FIELD_PROP);
      if (entityName == null || fieldName == null) {
        return;
      }
      NavigationUtil.navigateToEntityColumn(entityName, fieldName, getDestinationId());

    });
  }

  /*
   * Contributes actions to the context menu of fields hierarchy viewer
   */
  private void contributeToHierarchyViewerContextMenu(final IMenuManager menu) {
    final IAdtObjectReferenceNode adtObjRefNode = getAdtObjRefFromSelection();
    if (adtObjRefNode == null) {
      return;
    }
    final String entityName = adtObjRefNode.getDisplayName();
    final String fieldName = adtObjRefNode.getPropertyValue(ICdsAnalysisConstants.FIELD_PROP);
    if (entityName == null || fieldName == null) {
      return;
    }

    menu.appendToGroup(IGeneralMenuConstants.GROUP_OPEN, new Action(
        Messages.FieldHierarchyView_NavigateToFieldAction_xmit) {
      @Override
      public void run() {
        NavigationUtil.navigateToEntityColumn(entityName, fieldName, getDestinationId());
      }
    });

  }

  /*
   * Create tree viewer for displaying the field hierarchy for a single field
   */
  private Control createHierarchyViewerControl(final Composite parent) {
    final Composite hierarchyComposite = new Composite(parent, SWT.NONE);
    hierarchyComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    hierarchyComposite.setSize(100, 100);
    hierarchyComposite.setLayout(new FillLayout());
    hierarchyTreeViewer = new FieldHierarchyViewer(hierarchyComposite);
    return hierarchyComposite;
  }

  /*
   * Create composite for no selected field
   */
  private Composite createNoFieldSelectionComposite(final PageBook pageBook) {
    final Composite composite = new Composite(pageBook, SWT.BACKGROUND);
    GridLayoutFactory.swtDefaults().applyTo(composite);

    final Label label = new Label(composite, SWT.LEAD | SWT.TOP | SWT.WRAP);
    label.setText(Messages.FieldHierarchyView_NoFieldSelected_xfld);

    GridDataFactory.fillDefaults()
        .align(SWT.FILL, SWT.CENTER)
        .grab(true, false)
        .indent(5, SWT.DEFAULT)
        .applyTo(label);
    return composite;
  }

  private void createToolbarActions() {
    actionToggleGroup = new RadioActionGroup();
    actionToggleGroup.addAction(TOP_DOWN_ACTION,
        Messages.FieldHierarchyViewer_FieldOriginModeButton_xtol, SearchAndAnalysisPlugin
            .getDefault()
            .getImageDescriptor(IImages.FIELD_TOP_DOWN), true);
    actionToggleGroup.addAction(WHERE_USED_ACTION,
        Messages.FieldHierarchyViewer_FieldReferencesModeButton_xtol, SearchAndAnalysisPlugin
            .getDefault()
            .getImageDescriptor(IImages.FIELD_WHERE_USED), false);
    actionToggleGroup.addActionToggledListener(actionId -> {
      final boolean isTopDown = TOP_DOWN_ACTION.equals(actionId);
      hierarchyTreeViewer.updateInput(isTopDown);
      updateToolbarLabel(isTopDown);
      searchCalcFieldsAction.setEnabled(!isTopDown);
      settings.setTopDown(isTopDown);
    });
    searchCalcFieldsAction = ActionFactory.createAction(
        Messages.FieldHierarchyView_CalculatedFieldsSearch_xtol, SearchAndAnalysisPlugin
            .getDefault()
            .getImageDescriptor(IImages.FUNCTION), IAction.AS_CHECK_BOX, () -> {
              if (currentFieldInput == null) {
                return;
              }
              settings.setSearchInCalcFields(searchCalcFieldsAction.isChecked());
              reloadFieldInput();
            });
  }

  private void fillToolbar(final ToolBarManager fieldTbm) {
    fieldTbm.add(searchCalcFieldsAction);
    fieldTbm.add(new Separator());
    actionToggleGroup.contributeToToolbar(fieldTbm);
  }

  /*
   * Retrieves an ADT object reference node from the current selection or 'null'
   */
  private IAdtObjectReferenceNode getAdtObjRefFromSelection() {
    final StructuredSelection selection = (StructuredSelection) hierarchyTreeViewer.getSelection();
    if (selection.isEmpty()) {
      return null;
    }
    final Object selected = selection.getFirstElement();
    if (!(selected instanceof IAdtObjectReferenceNode)) {
      return null;
    }
    return (IAdtObjectReferenceNode) selected;
  }

  /*
   * Updates the Tool Bar label
   */
  private void updateToolbarLabel(final boolean topDown) {
    hierarchyViewerPaneLabel.setImage(fieldNode.getImage());
    final StringBuilder infoLabelText = new StringBuilder(
        currentInputObjectType != ObjectType.DATA_DEFINITION ? fieldNode.getDisplayName()
            .toUpperCase() : fieldNode.getDisplayName());
    infoLabelText.append("   ["); //$NON-NLS-1$
    if (topDown) {
      infoLabelText.append(Messages.FieldHierarchyView_FieldOriginModeHeading_xfld);
    } else {
      infoLabelText.append(Messages.FieldHierarchyView_FieldReferencesModeHeading_xfld);
    }
    infoLabelText.append("]"); //$NON-NLS-1$
    hierarchyViewerPaneLabel.setText(infoLabelText.toString());
  }
}
