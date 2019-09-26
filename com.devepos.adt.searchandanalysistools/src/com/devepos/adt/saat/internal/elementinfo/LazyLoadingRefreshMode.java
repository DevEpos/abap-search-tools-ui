package com.devepos.adt.saat.internal.elementinfo;

/**
 * Mode for refreshing content after entity is loaded
 *
 * @author stockbal
 */
public enum LazyLoadingRefreshMode {
	/**
	 * Only the root object should be refreshed
	 */
	ROOT_ONLY,
	/**
	 * The root object and any existing child nodes that do not have lazy loading
	 * capabilities
	 */
	ROOT_AND_NON_LAZY_CHILDREN,
	/**
	 * The root object and any existing child node
	 */
	ROOT_AND_ALL_CHILDREN;

}
