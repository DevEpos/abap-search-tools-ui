package com.devepos.adt.saat.internal.elementinfo;

import java.util.Map;

/**
 * Holds a list of properties
 * 
 * @author stockbal
 */
public interface IPropertyBag {
	/**
	 * Retrieves a map of stored properties of the element information
	 *
	 * @return a map of stored properties of the element information
	 */
	Map<String, String> getProperties();

	/**
	 * Retrieves the property value for the given key or <code>null</code> if no
	 * property with the given <code>key</code> is defined in this element info
	 *
	 * @param  key the key of the property to be retrieved
	 * @return     the found property value or <code>null</code>
	 */
	String getPropertyValue(String key);
}
