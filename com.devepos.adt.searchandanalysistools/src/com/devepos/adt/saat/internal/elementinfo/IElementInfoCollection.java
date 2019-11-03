package com.devepos.adt.saat.internal.elementinfo;

import java.util.List;

/**
 * Holds a collection of element infos
 *
 * @author stockbal
 */
public interface IElementInfoCollection extends IElementInfo {

	/**
	 * @return a list of {@link IElementInfo} references
	 */
	List<IElementInfo> getChildren();

	/**
	 * Returns the size of this element collection
	 * 
	 * @return the size of this element collection
	 */
	int size();

	/**
	 * Returns the child element with the given name or <code>null</code> if it does
	 * not exist
	 *
	 * @param  name the name of child element
	 * @return      the child element with the given name or <code>null</code> if it
	 *              does not exist
	 */
	IElementInfo getChild(String name);

	/**
	 * Returns <code>true</code> if the collection has a child the the given name
	 *
	 * @param  name the name of a child
	 * @return      <code>true</code> if the collection has a child the the given
	 *              name
	 */
	boolean hasChild(String name);

	/**
	 * Returns <code>true</code> if the collection has children
	 *
	 * @return <code>true</code> if the collection has children
	 */
	boolean hasChildren();
}
