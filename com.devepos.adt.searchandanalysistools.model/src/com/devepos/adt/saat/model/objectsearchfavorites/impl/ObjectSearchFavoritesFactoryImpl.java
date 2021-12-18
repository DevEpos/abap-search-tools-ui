/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesFactory;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class ObjectSearchFavoritesFactoryImpl extends EFactoryImpl implements
    IObjectSearchFavoritesFactory {
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public static IObjectSearchFavoritesFactory init() {
    try {
      IObjectSearchFavoritesFactory theObjectSearchFavoritesFactory = (IObjectSearchFavoritesFactory) EPackage.Registry.INSTANCE
          .getEFactory(IObjectSearchFavoritesPackage.eNS_URI);
      if (theObjectSearchFavoritesFactory != null) {
        return theObjectSearchFavoritesFactory;
      }
    } catch (Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new ObjectSearchFavoritesFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  public ObjectSearchFavoritesFactoryImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EObject create(final EClass eClass) {
    switch (eClass.getClassifierID()) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES:
      return createObjectSearchFavorites();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE:
      return createObjectSearchFavorite();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName()
          + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IObjectSearchFavorites createObjectSearchFavorites() {
    ObjectSearchFavoritesImpl objectSearchFavorites = new ObjectSearchFavoritesImpl();
    return objectSearchFavorites;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IObjectSearchFavorite createObjectSearchFavorite() {
    ObjectSearchFavoriteImpl objectSearchFavorite = new ObjectSearchFavoriteImpl();
    return objectSearchFavorite;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IObjectSearchFavoritesPackage getObjectSearchFavoritesPackage() {
    return (IObjectSearchFavoritesPackage) getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @deprecated
   * @generated
   */
  @Deprecated
  public static IObjectSearchFavoritesPackage getPackage() {
    return IObjectSearchFavoritesPackage.eINSTANCE;
  }

} // ObjectSearchFavoritesFactoryImpl
