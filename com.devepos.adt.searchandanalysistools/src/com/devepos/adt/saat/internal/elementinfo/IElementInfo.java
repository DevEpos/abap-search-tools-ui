package com.devepos.adt.saat.internal.elementinfo;

/**
 * Information about a search result element
 *
 * @author stockbal
 */
public interface IElementInfo extends IPropertyBag {
	/**
	 * @return the name of the element
	 */
	String getName();

	/**
	 * Sets the name of the element info
	 *
	 * @param name the name to be set
	 */
	void setName(String name);

	/**
	 * @return the image id of this element
	 */
	String getImageId();

	/**
	 * Sets the image id to be associated with this element
	 * 
	 * @param imageId
	 */
	void setImageId(String imageId);

	/**
	 * @return the display name of the element
	 */
	String getDisplayName();

	/**
	 * Sets the display name of the element information
	 *
	 * @param displayName the display name to be set
	 */
	void setDisplayName(String displayName);

	/**
	 * @return the description of the element
	 */
	String getDescription();

	/**
	 * Sets the description of the element info
	 *
	 * @param description the description to be set
	 */
	void setDescription(String description);

	/**
	 * Sets additional information object
	 *
	 * @param info
	 */
	void setAdditionalInfo(Object info);

	/**
	 * Returns the additional info object
	 *
	 * @return
	 */
	Object getAdditionalInfo();

	/**
	 * Returns <code>true</code> if this element information has an object with
	 * additional information
	 *
	 * @return <code>true</code> if this element information has an object with
	 *         additional information
	 */
	boolean hasAdditionalInfo();
}
