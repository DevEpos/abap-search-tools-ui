/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorites;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Object
 * Search Favorites</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoritesImpl#getObjectSearchFavorites
 * <em>Object Search Favorites</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ObjectSearchFavoritesImpl extends MinimalEObjectImpl.Container implements
    IObjectSearchFavorites {
  /**
   * The cached value of the '{@link #getObjectSearchFavorites() <em>Object Search
   * Favorites</em>}' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getObjectSearchFavorites()
   * @generated
   * @ordered
   */
  protected EList<IObjectSearchFavorite> objectSearchFavorites;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ObjectSearchFavoritesImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return IObjectSearchFavoritesPackage.Literals.OBJECT_SEARCH_FAVORITES;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<IObjectSearchFavorite> getObjectSearchFavorites() {
    if (objectSearchFavorites == null) {
      objectSearchFavorites = new EObjectContainmentEList<>(IObjectSearchFavorite.class, this,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES);
    }
    return objectSearchFavorites;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
      final NotificationChain msgs) {
    switch (featureID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES:
      return ((InternalEList<?>) getObjectSearchFavorites()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES:
      return getObjectSearchFavorites();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES:
      getObjectSearchFavorites().clear();
      getObjectSearchFavorites().addAll((Collection<? extends IObjectSearchFavorite>) newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES:
      getObjectSearchFavorites().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITES__OBJECT_SEARCH_FAVORITES:
      return objectSearchFavorites != null && !objectSearchFavorites.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ObjectSearchFavoritesImpl
