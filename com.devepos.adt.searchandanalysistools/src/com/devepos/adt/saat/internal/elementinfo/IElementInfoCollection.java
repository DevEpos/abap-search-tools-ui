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
	 * Returns <code>true</code> if the collection has children
	 * 
	 * @return <code>true</code> if the collection has children
	 */
	boolean hasChildren();
}
