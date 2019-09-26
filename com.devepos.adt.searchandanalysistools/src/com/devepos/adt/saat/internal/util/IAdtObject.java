package com.devepos.adt.saat.internal.util;

import org.eclipse.core.resources.IProject;

import com.devepos.adt.saat.ObjectType;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Simple representation of an ADT Object
 *
 * @author stockbal
 */
public interface IAdtObject {

	/**
	 * Returns the name of the object
	 *
	 * @return the name of the object
	 */
	String getName();

	/**
	 * Returns the object type of the ADT object
	 *
	 * @return the type of the object
	 */
	ObjectType getObjectType();

	/**
	 * Returns the project connected to this object
	 *
	 * @return the ABAP project of the object
	 */
	IProject getProject();

	/**
	 * Returns the reference to the ADT object
	 * 
	 * @return
	 */
	IAdtObjectReference getReference();
}
