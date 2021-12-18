/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model. <!-- end-user-doc -->
 *
 * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage
 * @generated
 */
public interface IObjectSearchFavoritesFactory extends EFactory {
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  IObjectSearchFavoritesFactory eINSTANCE = com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesFactoryImpl
      .init();

  /**
   * Returns a new object of class '<em>Object Search Favorites</em>'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Object Search Favorites</em>'.
   * @generated
   */
  IObjectSearchFavorites createObjectSearchFavorites();

  /**
   * Returns a new object of class '<em>Object Search Favorite</em>'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Object Search Favorite</em>'.
   * @generated
   */
  IObjectSearchFavorite createObjectSearchFavorite();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the package supported by this factory.
   * @generated
   */
  IObjectSearchFavoritesPackage getObjectSearchFavoritesPackage();

} // IObjectSearchFavoritesFactory
