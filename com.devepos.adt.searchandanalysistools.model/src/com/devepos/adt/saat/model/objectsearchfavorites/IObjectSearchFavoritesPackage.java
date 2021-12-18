/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesFactory
 * @model kind="package"
 * @generated
 */
public interface IObjectSearchFavoritesPackage extends EPackage {
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "objectsearchfavorites";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "https://www.devepos.com/adt/objectsearch/favorites";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "searchfav";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  IObjectSearchFavoritesPackage eINSTANCE = com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesPackageImpl
      .init();

  /**
   * The meta object id for the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesImpl
   * <em>Object Search Favorites</em>}' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesImpl
   * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesPackageImpl#getObjectSearchFavorites()
   * @generated
   */
  int OBJECT_SEARCH_FAVORITES = 0;

  /**
   * The feature id for the '<em><b>Object Search Favorites</b></em>' containment
   * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES = 0;

  /**
   * The number of structural features of the '<em>Object Search Favorites</em>'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITES_FEATURE_COUNT = 1;

  /**
   * The number of operations of the '<em>Object Search Favorites</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITES_OPERATION_COUNT = 0;

  /**
   * The meta object id for the
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl
   * <em>Object Search Favorite</em>}' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl
   * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesPackageImpl#getObjectSearchFavorite()
   * @generated
   */
  int OBJECT_SEARCH_FAVORITE = 1;

  /**
   * The feature id for the '<em><b>Object Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__OBJECT_NAME = 0;

  /**
   * The feature id for the '<em><b>Search Filter</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__SEARCH_FILTER = 1;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__DESCRIPTION = 2;

  /**
   * The feature id for the '<em><b>Destination Id</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__DESTINATION_ID = 3;

  /**
   * The feature id for the '<em><b>And Search Active</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE = 4;

  /**
   * The feature id for the '<em><b>Search Type</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__SEARCH_TYPE = 5;

  /**
   * The feature id for the '<em><b>Project Independent</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT = 6;

  /**
   * The feature id for the '<em><b>Max Results</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__MAX_RESULTS = 7;

  /**
   * The feature id for the '<em><b>All Results</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE__ALL_RESULTS = 8;

  /**
   * The number of structural features of the '<em>Object Search Favorite</em>'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE_FEATURE_COUNT = 9;

  /**
   * The number of operations of the '<em>Object Search Favorite</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int OBJECT_SEARCH_FAVORITE_OPERATION_COUNT = 0;

  /**
   * Returns the meta object for class
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites
   * <em>Object Search Favorites</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the meta object for class '<em>Object Search Favorites</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites
   * @generated
   */
  EClass getObjectSearchFavorites();

  /**
   * Returns the meta object for the containment reference list
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites#getObjectSearchFavorites
   * <em>Object Search Favorites</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the meta object for the containment reference list '<em>Object Search
   *         Favorites</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites#getObjectSearchFavorites()
   * @see #getObjectSearchFavorites()
   * @generated
   */
  EReference getObjectSearchFavorites_ObjectSearchFavorites();

  /**
   * Returns the meta object for class
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite
   * <em>Object Search Favorite</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the meta object for class '<em>Object Search Favorite</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite
   * @generated
   */
  EClass getObjectSearchFavorite();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getObjectName
   * <em>Object Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Object Name</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getObjectName()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_ObjectName();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchFilter
   * <em>Search Filter</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Search Filter</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchFilter()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_SearchFilter();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDescription
   * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDescription()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_Description();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDestinationId
   * <em>Destination Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Destination Id</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getDestinationId()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_DestinationId();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAndSearchActive
   * <em>And Search Active</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>And Search Active</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAndSearchActive()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_AndSearchActive();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchType
   * <em>Search Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Search Type</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getSearchType()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_SearchType();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isProjectIndependent
   * <em>Project Independent</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Project Independent</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isProjectIndependent()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_ProjectIndependent();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getMaxResults
   * <em>Max Results</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Max Results</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#getMaxResults()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_MaxResults();

  /**
   * Returns the meta object for the attribute
   * '{@link com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAllResults
   * <em>All Results</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>All Results</em>'.
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite#isAllResults()
   * @see #getObjectSearchFavorite()
   * @generated
   */
  EAttribute getObjectSearchFavorite_AllResults();

  /**
   * Returns the factory that creates the instances of the model. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  IObjectSearchFavoritesFactory getObjectSearchFavoritesFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each operation of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   *
   * @generated
   */
  interface Literals {
    /**
     * The meta object literal for the
     * '{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesImpl
     * <em>Object Search Favorites</em>}' class. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesImpl
     * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesPackageImpl#getObjectSearchFavorites()
     * @generated
     */
    EClass OBJECT_SEARCH_FAVORITES = eINSTANCE.getObjectSearchFavorites();

    /**
     * The meta object literal for the '<em><b>Object Search Favorites</b></em>'
     * containment reference list feature. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @generated
     */
    EReference OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES = eINSTANCE
        .getObjectSearchFavorites_ObjectSearchFavorites();

    /**
     * The meta object literal for the
     * '{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl
     * <em>Object Search Favorite</em>}' class. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl
     * @see com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesPackageImpl#getObjectSearchFavorite()
     * @generated
     */
    EClass OBJECT_SEARCH_FAVORITE = eINSTANCE.getObjectSearchFavorite();

    /**
     * The meta object literal for the '<em><b>Object Name</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__OBJECT_NAME = eINSTANCE.getObjectSearchFavorite_ObjectName();

    /**
     * The meta object literal for the '<em><b>Search Filter</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__SEARCH_FILTER = eINSTANCE
        .getObjectSearchFavorite_SearchFilter();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__DESCRIPTION = eINSTANCE
        .getObjectSearchFavorite_Description();

    /**
     * The meta object literal for the '<em><b>Destination Id</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__DESTINATION_ID = eINSTANCE
        .getObjectSearchFavorite_DestinationId();

    /**
     * The meta object literal for the '<em><b>And Search Active</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE = eINSTANCE
        .getObjectSearchFavorite_AndSearchActive();

    /**
     * The meta object literal for the '<em><b>Search Type</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__SEARCH_TYPE = eINSTANCE.getObjectSearchFavorite_SearchType();

    /**
     * The meta object literal for the '<em><b>Project Independent</b></em>'
     * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT = eINSTANCE
        .getObjectSearchFavorite_ProjectIndependent();

    /**
     * The meta object literal for the '<em><b>Max Results</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__MAX_RESULTS = eINSTANCE.getObjectSearchFavorite_MaxResults();

    /**
     * The meta object literal for the '<em><b>All Results</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EAttribute OBJECT_SEARCH_FAVORITE__ALL_RESULTS = eINSTANCE.getObjectSearchFavorite_AllResults();

  }

} // IObjectSearchFavoritesPackage
