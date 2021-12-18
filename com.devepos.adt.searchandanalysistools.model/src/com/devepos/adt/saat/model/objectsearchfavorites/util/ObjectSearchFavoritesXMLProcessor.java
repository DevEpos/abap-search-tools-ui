/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites.util;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;

import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage;

/**
 * This class contains helper methods to serialize and deserialize XML documents
 * <!-- begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class ObjectSearchFavoritesXMLProcessor extends XMLProcessor {

  /**
   * Public constructor to instantiate the helper. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public ObjectSearchFavoritesXMLProcessor() {
    super(EPackage.Registry.INSTANCE);
    IObjectSearchFavoritesPackage.eINSTANCE.eClass();
  }

  /**
   * Register for "*" and "xml" file extensions the
   * ObjectSearchFavoritesResourceFactoryImpl factory. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected Map<String, Resource.Factory> getRegistrations() {
    if (registrations == null) {
      super.getRegistrations();
      registrations.put(XML_EXTENSION, new ObjectSearchFavoritesResourceFactoryImpl());
      registrations.put(STAR_EXTENSION, new ObjectSearchFavoritesResourceFactoryImpl());
    }
    return registrations;
  }

} // ObjectSearchFavoritesXMLProcessor
