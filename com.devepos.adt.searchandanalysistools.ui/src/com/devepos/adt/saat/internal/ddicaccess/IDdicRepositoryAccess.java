package com.devepos.adt.saat.internal.ddicaccess;

import java.util.List;

import com.devepos.adt.base.elementinfo.IElementInfo;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Repository Access for DDIC Objects
 *
 * @author stockbal
 */
public interface IDdicRepositoryAccess {
  /**
   * Retrieves column information for the given object name
   *
   * @param destinationId the destination of the project to be used
   * @param objectUri     the URI for the ADT Object
   * @return the found column information
   */
  List<IElementInfo> getElementColumnInformation(String destinationId, String objectUri);

  /**
   * Retrieves an ADT object reference for the given column in the given entity
   *
   * @param destinationId the project destination id to be used
   * @param objectName    the name of the object
   * @param column        the name of the column
   * @return
   */
  IAdtObjectReference getColumnUri(String destinationId, String objectName, String column);
}
