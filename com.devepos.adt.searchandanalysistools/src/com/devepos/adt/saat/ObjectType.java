package com.devepos.adt.saat;

/**
 * Relevant (ADT) Object type for the Plugin
 *
 * @author stockbal
 */
public enum ObjectType {
	CDS_VIEW("C", IAdtObjectTypeConstants.DDLS_DEFINITION_TYPE, true),
	CDS_VIEW_FIELD("", IAdtObjectTypeConstants.CDS_VIEW_FIELD_TYPE),
	VIEW("V", IAdtObjectTypeConstants.VIEW_DEFINITION_TYPE, true),
	TABLE("T", IAdtObjectTypeConstants.TABLE_DEFINITION_TYPE, true),
	TABLE_FIELD("", IAdtObjectTypeConstants.TABLE_FIELD_TYPE),
	TABLE_SETTINGS("", IAdtObjectTypeConstants.TABLE_SETTINGS_TYPE),
	CLASS("", IAdtObjectTypeConstants.CLASS_DEFINITION_TYPE),
	BUSINESS_OBJECT("", IAdtObjectTypeConstants.BUSINESS_OBJECT_TYPE),
	METADATA_EXT("", IAdtObjectTypeConstants.METADATA_EXTENSION_TYPE),
	ACCESS_CONTROL("", IAdtObjectTypeConstants.ACCESS_CONTROL_TYPE);

	private final String id;
	private final String adtExecutionType;
	private final boolean supportsDataPreview;

	private ObjectType(final String id, final String adtExecutionType) {
		this(id, adtExecutionType, false);
	}

	private ObjectType(final String id, final String adtExecutionType, final boolean supportsDataPreview) {
		this.id = id;
		this.adtExecutionType = adtExecutionType;
		this.supportsDataPreview = supportsDataPreview;
	}

	/**
	 * Returns <code>true</code> if the object type supports Data Preview
	 *
	 * @return
	 */
	public boolean supportsDataPreview() {
		return this.supportsDataPreview;
	}

	/**
	 * Retrieve the internal id of the Object type
	 *
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieve the ADT execution type of this object type
	 *
	 * @return
	 */
	public String getAdtExecutionType() {
		return this.adtExecutionType;
	}

	/**
	 * Retrieve Object type for the given id
	 *
	 * @param  id
	 * @return
	 */
	public static ObjectType getFromId(final String id) {
		for (final ObjectType ot : ObjectType.values()) {
			if (ot.id.equals(id)) {
				return ot;
			}
		}
		return null;
	}

	/**
	 * Get object type instance from the given ADT type
	 *
	 * @param  execType
	 * @return
	 */
	public static ObjectType getFromAdtType(final String execType) {
		for (final ObjectType ot : ObjectType.values()) {
			if (ot.adtExecutionType.equalsIgnoreCase(execType)) {
				return ot;
			}
		}
		return null;
	}

}
