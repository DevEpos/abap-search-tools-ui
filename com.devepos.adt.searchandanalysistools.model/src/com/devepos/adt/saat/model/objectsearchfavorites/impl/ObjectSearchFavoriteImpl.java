/**
 */
package com.devepos.adt.saat.model.objectsearchfavorites.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavorite;
import com.devepos.adt.saat.model.objectsearchfavorites.IObjectSearchFavoritesPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Object
 * Search Favorite</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#getObjectName
 * <em>Object Name</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#getSearchFilter
 * <em>Search Filter</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#getDescription
 * <em>Description</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#getDestinationId
 * <em>Destination Id</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#isAndSearchActive
 * <em>And Search Active</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#getSearchType
 * <em>Search Type</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#isProjectIndependent
 * <em>Project Independent</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#getMaxResults
 * <em>Max Results</em>}</li>
 * <li>{@link com.devepos.adt.saat.model.objectsearchfavorites.impl.ObjectSearchFavoriteImpl#isAllResults
 * <em>All Results</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ObjectSearchFavoriteImpl extends MinimalEObjectImpl.Container implements
    IObjectSearchFavorite {
  /**
   * The default value of the '{@link #getObjectName() <em>Object Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getObjectName()
   * @generated
   * @ordered
   */
  protected static final String OBJECT_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getObjectName() <em>Object Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getObjectName()
   * @generated
   * @ordered
   */
  protected String objectName = OBJECT_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getSearchFilter() <em>Search Filter</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSearchFilter()
   * @generated
   * @ordered
   */
  protected static final String SEARCH_FILTER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSearchFilter() <em>Search Filter</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSearchFilter()
   * @generated
   * @ordered
   */
  protected String searchFilter = SEARCH_FILTER_EDEFAULT;

  /**
   * The default value of the '{@link #getDescription() <em>Description</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected static final String DESCRIPTION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDescription() <em>Description</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected String description = DESCRIPTION_EDEFAULT;

  /**
   * The default value of the '{@link #getDestinationId() <em>Destination
   * Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDestinationId()
   * @generated
   * @ordered
   */
  protected static final String DESTINATION_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDestinationId() <em>Destination Id</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDestinationId()
   * @generated
   * @ordered
   */
  protected String destinationId = DESTINATION_ID_EDEFAULT;

  /**
   * The default value of the '{@link #isAndSearchActive() <em>And Search
   * Active</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isAndSearchActive()
   * @generated
   * @ordered
   */
  protected static final boolean AND_SEARCH_ACTIVE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isAndSearchActive() <em>And Search
   * Active</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isAndSearchActive()
   * @generated
   * @ordered
   */
  protected boolean andSearchActive = AND_SEARCH_ACTIVE_EDEFAULT;

  /**
   * The default value of the '{@link #getSearchType() <em>Search Type</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSearchType()
   * @generated
   * @ordered
   */
  protected static final String SEARCH_TYPE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSearchType() <em>Search Type</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSearchType()
   * @generated
   * @ordered
   */
  protected String searchType = SEARCH_TYPE_EDEFAULT;

  /**
   * The default value of the '{@link #isProjectIndependent() <em>Project
   * Independent</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isProjectIndependent()
   * @generated
   * @ordered
   */
  protected static final boolean PROJECT_INDEPENDENT_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isProjectIndependent() <em>Project
   * Independent</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isProjectIndependent()
   * @generated
   * @ordered
   */
  protected boolean projectIndependent = PROJECT_INDEPENDENT_EDEFAULT;

  /**
   * The default value of the '{@link #getMaxResults() <em>Max Results</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMaxResults()
   * @generated
   * @ordered
   */
  protected static final int MAX_RESULTS_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getMaxResults() <em>Max Results</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMaxResults()
   * @generated
   * @ordered
   */
  protected int maxResults = MAX_RESULTS_EDEFAULT;

  /**
   * The default value of the '{@link #isAllResults() <em>All Results</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isAllResults()
   * @generated
   * @ordered
   */
  protected static final boolean ALL_RESULTS_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isAllResults() <em>All Results</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isAllResults()
   * @generated
   * @ordered
   */
  protected boolean allResults = ALL_RESULTS_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ObjectSearchFavoriteImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return IObjectSearchFavoritesPackage.Literals.OBJECT_SEARCH_FAVORITE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getObjectName() {
    return objectName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setObjectName(final String newObjectName) {
    final String oldObjectName = objectName;
    objectName = newObjectName;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__OBJECT_NAME, oldObjectName,
          objectName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getSearchFilter() {
    return searchFilter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setSearchFilter(final String newSearchFilter) {
    final String oldSearchFilter = searchFilter;
    searchFilter = newSearchFilter;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_FILTER, oldSearchFilter,
          searchFilter));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setDescription(final String newDescription) {
    final String oldDescription = description;
    description = newDescription;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESCRIPTION, oldDescription,
          description));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getDestinationId() {
    return destinationId;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setDestinationId(final String newDestinationId) {
    final String oldDestinationId = destinationId;
    destinationId = newDestinationId;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESTINATION_ID, oldDestinationId,
          destinationId));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isAndSearchActive() {
    return andSearchActive;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setAndSearchActive(final boolean newAndSearchActive) {
    final boolean oldAndSearchActive = andSearchActive;
    andSearchActive = newAndSearchActive;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE,
          oldAndSearchActive, andSearchActive));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getSearchType() {
    return searchType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setSearchType(final String newSearchType) {
    final String oldSearchType = searchType;
    searchType = newSearchType;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_TYPE, oldSearchType,
          searchType));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isProjectIndependent() {
    return projectIndependent;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setProjectIndependent(final boolean newProjectIndependent) {
    final boolean oldProjectIndependent = projectIndependent;
    projectIndependent = newProjectIndependent;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT,
          oldProjectIndependent, projectIndependent));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int getMaxResults() {
    return maxResults;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setMaxResults(final int newMaxResults) {
    final int oldMaxResults = maxResults;
    maxResults = newMaxResults;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__MAX_RESULTS, oldMaxResults,
          maxResults));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isAllResults() {
    return allResults;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setAllResults(final boolean newAllResults) {
    final boolean oldAllResults = allResults;
    allResults = newAllResults;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET,
          IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__ALL_RESULTS, oldAllResults,
          allResults));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__OBJECT_NAME:
      return getObjectName();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_FILTER:
      return getSearchFilter();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESCRIPTION:
      return getDescription();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESTINATION_ID:
      return getDestinationId();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE:
      return isAndSearchActive();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_TYPE:
      return getSearchType();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT:
      return isProjectIndependent();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__MAX_RESULTS:
      return getMaxResults();
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__ALL_RESULTS:
      return isAllResults();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__OBJECT_NAME:
      setObjectName((String) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_FILTER:
      setSearchFilter((String) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESCRIPTION:
      setDescription((String) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESTINATION_ID:
      setDestinationId((String) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE:
      setAndSearchActive((Boolean) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_TYPE:
      setSearchType((String) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT:
      setProjectIndependent((Boolean) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__MAX_RESULTS:
      setMaxResults((Integer) newValue);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__ALL_RESULTS:
      setAllResults((Boolean) newValue);
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
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__OBJECT_NAME:
      setObjectName(OBJECT_NAME_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_FILTER:
      setSearchFilter(SEARCH_FILTER_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESCRIPTION:
      setDescription(DESCRIPTION_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESTINATION_ID:
      setDestinationId(DESTINATION_ID_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE:
      setAndSearchActive(AND_SEARCH_ACTIVE_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_TYPE:
      setSearchType(SEARCH_TYPE_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT:
      setProjectIndependent(PROJECT_INDEPENDENT_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__MAX_RESULTS:
      setMaxResults(MAX_RESULTS_EDEFAULT);
      return;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__ALL_RESULTS:
      setAllResults(ALL_RESULTS_EDEFAULT);
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
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__OBJECT_NAME:
      return OBJECT_NAME_EDEFAULT == null ? objectName != null
          : !OBJECT_NAME_EDEFAULT.equals(objectName);
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_FILTER:
      return SEARCH_FILTER_EDEFAULT == null ? searchFilter != null
          : !SEARCH_FILTER_EDEFAULT.equals(searchFilter);
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESCRIPTION:
      return DESCRIPTION_EDEFAULT == null ? description != null
          : !DESCRIPTION_EDEFAULT.equals(description);
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESTINATION_ID:
      return DESTINATION_ID_EDEFAULT == null ? destinationId != null
          : !DESTINATION_ID_EDEFAULT.equals(destinationId);
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE:
      return andSearchActive != AND_SEARCH_ACTIVE_EDEFAULT;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_TYPE:
      return SEARCH_TYPE_EDEFAULT == null ? searchType != null
          : !SEARCH_TYPE_EDEFAULT.equals(searchType);
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT:
      return projectIndependent != PROJECT_INDEPENDENT_EDEFAULT;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__MAX_RESULTS:
      return maxResults != MAX_RESULTS_EDEFAULT;
    case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__ALL_RESULTS:
      return allResults != ALL_RESULTS_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   */
  @Override
  public String toString() {
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuilder result = new StringBuilder(super.toString());
    result.append(" (objectName: ");
    result.append(objectName);
    result.append(", searchFilter: ");
    result.append(searchFilter);
    result.append(", description: ");
    result.append(description);
    result.append(", destinationId: ");
    result.append(destinationId);
    result.append(", andSearchActive: ");
    result.append(andSearchActive);
    result.append(", searchType: ");
    result.append(searchType);
    result.append(", projectIndependent: ");
    result.append(projectIndependent);
    result.append(", maxResults: ");
    result.append(maxResults);
    result.append(", allResults: ");
    result.append(allResults);
    result.append(')');
    return result.toString();
  }

} // ObjectSearchFavoriteImpl
