package com.devepos.adt.saat.internal.elementinfo;

import java.util.List;

import com.devepos.adt.saat.ObjectType;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

public interface IElementInfoRetrievalService {

	/**
	 * Retrieves column information for the given object name
	 *
	 * @param  destinationId the destination of the project to be used
	 * @param  objectName    the name of the object
	 * @param  type          the type of the object
	 * @return               the found column information
	 */
	List<IElementInfo> getElementColumnInformation(String destinationId, String objectName, ObjectType type);

	/**
	 * Retrieves an ADT object reference for the given column in the given entity
	 *
	 * @param  destinationId the project destination id to be used
	 * @param  objectName    the name of the object
	 * @param  column        the name of the column
	 * @return
	 */
	IAdtObjectReference getColumnUri(String destinationId, String objectName, String column);

	/**
	 * Retrieves some basic information about the given object
	 *
	 * @param  destinationId the destination id of the ABAP Project
	 * @param  objectName    the name of the object for which the element
	 *                       information should be retrieved
	 * @param  objectType    the type of the object
	 * @return               the found element information of the ADT object or
	 *                       <code>null</code>
	 */
	IAdtObjectReferenceElementInfo retrieveBasicElementInformation(final String destinationId, String objectName,
		ObjectType objectType);

	/**
	 * Retrieves the element information for the given ADT object reference
	 *
	 * @param  destinationId   destination Id of an ABAP project
	 * @param  objectReference the object reference for which the element
	 *                         information should be retrieved
	 * @return                 the found element information or <code>null</code>
	 */
	IAdtObjectReferenceElementInfo retrieveElementInformation(final String destinationId, IAdtObjectReference objectReference);

	/**
	 * Retrieve secondary element information about the given CDS view
	 *
	 * @param  destinationId destination Id of an ABAP project
	 * @param  cdsViewName   name of a CDS view
	 * @return               the found secondary element information
	 */
	IElementInfoCollection retrieveCDSSecondaryElements(final String destinationId, final String cdsViewName);

}