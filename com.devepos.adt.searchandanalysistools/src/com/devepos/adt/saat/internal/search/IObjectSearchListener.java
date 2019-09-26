package com.devepos.adt.saat.internal.search;

import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;

/**
 * Interface for listening for search updates
 *
 * @author stockbal
 */
@FunctionalInterface
public interface IObjectSearchListener {

	/**
	 * Will be called when the search is finished
	 *
	 * @param searchResult   the objects that were found during the search
	 * @param definedMaxRows the maximum number of rows that were defined for the
	 *                       search
	 */
	void searchFinished(IAdtObjectReferenceNode[] searchResult, int definedMaxRows);
}
