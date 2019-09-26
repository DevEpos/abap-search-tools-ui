package com.devepos.adt.saat.internal.elementinfo;

import org.eclipse.core.runtime.IAdaptable;

import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

public interface IAdtObjectReferenceElementInfo extends IElementInfoCollection, ILazyLoadingElementInfo, IAdaptable {

	/**
	 * Signals that the ADT object reference supports lazy loading or not
	 *
	 * @return <code>true</code> if this element info of a ADT Object reference
	 *         support lazy loading
	 */
	boolean hasLazyLoadingSupport();

	/**
	 * Sets this ADT Object reference element information to lazy loading
	 *
	 * @param supportsLazyLoading
	 */
	void setLazyLoadingSupport(boolean lazyLoading);

	/**
	 * @return the ADT object reference of the element
	 */
	IAdtObjectReference getAdtObjectReference();

	/**
	 * Sets ADT object reference of this element
	 *
	 * @param objectReference the new object reference to be set
	 */
	void setAdtObjectReference(IAdtObjectReference objectReference);

}
