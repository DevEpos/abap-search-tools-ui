/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance
 * hierarchy. It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the
 * result of the switch. <!-- end-user-doc -->
 *
 * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage
 * @generated
 */
public class ObjectSearchFavoritesSwitch<T> extends Switch<T> {
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected static IObjectSearchFavoritesPackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  public ObjectSearchFavoritesSwitch() {
    if (modelPackage == null) {
      modelPackage = IObjectSearchFavoritesPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(final EPackage ePackage) {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a
   * non null result; it yields that result. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(final int classifierID, final EObject theEObject) {
    switch (classifierID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES: {
      IObjectSearchFavorites objectSearchFavorites = (IObjectSearchFavorites) theEObject;
      T result = caseObjectSearchFavorites(objectSearchFavorites);
      if (result == null) {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE: {
      IObjectSearchFavorite objectSearchFavorite = (IObjectSearchFavorite) theEObject;
      T result = caseObjectSearchFavorite(objectSearchFavorite);
      if (result == null) {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Object
   * Search Favorites</em>'. <!-- begin-user-doc --> This implementation returns
   * null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Object
   *         Search Favorites</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseObjectSearchFavorites(final IObjectSearchFavorites object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Object
   * Search Favorite</em>'. <!-- begin-user-doc --> This implementation returns
   * null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Object
   *         Search Favorite</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseObjectSearchFavorite(final IObjectSearchFavorite object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of
   * '<em>EObject</em>'. <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last
   * case anyway. <!-- end-user-doc -->
   *
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of
   *         '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(final EObject object) {
    return null;
  }

} // ObjectSearchFavoritesSwitch
