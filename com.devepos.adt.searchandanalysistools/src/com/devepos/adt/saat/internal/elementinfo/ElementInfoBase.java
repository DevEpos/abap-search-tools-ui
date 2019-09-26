package com.devepos.adt.saat.internal.elementinfo;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Information about an element. Holds some basic information like the
 * <b>name</b> or the <b>description</b> of the element
 *
 * @author stockbal
 */
public abstract class ElementInfoBase implements IElementInfo, IAdaptable {
	protected String name;
	protected String displayName;
	protected String description;
	protected String imageId;
	private Object additionalInfo;
	private final Map<String, String> properties;

	public ElementInfoBase() {
		this("", "", null, null);
	}

	public ElementInfoBase(final String name) {
		this(name, name, null, null);
	}

	public ElementInfoBase(final String name, final String imageId) {
		this(name, name, imageId, null);
	}

	public ElementInfoBase(final String name, final String displayName, final String imageId, final String description) {
		this.name = name;
		this.properties = new HashMap<>();
		this.displayName = displayName;
		this.description = description;
		this.imageId = imageId;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getImageId() {
		return this.imageId;
	}

	@Override
	public Object getAdditionalInfo() {
		return this.additionalInfo;
	}

	@Override
	public void setAdditionalInfo(final Object info) {
		this.additionalInfo = info;
	}

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		try {
			return adapter.cast(this.additionalInfo);
		} catch (final ClassCastException exc) {
		}
		return null;
	}

	@Override
	public Map<String, String> getProperties() {
		return this.properties;
	}

	@Override
	public String getPropertyValue(final String key) {
		return this.properties.get(key);
	}
}
