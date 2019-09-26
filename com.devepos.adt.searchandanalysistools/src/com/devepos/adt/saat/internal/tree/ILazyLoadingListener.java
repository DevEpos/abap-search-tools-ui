package com.devepos.adt.saat.internal.tree;

/**
 * Listener for changes in an object that supports lazy loading
 *
 * @see    ILazyLoadingNode
 * @author stockbal
 */
public interface ILazyLoadingListener {
	/**
	 * Will be called after the content of the lazy loading object was loaded
	 * 
	 * @param childCount
	 */
	void loadingFinished(int childCount);
}
