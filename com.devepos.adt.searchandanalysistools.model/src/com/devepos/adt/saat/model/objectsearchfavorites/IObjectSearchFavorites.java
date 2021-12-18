/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Object
 * Search Favorites</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites#getObjectSearchFavorites
 * <em>Object Search Favorites</em>}</li>
 * </ul>
 *
 * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorites()
 * @model
 * @generated
 */
public interface IObjectSearchFavorites extends EObject {
  /**
   * Returns the value of the '<em><b>Object Search Favorites</b></em>'
   * containment reference list. The list contents are of type
   * {@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Object Search Favorites</em>' containment
   *         reference list.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorites_ObjectSearchFavorites()
   * @model containment="true" required="true"
   * @generated
   */
  EList<IObjectSearchFavorite> getObjectSearchFavorites();

} // IObjectSearchFavorites
