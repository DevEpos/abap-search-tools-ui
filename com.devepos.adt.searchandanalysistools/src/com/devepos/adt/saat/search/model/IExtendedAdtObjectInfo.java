package com.devepos.adt.saat.search.model;

import com.devepos.adt.saat.IDataSourceType;
import com.devepos.adt.saat.ObjectType;

/**
 * Additional information about a result object from an object search
 *
 * @author stockbal
 */
public interface IExtendedAdtObjectInfo {
	/**
	 * Returns <code>true</code> if the result object is released
	 *
	 * @return
	 */
	boolean isReleased();

	/**
	 * Returns the source type of the object search result <br>
	 * This is only relevant for objects of type {@link ObjectType#CDS_VIEW} at the
	 * moment
	 *
	 * @return
	 */
	IDataSourceType getSourceType();
}
