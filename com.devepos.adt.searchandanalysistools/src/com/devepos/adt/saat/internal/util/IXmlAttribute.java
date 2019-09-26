package com.devepos.adt.saat.internal.util;

/**
 * Attribute of a XML Element
 *
 * @author stockbal
 */
public interface IXmlAttribute {

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * @return the value
	 */
	String getValue();

	/**
	 * @param value the value to set
	 */
	void setValue(String value);

	/**
	 * @return the namespace
	 */
	String getNamespace();

	/**
	 * @param namespace the namespace to set
	 */
	void setNamespace(String namespace);

}