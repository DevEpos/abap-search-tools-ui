package com.devepos.adt.saat.internal.navtargets;

import com.devepos.adt.base.ObjectType;

/**
 * Service which handles the retrieval of available navigation targets for ADT
 * objects
 *
 * @author stockbal
 */
public interface INavigationTargetService {

  /**
   * Retrieves a list of navigation targets for the given object name and type or
   * <code>null</code> if none could be determined
   *
   * @param objectName the name of the object
   * @param type       the type of the object
   * @return
   */
  INavigationTarget[] getTargets(String objectName, ObjectType type);
}
