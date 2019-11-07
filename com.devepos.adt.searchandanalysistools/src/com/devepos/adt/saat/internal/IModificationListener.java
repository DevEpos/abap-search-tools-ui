package com.devepos.adt.saat.internal;

/**
 * This listener interface can be used to listen for modifications of any kind
 *
 * @author stockbal
 */
public interface IModificationListener {

	/**
	 * The type of a modifcation
	 *
	 * @author stockbal
	 */
	public enum ModificationKind {
		CLEARED,
		ADDED,
		REMOVED
	}

	/**
	 * Tells the subscriber of a modification. The <code>kind</code> tells the
	 * subscriber of the kind of the modification that occurred
	 *
	 * @param kind the kind of the modification event
	 */
	void modified(ModificationKind kind);
}
