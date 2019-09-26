package com.devepos.adt.saat.internal.elementinfo;

import java.util.List;

/**
 * Provider for Retrieving element information
 *
 * @author stockbal
 */
public interface IElementInfoProvider {

	/**
	 * Reads the elements considering the retrieval rules of this element
	 * information provider
	 *
	 * @return
	 */
	List<IElementInfo> getElements();

	/**
	 * Returns a descriptive text for the element information provider
	 * 
	 * @return
	 */
	String getProviderDescription();
}
