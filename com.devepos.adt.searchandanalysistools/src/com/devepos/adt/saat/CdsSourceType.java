package com.devepos.adt.saat;

import java.util.stream.Stream;

import com.devepos.adt.saat.internal.util.IImages;

/**
 * Source type of a CDS view
 *
 * @author stockbal
 */
public enum CdsSourceType implements IDataSourceType {
	NORMAL("V", null),
	CUSTOM("Q", IImages.SOURCE_TYPE_CUSTOM_ENTITY_OVR),
	ABSTRACT("A", IImages.SOURCE_TYPE_ABSTRACT_ENTITY_OVR),
	FUNCTION("F", IImages.SOURCE_TYPE_FUNCTION_OVR),
	EXTEND("E", null);

	private String imageId;
	private String id;

	private CdsSourceType(final String id, final String imageId) {
		this.imageId = imageId;
		this.id = id;
	}

	/**
	 * Retrieves a CDS Source type for the given id
	 *
	 * @param  id the internal id of a potential source type
	 * @return    the found {@link CdsSourcetype} or <code>null</null>
	 */
	public static CdsSourceType getFromId(final String id) {
		if (id == null) {
			return null;
		}
		return Stream.of(CdsSourceType.values()).filter(type -> type.id.equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	@Override
	public String getImageId() {
		return this.imageId;
	}

	@Override
	public String getId() {
		return this.id;
	}

}
