package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.IColorConstants;
import com.devepos.adt.saat.ICommandConstants;
import com.devepos.adt.saat.IContextMenuConstants;
import com.devepos.adt.saat.IDestinationProvider;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsTopDownElementInfoProvider;
import com.devepos.adt.saat.internal.cdsanalysis.ISqlRelationInfo;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.LazyLoadingRefreshMode;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.ILazyLoadingNode;
import com.devepos.adt.saat.internal.tree.IStyledTreeNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider.LoadingElement;
import com.devepos.adt.saat.internal.ui.MenuItemFactory;
import com.devepos.adt.saat.internal.ui.PreferenceToggleAction;
import com.devepos.adt.saat.internal.ui.StylerFactory;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Top-Down Analysis of CDS Analysis page
 *
 * @see    {@link CdsAnalyzerPage}
 * @author stockbal
 */
public class CdsTopDownAnalysisView extends CdsAnalysisPage {
	private enum Column {
		OBJECT_NAME(400, Messages.CdsTopDownAnalysisView_ObjectTypeColumn_xmit),
		RELATION(100, Messages.CdsTopDownAnalysisView_SqlRelationColumn_xmit);

		public final int defaultWidth;
		public final String headerText;

		private Column(final int width, final String headerText) {
			this.defaultWidth = width;
			this.headerText = headerText;
		}

	}

	private PreferenceToggleAction showDescriptions;
	private PreferenceToggleAction showAliasNames;
	private static final String SHOW_DESCRIPTIONS_PREF_KEY = "com.devepos.adt.saat.cdstopdownanalysis.showDescriptions"; //$NON-NLS-1$
	private static final String SHOW_ALIAS_NAMES_PREF_KEY = "com.devepos.adt.saat.cdstopdownanalysis.showAliasNames"; //$NON-NLS-1$
	private final List<Column> columns;
	private LazyLoadingAdtObjectReferenceNode cdsNode;
	private final IPropertyChangeListener propertyChangeListener;

	public CdsTopDownAnalysisView(final CdsAnalysis parentView) {
		super(parentView);
		this.columns = Arrays.asList(Column.OBJECT_NAME, Column.RELATION);
		this.propertyChangeListener = event -> {
			if (event.getProperty().equals(SHOW_ALIAS_NAMES_PREF_KEY) || event.getProperty().equals(SHOW_DESCRIPTIONS_PREF_KEY)) {
				getViewer().refresh();
			}
		};
		SearchAndAnalysisPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this.propertyChangeListener);
	}

	@Override
	public Image getImage() {
		return SearchAndAnalysisPlugin.getDefault().getImage(IImages.TOP_DOWN);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.TOP_DOWN);
	}

	@Override
	public void dispose() {
		super.dispose();
		SearchAndAnalysisPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this.propertyChangeListener);
	}

	@Override
	public String getLabelPrefix() {
		return Messages.CdsTopDownAnalysisView_ViewLabel_xfld;
	}

	@Override
	protected void fillContextMenu(final IMenuManager mgr, final CommandPossibleChecker commandPossibleChecker) {
		super.fillContextMenu(mgr, commandPossibleChecker);
		if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS)) {
			MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
				ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
		}
		if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.USED_ENTITIES_ANALYSIS)) {
			MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
				ICommandConstants.USED_ENTITIES_ANALYSIS);
		}
		if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.FIELD_ANALYSIS)) {
			MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
				ICommandConstants.FIELD_ANALYSIS);
		}
	}

	@Override
	protected void configureTreeViewer(final TreeViewer treeViewer) {
		treeViewer.setContentProvider(new LazyLoadingTreeContentProvider(LazyLoadingRefreshMode.ROOT_AND_NON_LAZY_CHILDREN, 1));
		treeViewer.setUseHashlookup(true);
		treeViewer.getTree().setHeaderVisible(true);
		ColumnViewerToolTipSupport.enableFor(treeViewer);
		createColumns(treeViewer);
	}

	@Override
	protected void setTreeInput(final IAdtObjectReferenceElementInfo cdsObjInfo, final TreeViewer treeViewer) {
		final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(cdsObjInfo.getName(),
			cdsObjInfo.getDisplayName(), cdsObjInfo.getDescription(), cdsObjInfo.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = cdsObjInfo.getAdapter(IDestinationProvider.class);
		node.setElementInfoProvider(new CdsTopDownElementInfoProvider(
			destProvider != null ? destProvider.getDestinationId() : null, cdsObjInfo.getName()));
		node.setAdditionalInfo(cdsObjInfo.getAdditionalInfo());
		this.cdsNode = node;
		treeViewer.setInput(new Object[] { node });
		treeViewer.expandAll();
	}

	@Override
	protected void refreshAnalysis() {
		if (this.cdsNode != null && this.cdsNode instanceof ILazyLoadingNode) {
			final TreeViewer viewer = (TreeViewer) getViewer();
			viewer.collapseAll();
			((ILazyLoadingNode) this.cdsNode).resetLoadedState();
			viewer.expandAll();
		}
	}

	@Override
	protected void fillPullDownMenu(final IMenuManager menu) {
		super.fillPullDownMenu(menu);
		menu.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, this.showDescriptions);
		menu.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, this.showAliasNames);
	}

	@Override
	protected void createActions() {
		super.createActions();
		this.showDescriptions = new PreferenceToggleAction(Messages.CdsTopDownAnalysisView_ShowDescriptionsToggleAction_xmit,
			null, SHOW_DESCRIPTIONS_PREF_KEY, true);
		this.showAliasNames = new PreferenceToggleAction(Messages.CdsTopDownAnalysisView_ShowAliasNamesToggleAction_xmit, null,
			SHOW_ALIAS_NAMES_PREF_KEY, true);
	}

	@Override
	protected StyledString getTreeNodeLabel(final Object element) {
		StyledString text = null;
		final ITreeNode node = (ITreeNode) element;

		if (element instanceof IStyledTreeNode) {
			text = ((IStyledTreeNode) element).getStyledText();
		} else {
			text = new StyledString();
			if (element instanceof LoadingElement) {
				text.append(node.getDisplayName(), StylerFactory.ITALIC_STYLER);
			} else {
				text.append(" "); // for broader image due to overlay //$NON-NLS-1$
				text.append(node.getDisplayName());
			}

			if (this.showAliasNames.isChecked()) {
				if (element instanceof IAdaptable) {
					ISqlRelationInfo relationalInfo = null;
					relationalInfo = ((IAdaptable) element).getAdapter(ISqlRelationInfo.class);
					if (relationalInfo != null) {
						final String alias = relationalInfo.getAliasName();
						if (alias != null && !alias.isEmpty()) {
							text.append(" [" + alias + "] ", //$NON-NLS-1$ //$NON-NLS-2$
								StylerFactory.createCustomStyler(SWT.NORMAL, IColorConstants.CDS_ANALYSIS_ALIAS_NAME, null));
						}
					}
				}
			}

			if (this.showDescriptions.isChecked()) {
				final String description = node.getDescription();
				if (description != null && !description.isEmpty()) {
					text.append("  " + description + "  ", //$NON-NLS-1$ //$NON-NLS-2$
						StylerFactory.createCustomStyler(SWT.ITALIC, JFacePreferences.DECORATIONS_COLOR, null));
				}
			}
		}
		return text;
	}

	private void createColumns(final TreeViewer treeViewer) {
		for (final Column column : this.columns) {
			createColumn(treeViewer, column);
		}
	}

	private void createColumn(final TreeViewer treeViewer, final Column column) {
		final TreeViewerColumn viewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		viewerColumn.getColumn().setText(column.headerText);
		viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new ColumnLabelProvider(column)));
		viewerColumn.getColumn().setWidth(column.defaultWidth);
		viewerColumn.getColumn().setMoveable(true);
	}

	/**
	 * Label provider for a single column in this TreeViewer
	 *
	 * @author stockbal
	 */
	class ColumnLabelProvider extends CellLabelProvider implements DelegatingStyledCellLabelProvider.IStyledLabelProvider {

		private final Column column;

		public ColumnLabelProvider(final Column column) {
			this.column = column;
		}

		@Override
		public String getToolTipText(final Object element) {
			if (element instanceof IAdtObjectReferenceNode) {
				final IAdtObjectReferenceNode adtNode = (IAdtObjectReferenceNode) element;
				final StringBuffer tooltip = new StringBuffer();
				appendTooltipInfo(tooltip, Messages.CdsTopDownAnalysisView_NameTooltipPart_xtol, adtNode.getDisplayName());
				appendTooltipInfo(tooltip, Messages.CdsTopDownAnalysisView_DescriptionTooltipPart_xtol, adtNode.getDescription());
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
		public StyledString getStyledText(final Object element) {
			StyledString text = new StyledString();

			switch (this.column) {
			case OBJECT_NAME:
				text = getTreeNodeLabel(element);
				break;
			case RELATION:
				ISqlRelationInfo relationalInfo = null;
				if (element instanceof IAdaptable) {
					relationalInfo = ((IAdaptable) element).getAdapter(ISqlRelationInfo.class);
				}

				if (relationalInfo != null && relationalInfo.getRelation() != null && !relationalInfo.getRelation().isEmpty()) {
					text.append(relationalInfo.getRelation());
				}

				break;
			}

			return text;
		}

		@Override
		public Image getImage(final Object element) {
			Image image = null;
			if (this.column == Column.OBJECT_NAME) {
				image = getTreeNodeImage(element);
			}
			return image;
		}

		@Override
		public void update(final ViewerCell cell) {
		}

		private void appendTooltipInfo(final StringBuffer tooltip, final String infoName, final String infoContent) {
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

}
