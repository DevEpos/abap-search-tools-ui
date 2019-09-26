package com.devepos.adt.saat.internal.tree;

import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoCollection;
import com.devepos.adt.saat.internal.elementinfo.IExecutableElementInfo;
import com.devepos.adt.saat.internal.elementinfo.ILazyLoadingElementInfo;

/**
 * Utility class for creating Tree nodes
 *
 * @author stockbal
 */
public class TreeNodeCreator {

	private final ICollectionTreeNode treeNode;

	/**
	 * Creates new {@link TreeNodeCreator} instance for the given
	 * <code>treeNode</code>
	 *
	 * @param treeNode      the tree node for which sub nodes should be created
	 * @param destinationId the destination
	 */
	public TreeNodeCreator(final ICollectionTreeNode treeNode) {
		this.treeNode = treeNode;
		if (this.treeNode.getChildren() == null) {
			this.treeNode.setChildren(new ArrayList<>());
		}
	}

	/**
	 * Create sub nodes for the given element information
	 *
	 * @param elementInfo information about an element
	 */
	public void createSubNodes(final IElementInfo elementInfo) {
		if (elementInfo == null) {
			return;
		}
		addNode(elementInfo, this.treeNode);
	}

	/**
	 * Creates sub nodes for the given list of element information
	 *
	 * @param elementInfos a list of element information objects
	 */
	public void createSubNodes(final List<IElementInfo> elementInfos) {
		if (elementInfos == null) {
			return;
		}
		for (final IElementInfo elementInfo : elementInfos) {
			createSubNodes(elementInfo);
		}
	}

	protected void addNode(final IElementInfo elementInfo, final ICollectionTreeNode parent) {
		if (elementInfo instanceof IAdtObjectReferenceElementInfo) {
			addAdtObjectRefNode((IAdtObjectReferenceElementInfo) elementInfo, parent);
		} else if (elementInfo instanceof IElementInfoCollection) {
			addCollection((IElementInfoCollection) elementInfo, parent);
		} else if (elementInfo instanceof IExecutableElementInfo) {
			addActionNode((IExecutableElementInfo) elementInfo, parent);
		} else if (elementInfo instanceof ILazyLoadingElementInfo) {
			addLazyLoadingFolder((ILazyLoadingElementInfo) elementInfo, parent);
		} else {
			addSimpleNode(elementInfo, parent);
		}
	}

	protected void addLazyLoadingFolder(final ILazyLoadingElementInfo lazyLoadingElement, final ICollectionTreeNode parent) {
		final ICollectionTreeNode lazyFolder = new LazyLoadingFolderNode(lazyLoadingElement.getName(),
			lazyLoadingElement.getDisplayName(), lazyLoadingElement.getElementInfoProvider(), lazyLoadingElement.getImageId(),
			null, parent);
		lazyFolder.setAdditionalInfo(lazyLoadingElement.getAdditionalInfo());
		lazyFolder.getProperties().putAll(lazyLoadingElement.getProperties());
		((ILazyLoadingNode) lazyFolder).setContentRefreshMode(lazyLoadingElement.getContentRefreshMode());
		if (lazyFolder != null) {
			parent.getChildren().add(lazyFolder);
		}

	}

	protected void addCollection(final IElementInfoCollection collection, final ICollectionTreeNode parentNode) {
		final ICollectionTreeNode collectionTreeNode = new FolderTreeNode(collection.getDisplayName(), parentNode,
			collection.getImageId(), null);
		collectionTreeNode.setAdditionalInfo(collection.getAdditionalInfo());
		collectionTreeNode.getProperties().putAll(collection.getProperties());
		for (final IElementInfo collectionElement : collection.getChildren()) {
			addNode(collectionElement, collectionTreeNode);
		}
		parentNode.getChildren().add(collectionTreeNode);
	}

	protected void addAdtObjectRefNode(final IAdtObjectReferenceElementInfo adtObjElementInfo,
		final ICollectionTreeNode collection) {
		IAdtObjectReferenceNode adtObjectRefNode = null;
		if (adtObjElementInfo.hasLazyLoadingSupport()) {
			adtObjectRefNode = new LazyLoadingAdtObjectReferenceNode(adtObjElementInfo.getName(),
				adtObjElementInfo.getDisplayName(), adtObjElementInfo.getDescription(), adtObjElementInfo.getAdtObjectReference(),
				collection);
			final ILazyLoadingNode lazyLoadingNode = (ILazyLoadingNode) adtObjectRefNode;
			lazyLoadingNode.setContentRefreshMode(adtObjElementInfo.getContentRefreshMode());
			lazyLoadingNode.setElementInfoProvider(adtObjElementInfo.getElementInfoProvider());
		} else {
			adtObjectRefNode = new AdtObjectReferenceNode(adtObjElementInfo.getName(), adtObjElementInfo.getDisplayName(),
				adtObjElementInfo.getDescription(), adtObjElementInfo.getAdtObjectReference(), collection);
			if (adtObjElementInfo.hasChildren()) {
				for (final IElementInfo child : adtObjElementInfo.getChildren()) {
					addNode(child, adtObjectRefNode);
				}
			}
		}
		adtObjectRefNode.setAdditionalInfo(adtObjElementInfo.getAdditionalInfo());
		adtObjectRefNode.getProperties().putAll(adtObjElementInfo.getProperties());
		collection.getChildren().add(adtObjectRefNode);
	}

	protected void addSimpleNode(final IElementInfo elementInfo, final ICollectionTreeNode collection) {
		final ITreeNode simpleNode = new SimpleInfoTreeNode(elementInfo.getName(), elementInfo.getDisplayName(),
			elementInfo.getImageId(), elementInfo.getDescription(), collection);
		simpleNode.setAdditionalInfo(elementInfo.getAdditionalInfo());
		simpleNode.getProperties().putAll(elementInfo.getProperties());
		collection.getChildren().add(simpleNode);

	}

	protected void addActionNode(final IExecutableElementInfo elementInfo, final ICollectionTreeNode collection) {
		final ITreeNode actionNode = new ActionTreeNode(elementInfo.getName(), elementInfo.getImageId(), collection,
			elementInfo.getExecutable());
		actionNode.setAdditionalInfo(elementInfo.getAdditionalInfo());
		actionNode.getProperties().putAll(elementInfo.getProperties());
		collection.getChildren().add(actionNode);

	}
}
