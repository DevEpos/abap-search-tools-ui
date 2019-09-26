package com.devepos.adt.saat.internal.util;

import java.util.List;
import java.util.Map;

/**
 * Element of a XML document
 *
 * @author stockbal
 */
public interface IXmlElement {

	/**
	 * @return the child elements of this element
	 */
	List<IXmlElement> getChildren();

	/**
	 * @return the first child of the element or <code>null</code> if it has none
	 */
	IXmlElement getFirstChild();

	/**
	 * @return <code>true</code> if the element has attributes
	 */
	boolean hasChildren();

	/**
	 * Sets the namespace prefix of the element
	 *
	 * @param prefix the prefix of this element
	 */
	void setPrefix(String prefix);

	/**
	 * Sets the namespace URI of this element
	 *
	 * @param namespaceURI the namespace URI
	 */
	void setNamespace(String namespaceURI);

	/**
	 * @return the attributes of this element
	 */
	List<IXmlAttribute> getAttributes();

	/**
	 * @return <code>true</code> if the element has attributes
	 */
	boolean hasAttributes();

	/**
	 * Retrieves the value of the attribute with the given name
	 *
	 * @param  attributeName the name of attribute
	 * @return               the value of the attribute
	 */
	String getAttributeValue(String attributeName);

	/**
	 * Retrieves the value of the attribute with the given name
	 *
	 * @param  namespacePrefix the namespace prefix
	 * @param  attributeName   the name of the attribute
	 * @return                 the value of the attribute
	 */
	String getAttributeValue(String namespacePrefix, String attributeName);

	/**
	 * @return a map of all the defined namespaces with their prefixes
	 */
	Map<String, String> getNamespaces();

	/**
	 * @return the namespace prefix of the element
	 */
	String getPrefix();

	/**
	 * @return the namespace URI of the element
	 */
	String getNamespaceURI();

	/**
	 * @return the text
	 */
	String getText();

	/**
	 * @param text the text to set
	 */
	void setText(String text);

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * Returns <code>true</code> if this element has a child element with the name
	 * <code>tagName</code>
	 *
	 * @param  tagName the element tag name to look for in this elements children
	 * @return
	 */
	boolean hasChild(String tagName);

}