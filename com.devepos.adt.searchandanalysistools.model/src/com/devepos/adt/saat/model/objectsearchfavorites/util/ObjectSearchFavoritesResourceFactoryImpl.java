/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * <!-- begin-user-doc --> The <b>Resource Factory</b> associated with the
 * package. <!-- end-user-doc -->
 *
 * @see com.devepos.adt.saat.model.objectsearchfavorites.util.ObjectSearchFavoritesResourceImpl
 * @generated
 */
public class ObjectSearchFavoritesResourceFactoryImpl extends ResourceFactoryImpl {
  /**
   * Creates an instance of the resource factory. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public ObjectSearchFavoritesResourceFactoryImpl() {
    super();
  }

  /**
   * Creates an instance of the resource. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  @Override
  public Resource createResource(final URI uri) {
    Resource result = new ObjectSearchFavoritesResourceImpl(uri);
    return result;
  }

} // ObjectSearchFavoritesResourceFactoryImpl
