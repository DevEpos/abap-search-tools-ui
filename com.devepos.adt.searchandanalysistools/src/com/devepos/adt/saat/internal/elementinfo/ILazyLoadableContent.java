package com.devepos.adt.saat.internal.elementinfo;

/**
 * Describes content that has the ability to be loaded in a lazy way
 *
 * @author stockbal
 */
public interface ILazyLoadableContent {
	/**
	 * Sets the refresh mode for lazy loaded content after the loading is finished
	 *
	 * @param mode
	 */
	default void setContentRefreshMode(final LazyLoadingRefreshMode mode) {
	}

	/**
	 * Returns the refresh mode for the lazy loading content.<br>
	 * It becomes relevant after the content is loaded
	 *
	 * @return
	 */
	default LazyLoadingRefreshMode getContentRefreshMode() {
		return LazyLoadingRefreshMode.ROOT_ONLY;
	}

}
