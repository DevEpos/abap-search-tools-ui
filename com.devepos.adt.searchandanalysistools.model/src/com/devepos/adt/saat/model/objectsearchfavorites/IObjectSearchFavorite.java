/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Object
 * Search Favorite</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getObjectName
 * <em>Object Name</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchFilter
 * <em>Search Filter</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDescription
 * <em>Description</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDestinationId
 * <em>Destination Id</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAndSearchActive
 * <em>And Search Active</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchType
 * <em>Search Type</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isProjectIndependent
 * <em>Project Independent</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getMaxResults
 * <em>Max Results</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAllResults
 * <em>All Results</em>}</li>
 * </ul>
 *
 * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite()
 * @model
 * @generated
 */
public interface IObjectSearchFavorite extends EObject {
  /**
   * Returns the value of the '<em><b>Object Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Object Name</em>' attribute.
   * @see #setObjectName(String)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_ObjectName()
   * @model
   * @generated
   */
  String getObjectName();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getObjectName
   * <em>Object Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Object Name</em>' attribute.
   * @see #getObjectName()
   * @generated
   */
  void setObjectName(String value);

  /**
   * Returns the value of the '<em><b>Search Filter</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Search Filter</em>' attribute.
   * @see #setSearchFilter(String)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_SearchFilter()
   * @model
   * @generated
   */
  String getSearchFilter();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchFilter
   * <em>Search Filter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Search Filter</em>' attribute.
   * @see #getSearchFilter()
   * @generated
   */
  void setSearchFilter(String value);

  /**
   * Returns the value of the '<em><b>Description</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Description</em>' attribute.
   * @see #setDescription(String)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDescription
   * <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

  /**
   * Returns the value of the '<em><b>Destination Id</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Destination Id</em>' attribute.
   * @see #setDestinationId(String)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_DestinationId()
   * @model
   * @generated
   */
  String getDestinationId();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDestinationId
   * <em>Destination Id</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value the new value of the '<em>Destination Id</em>' attribute.
   * @see #getDestinationId()
   * @generated
   */
  void setDestinationId(String value);

  /**
   * Returns the value of the '<em><b>And Search Active</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>And Search Active</em>' attribute.
   * @see #setAndSearchActive(boolean)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_AndSearchActive()
   * @model
   * @generated
   */
  boolean isAndSearchActive();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAndSearchActive
   * <em>And Search Active</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value the new value of the '<em>And Search Active</em>' attribute.
   * @see #isAndSearchActive()
   * @generated
   */
  void setAndSearchActive(boolean value);

  /**
   * Returns the value of the '<em><b>Search Type</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Search Type</em>' attribute.
   * @see #setSearchType(String)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_SearchType()
   * @model
   * @generated
   */
  String getSearchType();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchType
   * <em>Search Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Search Type</em>' attribute.
   * @see #getSearchType()
   * @generated
   */
  void setSearchType(String value);

  /**
   * Returns the value of the '<em><b>Project Independent</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Project Independent</em>' attribute.
   * @see #setProjectIndependent(boolean)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_ProjectIndependent()
   * @model
   * @generated
   */
  boolean isProjectIndependent();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isProjectIndependent
   * <em>Project Independent</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value the new value of the '<em>Project Independent</em>' attribute.
   * @see #isProjectIndependent()
   * @generated
   */
  void setProjectIndependent(boolean value);

  /**
   * Returns the value of the '<em><b>Max Results</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Max Results</em>' attribute.
   * @see #setMaxResults(int)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_MaxResults()
   * @model
   * @generated
   */
  int getMaxResults();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getMaxResults
   * <em>Max Results</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Max Results</em>' attribute.
   * @see #getMaxResults()
   * @generated
   */
  void setMaxResults(int value);

  /**
   * Returns the value of the '<em><b>All Results</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>All Results</em>' attribute.
   * @see #setAllResults(boolean)
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#getObjectSearchFavorite_AllResults()
   * @model
   * @generated
   */
  boolean isAllResults();

  /**
   * Sets the value of the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAllResults
   * <em>All Results</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>All Results</em>' attribute.
   * @see #isAllResults()
   * @generated
   */
  void setAllResults(boolean value);

} // IObjectSearchFavorite
