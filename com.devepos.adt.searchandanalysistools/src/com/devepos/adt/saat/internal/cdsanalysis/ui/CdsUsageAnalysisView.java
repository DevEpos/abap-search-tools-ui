package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.devepos.adt.saat.ICommandConstants;
import com.devepos.adt.saat.IContextMenuConstants;
import com.devepos.adt.saat.IDestinationProvider;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisServiceFactory;
import com.devepos.adt.saat.internal.cdsanalysis.ICdsEntityUsageInfo;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoCollection;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.menu.MenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.ILazyLoadingNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Dependency usage analysis page of CDS Analysis page
 *
 * @see    {@link CdsAnalyzerPage}
 * @author stockbal
 */
public class CdsUsageAnalysisView extends CdsAnalysisPage {
	private enum Column {
		OBJECT_NAME(400, Messages.CdsUsageAnalysisView_ObjectNameColumn_xfld),
		OCCURRENCES(80, Messages.CdsUsageAnalysisView_OccurrencesColumn_xfld),
		USED_ENTITY_COUNT(60, Messages.CdsUsageAnalysisView_EntitiesColumn_xfld,
			Messages.CdsUsageAnalysisView_EntitiesColumn_xtol),
		USED_JOIN_COUNT(60, Messages.CdsUsageAnalysisView_JoinsColumn_xfld, Messages.CdsUsageAnalysisView_JoinsColumn_xtol),
		USED_UNION_COUNT(60, Messages.CdsUsageAnalysisView_UnionsColumn_xfld, Messages.CdsUsageAnalysisView_UnionsColumn_xtol);

		private static final Map<Integer, Column> COLUMNS;

		static {
			COLUMNS = new HashMap<>();
			for (final Column col : Column.values()) {
				COLUMNS.put(col.ordinal(), col);
			}
		}

		private Column(final int width, final String headerText) {
			this(width, headerText, headerText);
		}

		private Column(final int width, final String headerText, final String tooltip) {
			this.defaultWidth = width;
			this.headerText = headerText;
			this.tooltip = tooltip;
		}

		public static Column valueOf(final int ordinal) {
			return COLUMNS.get(ordinal);
		}

		public final int defaultWidth;
		public final String tooltip;
		public final String headerText;

	}

	private final List<Column> columns;
	private SortListener sortListener;

	public CdsUsageAnalysisView(final CdsAnalysis parentView) {
		super(parentView);
		this.columns = Arrays.asList(Column.values());
	}

	@Override
	public Image getImage() {
		return SearchAndAnalysisPlugin.getDefault().getImage(IImages.USAGE_ANALYZER);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.USAGE_ANALYZER);
	}

	@Override
	public String getLabelPrefix() {
		return Messages.CdsUsageAnalysisView_ViewLabel_xfld;
	}

	@Override
	protected void fillContextMenu(final IMenuManager mgr, final CommandPossibleChecker commandPossibleChecker) {
		super.fillContextMenu(mgr, commandPossibleChecker);
		if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.CDS_TOP_DOWN_ANALYSIS)) {
			MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
				ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
		}
		if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS)) {
			MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
				ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS);
		}
		if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.FIELD_ANALYSIS)) {
			MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
				ICommandConstants.FIELD_ANALYSIS);
		}
	}

	@Override
	protected void setTreeInput(final IAdtObjectReferenceElementInfo cdsViewAdtObj, final TreeViewer treeViewer) {
		final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(cdsViewAdtObj.getName(),
			cdsViewAdtObj.getDisplayName(), cdsViewAdtObj.getDescription(), cdsViewAdtObj.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = cdsViewAdtObj.getAdapter(IDestinationProvider.class);
		node.setElementInfoProvider(new IElementInfoProvider() {
			@Override
			public String getProviderDescription() {
				return NLS.bind(Messages.CdsAnalysis_UsageAnalysisProviderDescription_xmsg, cdsViewAdtObj.getDisplayName());
			}

			@Override
			public List<IElementInfo> getElements() {
				if (destProvider == null) {
					return null;
				}
				final IElementInfo cdsTopDownInfo = CdsAnalysisServiceFactory.createCdsAnalysisService()
					.loadUsedEntitiesAnalysis(cdsViewAdtObj.getName(), destProvider.getDestinationId());
				if (cdsTopDownInfo != null) {
					return ((IElementInfoCollection) cdsTopDownInfo).getChildren();
				}
				return null;
			}
		});
		treeViewer.setInput(node);
	}

	@Override
	protected void refreshAnalysis() {
		((ILazyLoadingNode) getViewer().getInput()).resetLoadedState();
		getViewer().refresh();
	}

	@Override
	protected void configureTreeViewer(final TreeViewer treeViewer) {
		this.sortListener = new SortListener(treeViewer, SWT.DOWN);
		final Tree tree = treeViewer.getTree();
		treeViewer.setComparator(new TreeSorter());
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.setUseHashlookup(true);
		createColumns(treeViewer);

		tree.setSortColumn(tree.getColumn(Column.USED_ENTITY_COUNT.ordinal()));
		tree.setSortDirection(SWT.DOWN);
	}

	private void createColumns(final TreeViewer treeViewer) {
		for (final Column column : this.columns) {
			createColumn(treeViewer, column);
		}
	}

	private void createColumn(final TreeViewer treeViewer, final Column column) {
		final TreeViewerColumn viewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		viewerColumn.getColumn().setText(column.headerText);
		viewerColumn.getColumn().setToolTipText(column.tooltip);
		viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new ColumnLabelProvider(column)));
		viewerColumn.getColumn().setWidth(column.defaultWidth);
		viewerColumn.getColumn().setMoveable(true);
		viewerColumn.getColumn().addListener(SWT.Selection, this.sortListener);
	}

	private class ContentProvider extends LazyLoadingTreeContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			final Object[] nodes = getChildren(inputElement);
			if (nodes != null) {
				return nodes;
			} else {
				return new Object[0];
			}
		}

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
		public StyledString getStyledText(final Object element) {
			StyledString text = new StyledString();

			if (this.column == Column.OBJECT_NAME) {
				text = getTreeNodeLabel(element);
			} else {
				ICdsEntityUsageInfo usageInfo = null;
				if (element instanceof IAdaptable) {
					usageInfo = ((IAdaptable) element).getAdapter(ICdsEntityUsageInfo.class);
				}
				if (usageInfo != null) {
					switch (this.column) {
					case OCCURRENCES:
						text.append(String.valueOf(usageInfo.getOccurrence()));
						break;
					case USED_ENTITY_COUNT:
						text.append(String.valueOf(usageInfo.getUsedEntitiesCount()));
						break;
					case USED_JOIN_COUNT:
						text.append(String.valueOf(usageInfo.getUsedJoinCount()));
						break;
					case USED_UNION_COUNT:
						text.append(String.valueOf(usageInfo.getUsedUnionCount()));
						break;
					default:
						break;
					}
				}

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
	}

	private class TreeSorter extends ViewerComparator {

		@Override
		public int compare(final Viewer viewer, final Object e1, final Object e2) {
			final Tree tree = (Tree) viewer.getControl();
			final TreeColumn sortedColumn = tree.getSortColumn();
			final int direction = tree.getSortDirection();
			final int sortedColIdx = tree.indexOf(sortedColumn);

			final Column column = Column.valueOf(sortedColIdx);
			if (column == Column.OBJECT_NAME) {
				return compare(((ITreeNode) e1).getName(), ((ITreeNode) e2).getName(), direction);
			} else {
				final ICdsEntityUsageInfo info1 = ((IAdaptable) e1).getAdapter(ICdsEntityUsageInfo.class);
				final ICdsEntityUsageInfo info2 = ((IAdaptable) e2).getAdapter(ICdsEntityUsageInfo.class);
				if (info1 == null || info2 == null) {
					return 0;
				}
				switch (column) {
				case OCCURRENCES:
					return compare(info1.getOccurrence(), info2.getOccurrence(), direction);
				case USED_ENTITY_COUNT:
					return compare(info1.getUsedEntitiesCount(), info2.getUsedEntitiesCount(), direction);
				case USED_JOIN_COUNT:
					return compare(info1.getUsedJoinCount(), info2.getUsedJoinCount(), direction);
				case USED_UNION_COUNT:
					return compare(info1.getUsedUnionCount(), info2.getUsedUnionCount(), direction);
				default:
					break;
				}
			}
			return 0;
		}

		private <T extends Comparable<T>> int compare(final T c1, final T c2, final int dir) {
			if (SWT.UP == dir) {
				if (c1 == null) {
					return 1;
				}
				if (c2 == null) {
					return -1;
				}
				return c1.compareTo(c2);
			} else {
				if (c2 == null) {
					return 1;
				}
				if (c1 == null) {
					return -1;
				}
				return c2.compareTo(c1);
			}
		}

	}
}
