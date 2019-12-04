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
public class ObjectSearchFavoriteImpl extends MinimalEObjectImpl.Container implements IObjectSearchFavorite {
	/**
	 * The default value of the '{@link #getObjectName() <em>Object Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getObjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String OBJECT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getObjectName() <em>Object Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getObjectName()
	 * @generated
	 * @ordered
	 */
	protected String objectName = OBJECT_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getSearchFilter() <em>Search Filter</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getSearchFilter()
	 * @generated
	 * @ordered
	 */
	protected static final String SEARCH_FILTER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSearchFilter() <em>Search Filter</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getSearchFilter()
	 * @generated
	 * @ordered
	 */
	protected String searchFilter = SEARCH_FILTER_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDestinationId() <em>Destination
	 * Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getDestinationId()
	 * @generated
	 * @ordered
	 */
	protected static final String DESTINATION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDestinationId() <em>Destination Id</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getDestinationId()
	 * @generated
	 * @ordered
	 */
	protected String destinationId = DESTINATION_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #isAndSearchActive() <em>And Search
	 * Active</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #isAndSearchActive()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AND_SEARCH_ACTIVE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAndSearchActive() <em>And Search
	 * Active</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #isAndSearchActive()
	 * @generated
	 * @ordered
	 */
	protected boolean andSearchActive = AND_SEARCH_ACTIVE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSearchType() <em>Search Type</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getSearchType()
	 * @generated
	 * @ordered
	 */
	protected static final String SEARCH_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSearchType() <em>Search Type</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getSearchType()
	 * @generated
	 * @ordered
	 */
	protected String searchType = SEARCH_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #isProjectIndependent() <em>Project
	 * Independent</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #isProjectIndependent()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PROJECT_INDEPENDENT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isProjectIndependent() <em>Project
	 * Independent</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #isProjectIndependent()
	 * @generated
	 * @ordered
	 */
	protected boolean projectIndependent = PROJECT_INDEPENDENT_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxResults() <em>Max Results</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getMaxResults()
	 * @generated
	 * @ordered
	 */
	protected static final int MAX_RESULTS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getMaxResults() <em>Max Results</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #getMaxResults()
	 * @generated
	 * @ordered
	 */
	protected int maxResults = MAX_RESULTS_EDEFAULT;

	/**
	 * The default value of the '{@link #isAllResults() <em>All Results</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #isAllResults()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ALL_RESULTS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAllResults() <em>All Results</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see       #isAllResults()
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
		return this.objectName;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setObjectName(final String newObjectName) {
		final String oldObjectName = this.objectName;
		this.objectName = newObjectName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__OBJECT_NAME, oldObjectName, this.objectName));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getSearchFilter() {
		return this.searchFilter;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setSearchFilter(final String newSearchFilter) {
		final String oldSearchFilter = this.searchFilter;
		this.searchFilter = newSearchFilter;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_FILTER, oldSearchFilter, this.searchFilter));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setDescription(final String newDescription) {
		final String oldDescription = this.description;
		this.description = newDescription;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESCRIPTION, oldDescription, this.description));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getDestinationId() {
		return this.destinationId;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setDestinationId(final String newDestinationId) {
		final String oldDestinationId = this.destinationId;
		this.destinationId = newDestinationId;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESTINATION_ID, oldDestinationId, this.destinationId));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isAndSearchActive() {
		return this.andSearchActive;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setAndSearchActive(final boolean newAndSearchActive) {
		final boolean oldAndSearchActive = this.andSearchActive;
		this.andSearchActive = newAndSearchActive;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE, oldAndSearchActive,
				this.andSearchActive));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getSearchType() {
		return this.searchType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setSearchType(final String newSearchType) {
		final String oldSearchType = this.searchType;
		this.searchType = newSearchType;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_TYPE, oldSearchType, this.searchType));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isProjectIndependent() {
		return this.projectIndependent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setProjectIndependent(final boolean newProjectIndependent) {
		final boolean oldProjectIndependent = this.projectIndependent;
		this.projectIndependent = newProjectIndependent;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT, oldProjectIndependent,
				this.projectIndependent));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public int getMaxResults() {
		return this.maxResults;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setMaxResults(final int newMaxResults) {
		final int oldMaxResults = this.maxResults;
		this.maxResults = newMaxResults;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__MAX_RESULTS, oldMaxResults, this.maxResults));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isAllResults() {
		return this.allResults;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setAllResults(final boolean newAllResults) {
		final boolean oldAllResults = this.allResults;
		this.allResults = newAllResults;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
				IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__ALL_RESULTS, oldAllResults, this.allResults));
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
			return OBJECT_NAME_EDEFAULT == null ? this.objectName != null : !OBJECT_NAME_EDEFAULT.equals(this.objectName);
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_FILTER:
			return SEARCH_FILTER_EDEFAULT == null ? this.searchFilter != null : !SEARCH_FILTER_EDEFAULT.equals(this.searchFilter);
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? this.description != null : !DESCRIPTION_EDEFAULT.equals(this.description);
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__DESTINATION_ID:
			return DESTINATION_ID_EDEFAULT == null ? this.destinationId != null
				: !DESTINATION_ID_EDEFAULT.equals(this.destinationId);
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__AND_SEARCH_ACTIVE:
			return this.andSearchActive != AND_SEARCH_ACTIVE_EDEFAULT;
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__SEARCH_TYPE:
			return SEARCH_TYPE_EDEFAULT == null ? this.searchType != null : !SEARCH_TYPE_EDEFAULT.equals(this.searchType);
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__PROJECT_INDEPENDENT:
			return this.projectIndependent != PROJECT_INDEPENDENT_EDEFAULT;
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__MAX_RESULTS:
			return this.maxResults != MAX_RESULTS_EDEFAULT;
		case IObjectSearchFavoritesPackage.OBJECT_SEARCH_FAVORITE__ALL_RESULTS:
			return this.allResults != ALL_RESULTS_EDEFAULT;
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
		result.append(this.objectName);
		result.append(", searchFilter: ");
		result.append(this.searchFilter);
		result.append(", description: ");
		result.append(this.description);
		result.append(", destinationId: ");
		result.append(this.destinationId);
		result.append(", andSearchActive: ");
		result.append(this.andSearchActive);
		result.append(", searchType: ");
		result.append(this.searchType);
		result.append(", projectIndependent: ");
		result.append(this.projectIndependent);
		result.append(", maxResults: ");
		result.append(this.maxResults);
		result.append(", allResults: ");
		result.append(this.allResults);
		result.append(')');
		return result.toString();
	}

} // ObjectSearchFavoriteImpl
