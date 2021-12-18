/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesFactory;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class ObjectSearchFavoritesPackageImpl extends EPackageImpl implements
    IObjectSearchFavoritesPackage {
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass objectSearchFavoritesEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass objectSearchFavoriteEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the
   * package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method
   * {@link #init init()}, which also performs initialization of the package, or
   * returns the registered package, if one already exists. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private ObjectSearchFavoritesPackageImpl() {
    super(eNS_URI, IObjectSearchFavoritesFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and
   * for any others upon which it depends.
   *
   * <p>
   * This method is used to initialize
   * {@link IObjectSearchFavoritesPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access
   * that field to obtain the package. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static IObjectSearchFavoritesPackage init() {
    if (isInited) {
      return (IObjectSearchFavoritesPackage) EPackage.Registry.INSTANCE.getEPackage(
          IObjectSearchFavoritesPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredObjectSearchFavoritesPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    ObjectSearchFavoritesPackageImpl theObjectSearchFavoritesPackage = registeredObjectSearchFavoritesPackage instanceof ObjectSearchFavoritesPackageImpl
        ? (ObjectSearchFavoritesPackageImpl) registeredObjectSearchFavoritesPackage
        : new ObjectSearchFavoritesPackageImpl();

    isInited = true;

    // Create package meta-data objects
    theObjectSearchFavoritesPackage.createPackageContents();

    // Initialize created meta-data
    theObjectSearchFavoritesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theObjectSearchFavoritesPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(IObjectSearchFavoritesPackage.eNS_URI,
        theObjectSearchFavoritesPackage);
    return theObjectSearchFavoritesPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getObjectSearchFavorites() {
    return objectSearchFavoritesEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getObjectSearchFavorites_ObjectSearchFavorites() {
    return (EReference) objectSearchFavoritesEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getObjectSearchFavorite() {
    return objectSearchFavoriteEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_ObjectName() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_SearchFilter() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_Description() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_DestinationId() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_AndSearchActive() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_SearchType() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_ProjectIndependent() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_MaxResults() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getObjectSearchFavorite_AllResults() {
    return (EAttribute) objectSearchFavoriteEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IObjectSearchFavoritesFactory getObjectSearchFavoritesFactory() {
    return (IObjectSearchFavoritesFactory) getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to
   * have no affect on any invocation but its first. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public void createPackageContents() {
    if (isCreated) {
      return;
    }
    isCreated = true;

    // Create classes and their features
    objectSearchFavoritesEClass = createEClass(OBJECT_SEARCH_FAVORITES);
    createEReference(objectSearchFavoritesEClass, OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES);

    objectSearchFavoriteEClass = createEClass(OBJECT_SEARCH_FAVORITE);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__OBJECT_NAME);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__SEARCH_FILTER);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__DESCRIPTION);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__DESTINATION_ID);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__SEARCH_TYPE);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__MAX_RESULTS);
    createEAttribute(objectSearchFavoriteEClass, OBJECT_SEARCH_FAVORITE__ALL_RESULTS);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is
   * guarded to have no affect on any invocation but its first. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void initializePackageContents() {
    if (isInitialized) {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes, features, and operations; add parameters
    initEClass(objectSearchFavoritesEClass, IObjectSearchFavorites.class, "ObjectSearchFavorites",
        !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getObjectSearchFavorites_ObjectSearchFavorites(), getObjectSearchFavorite(),
        null, "objectSearchFavorites", null, 1, -1, IObjectSearchFavorites.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(objectSearchFavoriteEClass, IObjectSearchFavorite.class, "ObjectSearchFavorite",
        !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getObjectSearchFavorite_ObjectName(), ecorePackage.getEString(), "objectName",
        null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_SearchFilter(), ecorePackage.getEString(),
        "searchFilter", null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_Description(), ecorePackage.getEString(), "description",
        null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_DestinationId(), ecorePackage.getEString(),
        "destinationId", null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_AndSearchActive(), ecorePackage.getEBoolean(),
        "andSearchActive", null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_SearchType(), ecorePackage.getEString(), "searchType",
        null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_ProjectIndependent(), ecorePackage.getEBoolean(),
        "projectIndependent", null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_MaxResults(), ecorePackage.getEInt(), "maxResults", null,
        0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getObjectSearchFavorite_AllResults(), ecorePackage.getEBoolean(), "allResults",
        null, 0, 1, IObjectSearchFavorite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // ObjectSearchFavoritesPackageImpl
