package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IActionBars;

import com.devepos.adt.base.elementinfo.LazyLoadingRefreshMode;
import com.devepos.adt.base.ui.IGeneralMenuConstants;
import com.devepos.adt.base.ui.StylerFactory;
import com.devepos.adt.base.ui.action.ActionFactory;
import com.devepos.adt.base.ui.action.OpenColorPreferencePageAction;
import com.devepos.adt.base.ui.tree.IAdtObjectReferenceNode;
import com.devepos.adt.base.ui.tree.IStyledTreeNode;
import com.devepos.adt.base.ui.tree.ITreeNode;
import com.devepos.adt.base.ui.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.base.ui.tree.LoadingTreeItemsNode;
import com.devepos.adt.saat.internal.IColorConstants;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.devepos.adt.saat.internal.IContextMenuConstants;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsAnalysisPreferences;
import com.devepos.adt.saat.internal.cdsanalysis.ISqlRelationInfo;
import com.devepos.adt.saat.internal.menu.SaatMenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.ui.NativeColumnViewerToolTipSupport;
import com.devepos.adt.saat.internal.ui.TreeViewUiState;
import com.devepos.adt.saat.internal.ui.ViewUiState;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;

/**
 * Top-Down Analysis of CDS Analysis page
 *
 * @see {@link CdsAnalyzerPage}
 * @author stockbal
 */
public class CdsTopDownAnalysisView extends CdsAnalysisPage<CdsTopDownAnalysis> {

  private Action showDescriptions;

  private Action showAliasNames;
  private Action loadAssociations;
  private final List<Column> columns;
  private OpenColorPreferencePageAction showColorsAndFontsPrefs;
  private final IPropertyChangeListener colorPropertyChangeListener;

  public CdsTopDownAnalysisView(final CdsAnalysisView parentView) {
    super(parentView);
    columns = Arrays.asList(Column.OBJECT_NAME, Column.RELATION);
    colorPropertyChangeListener = event -> {
      if (IColorConstants.CDS_ANALYSIS_ALIAS_NAME.equals(event.getProperty())) {
        final StructuredViewer viewer = getViewer();
        if (viewer != null && !viewer.getControl().isDisposed()) {
          viewer.refresh();
        }
      }
    };
    JFaceResources.getColorRegistry().addListener(colorPropertyChangeListener);
  }

  /**
   * Label provider for a single column in this TreeViewer
   *
   * @author stockbal
   */
  class ColumnLabelProvider extends CellLabelProvider implements
      DelegatingStyledCellLabelProvider.IStyledLabelProvider {

    private final Column column;

    public ColumnLabelProvider(final Column column) {
      this.column = column;
    }

    @Override
    public Image getImage(final Object element) {
      Image image = null;
      if (column == Column.OBJECT_NAME) {
        image = getTreeNodeImage(element);
      }
      return image;
    }

    @Override
    public StyledString getStyledText(final Object element) {
      StyledString text = new StyledString();

      switch (column) {
      case OBJECT_NAME:
        text = getTreeNodeLabel(element);
        break;
      case RELATION:
        ISqlRelationInfo relationalInfo = null;
        if (element instanceof IAdaptable) {
          relationalInfo = ((IAdaptable) element).getAdapter(ISqlRelationInfo.class);
        }

        if (relationalInfo != null && relationalInfo.getRelation() != null && !relationalInfo
            .getRelation()
            .isEmpty()) {
          text.append(relationalInfo.getRelation());
        }

        break;
      }

      return text;
    }

    @Override
    public String getToolTipText(final Object element) {
      if (column == Column.OBJECT_NAME && element instanceof IAdtObjectReferenceNode) {
        final IAdtObjectReferenceNode adtNode = (IAdtObjectReferenceNode) element;
        final StringBuffer tooltip = new StringBuffer();
        appendTooltipInfo(tooltip, Messages.CdsTopDownAnalysisView_NameTooltipPart_xtol, adtNode
            .getDisplayName());
        appendTooltipInfo(tooltip, Messages.CdsTopDownAnalysisView_DescriptionTooltipPart_xtol,
            adtNode.getDescription());
        final ISqlRelationInfo relationInfo = adtNode.getAdapter(ISqlRelationInfo.class);
        if (relationInfo != null) {
          appendTooltipInfo(tooltip, Messages.CdsTopDownAnalysisView_AliasTooltipPart_xtol,
              relationInfo.getAliasName());
        }
        return tooltip.toString();
      }
      return super.getToolTipText(element);
    }

    @Override
    public void update(final ViewerCell cell) {
    }

    private void appendTooltipInfo(final StringBuffer tooltip, final String infoName,
        final String infoContent) {
      if (infoContent == null || infoContent.isEmpty()) {
        return;
      }
      if (tooltip.length() > 0) {
        tooltip.append(System.lineSeparator());
      }
      tooltip.append(infoName);
      tooltip.append(":"); //$NON-NLS-1$
      tooltip.append(System.lineSeparator());
      tooltip.append("  "); //$NON-NLS-1$
      tooltip.append(infoContent);
    }
  }

  private enum Column {
    OBJECT_NAME(400, Messages.CdsTopDownAnalysisView_ObjectTypeColumn_xmit),
    RELATION(100, Messages.CdsTopDownAnalysisView_SqlRelationColumn_xmit);

    public final int defaultWidth;
    public final String headerText;

    Column(final int width, final String headerText) {
      defaultWidth = width;
      this.headerText = headerText;
    }

  }

  @Override
  public void dispose() {
    super.dispose();
    JFaceResources.getColorRegistry().removeListener(colorPropertyChangeListener);
  }

  @Override
  public void setActionBars(final IActionBars actionBars) {
    super.setActionBars(actionBars);
    final IMenuManager menu = actionBars.getMenuManager();
    menu.appendToGroup(IGeneralMenuConstants.GROUP_PROPERTIES, showDescriptions);
    menu.appendToGroup(IGeneralMenuConstants.GROUP_PROPERTIES, showAliasNames);
    menu.appendToGroup(IGeneralMenuConstants.GROUP_PROPERTIES, new Separator());
    menu.appendToGroup(IGeneralMenuConstants.GROUP_PROPERTIES, loadAssociations);
    menu.appendToGroup(IGeneralMenuConstants.GROUP_ADDITIONS, showColorsAndFontsPrefs);
  }

  @Override
  protected void configureTreeViewer(final TreeViewer treeViewer) {
    final LazyLoadingTreeContentProvider contentProvider = new LazyLoadingTreeContentProvider(
        LazyLoadingRefreshMode.ROOT_AND_NON_LAZY_CHILDREN, 1);
    contentProvider.setExpansionChecker(node -> {
      final ISqlRelationInfo relation = node.getAdapter(ISqlRelationInfo.class);
      return relation != null && !"ASSOCIATIONS".equals(relation.getType()); //$NON-NLS-1$
    });
    treeViewer.setContentProvider(contentProvider);
    treeViewer.setUseHashlookup(true);
    treeViewer.getTree().setHeaderVisible(true);
    NativeColumnViewerToolTipSupport.enableFor(treeViewer);
    createColumns(treeViewer);
  }

  @Override
  protected void createActions() {
    super.createActions();
    showDescriptions = ActionFactory.createAction(
        Messages.CdsTopDownAnalysisView_ShowDescriptionsToggleAction_xmit, null,
        Action.AS_CHECK_BOX, () -> {
          analysisResult.getSettings().setShowDescriptions(showDescriptions.isChecked());
          getViewer().refresh();
        });
    showAliasNames = ActionFactory.createAction(
        Messages.CdsTopDownAnalysisView_ShowAliasNamesToggleAction_xmit, null, Action.AS_CHECK_BOX,
        () -> {
          analysisResult.getSettings().setShowAliasNames(showAliasNames.isChecked());
          getViewer().refresh();
        });
    loadAssociations = ActionFactory.createAction(
        Messages.CdsTopDownAnalysisView_LoadAssociationsToggleAction_xmit, null,
        Action.AS_CHECK_BOX, () -> {
          analysisResult.getSettings().setLoadAssociations(loadAssociations.isChecked());
          refreshAnalysis();
        });
    showColorsAndFontsPrefs = new OpenColorPreferencePageAction();
    showColorsAndFontsPrefs.setColorId(IColorConstants.CDS_ANALYSIS_ALIAS_NAME);
    showColorsAndFontsPrefs.setCategories(IColorConstants.SAAT_COLOR_CATEGORY,
        IColorConstants.CDS_ANALYSIS_CATEGORY);
  }

  @Override
  protected void fillContextMenu(final IMenuManager mgr,
      final CommandPossibleChecker commandPossibleChecker) {
    super.fillContextMenu(mgr, commandPossibleChecker);
    if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS)) {
      SaatMenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
          ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
    }
    if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.USED_ENTITIES_ANALYSIS)) {
      SaatMenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
          ICommandConstants.USED_ENTITIES_ANALYSIS);
    }
    if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.FIELD_ANALYSIS)) {
      SaatMenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
          ICommandConstants.FIELD_ANALYSIS);
    }
  }

  @Override
  protected StyledString getTreeNodeLabel(final Object element) {
    StyledString text = null;
    final ITreeNode node = (ITreeNode) element;

    if (element instanceof IStyledTreeNode) {
      text = ((IStyledTreeNode) element).getStyledText();
    } else {
      text = new StyledString();
      if (element instanceof LoadingTreeItemsNode) {
        text.append(node.getDisplayName(), StylerFactory.ITALIC_STYLER);
      } else {
        text.append(" "); // for broader image due to overlay //$NON-NLS-1$
        text.append(node.getDisplayName());
      }

      if (showAliasNames.isChecked()) {
        ISqlRelationInfo relationalInfo;
        relationalInfo = node.getAdapter(ISqlRelationInfo.class);
        if (relationalInfo != null) {
          final String alias = relationalInfo.getAliasName();
          if (alias != null && !alias.isEmpty()) {
            text.append(" [" + alias + "] ", //$NON-NLS-1$ //$NON-NLS-2$
                StylerFactory.createCustomStyler(SWT.NORMAL,
                    IColorConstants.CDS_ANALYSIS_ALIAS_NAME, null));
          }
        }
      }

      if (showDescriptions.isChecked()) {
        final String description = node.getDescription();
        if (description != null && !description.isEmpty()) {
          text.append("  " + description + "  ", //$NON-NLS-1$ //$NON-NLS-2$
              StylerFactory.createCustomStyler(SWT.ITALIC, JFacePreferences.DECORATIONS_COLOR,
                  null));
        }
      }
    }
    return text;
  }

  @Override
  protected ViewUiState getUiState() {
    final TreeViewUiState uiState = new TreeViewUiState();
    uiState.setFromTreeViewer((TreeViewer) getViewer());
    return uiState;
  }

  @Override
  protected void loadInput(final ViewUiState uiState) {
    final TreeViewer viewer = (TreeViewer) getViewer();

    if (analysisResult.isResultLoaded()) {
      setActionStateFromSettings();
      viewer.setInput(analysisResult.getResult());
      // update ui state
      if (uiState instanceof TreeViewUiState) {
        ((TreeViewUiState) uiState).applyToTreeViewer(viewer);
      } else {
        final Object[] input = (Object[]) viewer.getInput();
        if (input != null && input.length >= 1) {
          viewer.expandToLevel(input[0], 1);
          viewer.setSelection(new StructuredSelection(input[0]));
        }
      }
    } else {
      initActionState();
      analysisResult.createElementInfoProvider();
      analysisResult.setResultLoaded(true);
      viewer.setInput(analysisResult.getResult());
      viewer.expandAll();
    }
  }

  @Override
  protected void refreshAnalysis() {
    final TreeViewer viewer = (TreeViewer) getViewer();
    viewer.collapseAll();
    analysisResult.refreshAnalysis();
    viewer.expandAll();
  }

  private void createColumn(final TreeViewer treeViewer, final Column column) {
    final TreeViewerColumn viewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
    viewerColumn.getColumn().setText(column.headerText);
    viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new ColumnLabelProvider(
        column)));
    viewerColumn.getColumn().setWidth(column.defaultWidth);
    viewerColumn.getColumn().setMoveable(true);
  }

  private void createColumns(final TreeViewer treeViewer) {
    for (final Column column : columns) {
      createColumn(treeViewer, column);
    }
  }

  private void initActionState() {
    IPreferenceStore prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
    boolean isShowDescriptions = prefStore.getBoolean(
        ICdsAnalysisPreferences.TOP_DOWN_SHOW_DESCRIPTIONS);
    boolean isShowAliasNames = prefStore.getBoolean(
        ICdsAnalysisPreferences.TOP_DOWN_SHOW_ALIAS_NAMES);
    boolean isLoadAssociations = prefStore.getBoolean(
        ICdsAnalysisPreferences.TOP_DOWN_LOAD_ASSOCIATIONS);
    showDescriptions.setChecked(isShowDescriptions);
    showAliasNames.setChecked(isShowAliasNames);
    loadAssociations.setChecked(isLoadAssociations);

    if (analysisResult != null) {
      ICdsTopDownSettingsUi settings = analysisResult.getSettings();
      settings.setShowDescriptions(isShowDescriptions);
      settings.setShowAliasNames(isShowAliasNames);
      settings.setLoadAssociations(isLoadAssociations);
    }
  }

  private void setActionStateFromSettings() {
    ICdsTopDownSettingsUi analysisSettings = analysisResult.getSettings();
    showDescriptions.setChecked(analysisSettings.isShowDescriptions());
    showAliasNames.setChecked(analysisSettings.isShowAliasNames());
    loadAssociations.setChecked(analysisSettings.isLoadAssociations());
  }

}
