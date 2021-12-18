package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Service for reading element information
 *
 * @author stockbal
 */
public interface IElementInfoRetrievalService {

  /**
   * Retrieves some basic information about the given object
   *
   * @param destinationId the destination id of the ABAP Project
   * @param objectName    the name of the object for which the element information
   *                      should be retrieved
   * @param objectType    the type of the object
   * @return the found element information of the ADT object or <code>null</code>
   */
  IAdtObjectReferenceElementInfo retrieveBasicElementInformation(final String destinationId,
      String objectName, ObjectType objectType);

  /**
   * Retrieves some basic information about the given object
   *
   * @param destinationId the destination id of the ABAP Project
   * @param uri           the URI of an ADT object
   * @return the found element information of the ADT object or <code>null</code>
   */
  IAdtObjectReferenceElementInfo retrieveBasicElementInformation(final String destinationId,
      String uri);

  /**
   * Retrieves the element information for the given ADT object reference
   *
   * @param destinationId   destination Id of an ABAP project
   * @param objectReference the object reference for which the element information
   *                        should be retrieved
   * @return the found element information or <code>null</code>
   */
  IAdtObjectReferenceElementInfo retrieveElementInformation(final String destinationId,
      IAdtObjectReference objectReference);

  /**
   * Retrieve secondary element information about the given CDS view
   *
   * @param destinationId destination Id of an ABAP project
   * @param cdsViewName   name of a CDS view
   * @return the found secondary element information
   */
  IElementInfoCollection retrieveCDSSecondaryElements(final String destinationId,
      final String cdsViewName);

}