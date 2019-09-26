package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.saat.IDataSourceType;
import com.devepos.adt.saat.search.model.IExtendedAdtObjectInfo;

public class ExtendedAdtObjectInfo implements IExtendedAdtObjectInfo {

	private boolean isReleased;
	private IDataSourceType sourceType;

	/**
	 * @param isReleased the isReleased to set
	 */
	public void setReleased(final boolean isReleased) {
		this.isReleased = isReleased;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(final IDataSourceType sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public boolean isReleased() {
		return this.isReleased;
	}

	@Override
	public IDataSourceType getSourceType() {
		return this.sourceType;
	}
}