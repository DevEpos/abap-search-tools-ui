package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.saat.IDataSourceType;
import com.devepos.adt.saat.search.model.IExtendedAdtObjectInfo;

public class ExtendedAdtObjectInfo implements IExtendedAdtObjectInfo {

	private boolean isReleased;
	private IDataSourceType sourceType;

	/**
	 * Sets the API State of the ADT Object
	 *
	 * @param apiState the API state. It can only have the following values:
	 *                 <ul>
	 *                 <li>{@link IExtendedAdtObjectInfo#API_STATE_DEPRECATED}</li>
	 *                 <li>{@link IExtendedAdtObjectInfo#API_STATE_RELEASED}</li>
	 *                 </ul>
	 */
	public void setApiState(final String apiState) {
		if (apiState == null) {
			return;
		}
		if (apiState == API_STATE_DEPRECATED) {
			this.isReleased = false;
		} else if (apiState == API_STATE_RELEASED) {
			this.isReleased = true;
		}
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