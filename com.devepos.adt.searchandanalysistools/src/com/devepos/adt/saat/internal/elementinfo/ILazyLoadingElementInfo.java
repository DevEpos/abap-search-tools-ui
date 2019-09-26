package com.devepos.adt.saat.internal.elementinfo;

/**
 * Element information objects that supports lazy loading
 *
 * @author stockbal
 */
public interface ILazyLoadingElementInfo extends IElementInfo, ILazyLoadableContent {

	/**
	 * Retrieves the provider to load the elements of this collection
	 *
	 * @return the element provider of this collection
	 */
	IElementInfoProvider getElementInfoProvider();

	/**
	 * Sets the element info provider of this element information object
	 *
	 * @param infoProvider the info provider
	 */
	void setElementInfoProvider(IElementInfoProvider infoProvider);
}
