package com.devepos.adt.saat.internal.cdsanalysis.ui;

import java.text.DecimalFormat;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.devepos.adt.saat.ICommandConstants;
import com.devepos.adt.saat.IContextMenuConstants;
import com.devepos.adt.saat.IDestinationProvider;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.WhereUsedInCdsElementInfoProvider;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.menu.MenuItemFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.tree.ICollectionTreeNode;
import com.devepos.adt.saat.internal.tree.ILazyLoadingListener;
import com.devepos.adt.saat.internal.tree.ILazyLoadingNode;
import com.devepos.adt.saat.internal.tree.IStyledTreeNode;
import com.devepos.adt.saat.internal.tree.ITreeNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider;
import com.devepos.adt.saat.internal.tree.LazyLoadingTreeContentProvider.LoadingElement;
import com.devepos.adt.saat.internal.ui.PreferenceToggleAction;
import com.devepos.adt.saat.internal.ui.StylerFactory;
import com.devepos.adt.saat.internal.util.CommandPossibleChecker;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Where-Used page of CDS Analysis page
 *
 * @see    {@link CdsAnalyzerPage}
 * @author stockbal
 */
public class WhereUsedInCdsAnalysisView extends CdsAnalysisPage {
	private static final String INDETERMINATE_COUNT = "?"; //$NON-NLS-1$
	private String rootWhereUsedCount = INDETERMINATE_COUNT;
	private final ILazyLoadingListener lazyLoadingListener;
	private PreferenceToggleAction showFromUses;
	private PreferenceToggleAction showAssocUses;
	private static final String USES_IN_SELECT_PREF_KEY = "com.devepos.adt.saat.whereusedincds.showReferencesInSelectPartOfCds"; //$NON-NLS-1$
	private static final String USES_IN_ASSOC_PREF_KEY = "com.devepos.adt.saat.whereusedincds.showReferencesInAssocPartOfCds"; //$NON-NLS-1$
	private final IPropertyChangeListener propertyChangeListener;
	private WhereUsedInCdsElementInfoProvider rootWhereUsedProvider;
	private ILazyLoadingNode cdsWhereUsedNode;

	public WhereUsedInCdsAnalysisView(final CdsAnalysis parentView) {
		super(parentView);
		this.lazyLoadingListener = (count) -> {
			this.rootWhereUsedCount = NLS.bind(
				count == 1 ? Messages.WhereUsedInCdsAnalysisView_SingleReferenceLabelSuffix_xfld
					: Messages.WhereUsedInCdsAnalysisView_MultipleReferencesLabelSuffix_xfld,
				new DecimalFormat("###,###").format(count)); //$NON-NLS-1$
			PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
				getViewPart().updateLabel();
			});
		};

		this.propertyChangeListener = event -> {
			final boolean showFromUsesChanged = event.getProperty().equals(USES_IN_SELECT_PREF_KEY);
			final boolean showAssocUsesChanged = event.getProperty().equals(USES_IN_ASSOC_PREF_KEY);

			if (!showFromUsesChanged && !showAssocUsesChanged) {
				return;
			}
			// trigger refresh of where used analysis
			this.rootWhereUsedProvider.updateSearchParameters(this.showFromUses.isChecked(), this.showAssocUses.isChecked());
			refreshAnalysis();
		};
		SearchAndAnalysisPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this.propertyChangeListener);
	}

	@Override
	public Image getImage() {
		return SearchAndAnalysisPlugin.getDefault().getImage(IImages.WHERE_USED_IN);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.WHERE_USED_IN);
	}

	@Override
	public String getLabelPrefix() {
		if (this.showAssocUses == null && this.showFromUses == null) {
			return Messages.WhereUsedInCdsAnalysisView_ViewLabel_xfld;
		} else {
			if (this.showAssocUses.isChecked() && this.showFromUses.isChecked()) {
				return Messages.WhereUsedInCdsAnalysisView_ViewLabel_xfld;
			} else if (this.showAssocUses.isChecked()) {
				return Messages.WhereUsedInCdsAnalysisView_ViewLabelAssocSearch_xfld;
			} else {
				return Messages.WhereUsedInCdsAnalysisView_ViewLabelSelectFromSearch_xlfd;
			}
		}
	}

	@Override
	public String getLabel() {
		if (this.rootWhereUsedCount.equals(INDETERMINATE_COUNT)) {
			return super.getLabel();
		} else {
			return String.format("%s  -  %s", super.getLabel(), this.rootWhereUsedCount); //$NON-NLS-1$
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		SearchAndAnalysisPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this.propertyChangeListener);
	}

	@Override
	protected void createActions() {
		super.createActions();
		this.showFromUses = new PreferenceToggleAction(Messages.WhereUsedInCdsAnalysisView_ShowUsesInSelectPartAction_xmit,
			SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.DATA_SOURCE), USES_IN_SELECT_PREF_KEY, true);
		this.showAssocUses = new PreferenceToggleAction(Messages.WhereUsedInCdsAnalysisView_ShowUsesInAssociationsAction_xmit,
			SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.ASSOCIATION), USES_IN_ASSOC_PREF_KEY, false);
		this.showAssocUses.addPropertyChangeListener((event) -> {
			if (!this.showAssocUses.isChecked()) {
				this.showFromUses.setChecked(true);
			}
		});
		this.showFromUses.addPropertyChangeListener((event) -> {
			if (!this.showFromUses.isChecked()) {
				this.showAssocUses.setChecked(true);
			}
		});
	}

	@Override
	protected void configureTreeViewer(final TreeViewer treeViewer) {
		treeViewer.setContentProvider(new LazyLoadingTreeContentProvider());
		treeViewer.setUseHashlookup(true);
		treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new TreeViewerLabelProvider()));
	}

	@Override
	protected void fillContextMenu(final IMenuManager mgr, final CommandPossibleChecker commandPossibleChecker) {
		super.fillContextMenu(mgr, commandPossibleChecker);
		if (commandPossibleChecker.canCommandBeEnabled(ICommandConstants.CDS_TOP_DOWN_ANALYSIS)) {
			MenuItemFactory.addCdsAnalyzerCommandItem(mgr, IContextMenuConstants.GROUP_CDS_ANALYSIS,
				ICommandConstants.CDS_TOP_DOWN_ANALYSIS);
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
	protected void setTreeInput(final IAdtObjectReferenceElementInfo cdsViewAdtObj, final TreeViewer treeViewer) {
		final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(cdsViewAdtObj.getName(),
			cdsViewAdtObj.getDisplayName(), cdsViewAdtObj.getDescription(), cdsViewAdtObj.getAdtObjectReference(), null);
		final IDestinationProvider destProvider = cdsViewAdtObj.getAdapter(IDestinationProvider.class);
		this.rootWhereUsedProvider = new WhereUsedInCdsElementInfoProvider(
			destProvider != null ? destProvider.getDestinationId() : null, cdsViewAdtObj.getName(), this.showFromUses.isChecked(),
			this.showAssocUses.isChecked());
		this.cdsWhereUsedNode = node;
		node.setElementInfoProvider(this.rootWhereUsedProvider);
		node.addLazyLoadingListener(this.lazyLoadingListener);
		treeViewer.setInput(new Object[] { node });
		treeViewer.expandAll();
	}

	@Override
	protected void fillPullDownMenu(final IMenuManager menu) {
		super.fillPullDownMenu(menu);
		menu.appendToGroup(IContextMenuConstants.GROUP_FILTERING, this.showFromUses);
		menu.appendToGroup(IContextMenuConstants.GROUP_FILTERING, this.showAssocUses);
	}

	@Override
	protected void refreshAnalysis() {
		if (this.cdsWhereUsedNode == null) {
			return;
		}
		this.cdsWhereUsedNode.resetLoadedState();
		this.rootWhereUsedCount = INDETERMINATE_COUNT;
		getViewPart().updateLabel();
		getViewer().refresh();
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
				text.append(" "); // for broader image due to overlay
				text.append(node.getDisplayName());
			}

			if (element instanceof ICollectionTreeNode) {
				final String size = ((ICollectionTreeNode) element).getSizeAsString();
				if (size != null) {
					text.append(" (" + size + ")", StyledString.COUNTER_STYLER); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			final String description = node.getDescription();
			if (description != null && !description.isEmpty()) {
				text.append("  " + description + "  ", //$NON-NLS-1$ //$NON-NLS-2$
					StylerFactory.createCustomStyler(SWT.ITALIC, JFacePreferences.DECORATIONS_COLOR, null));
			}
		}
		return text;
	}

}
