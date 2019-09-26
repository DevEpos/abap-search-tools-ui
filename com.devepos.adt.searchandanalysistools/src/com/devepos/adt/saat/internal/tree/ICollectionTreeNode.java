package com.devepos.adt.saat.internal.tree;

import java.util.List;

/**
 * Represents a collection of {@link ITreeNode}
 *
 * @author stockbal
 */
public interface ICollectionTreeNode extends ITreeNode {

	/**
	 * Returns the list of child nodes of this node
	 *
	 * @return a {@link List} of child nodes of this collection node
	 */
	List<ITreeNode> getChildren();

	/**
	 * Returns <code>true</code> if the node has children
	 *
	 * @return <code>true</code> if the node has children
	 */
	boolean hasChildren();

	/**
	 * Sets the child nodes of this collection node
	 *
	 * @param children List of child nodes
	 */
	void setChildren(List<ITreeNode> children);

	/**
	 * Returns the number of <code>nodes</code> in this collection in a readable
	 * format
	 *
	 * @return the current size as a String
	 */
	String getSizeAsString();

	/**
	 * Returns the size of this collection
	 *
	 * @return the number of entries in this collection
	 */
	int size();
}
