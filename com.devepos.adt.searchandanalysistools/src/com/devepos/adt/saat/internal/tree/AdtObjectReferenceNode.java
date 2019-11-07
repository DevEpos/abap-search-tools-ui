package com.devepos.adt.saat.internal.tree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.saat.internal.IDestinationProvider;
import com.devepos.adt.saat.internal.ObjectType;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.AdtWhereUsedAdapterFactory;
import com.sap.adt.project.IProjectProvider;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Represents a node in a tree which holds a reference to an
 * {@link IAdtObjectReference}
 *
 * @author stockbal
 */
public class AdtObjectReferenceNode extends TreeNodeBase implements IAdtObjectReferenceNode {

	protected IAdtObjectReference objectReference = null;
	protected List<ITreeNode> children = new ArrayList<>();
	protected String destinationId;

	public AdtObjectReferenceNode(final ITreeNode parent) {
		super("", parent);
	}

	public AdtObjectReferenceNode(final String name, final String displayName, final String description,
		final IAdtObjectReference objectReference) {
		this(name, displayName, description, objectReference, null);
	}

	public AdtObjectReferenceNode(final String name, final String displayName, final String description,
		final IAdtObjectReference objectReference, final ITreeNode parent) {
		super(name, displayName, description, parent);
		this.objectReference = objectReference;
	}

	@Override
	public String getImageId() {
		if (this.objectReference != null) {
			return AdtUtil.getImageForAdtType(this.objectReference.getType());
		}
		return null;
	}

	@Override
	public ObjectType getObjectType() {
		if (this.objectReference != null) {
			return ObjectType.getFromAdtType(this.objectReference.getType());
		}
		return null;
	}

	@Override
	public IAdtObjectReference getObjectReference() {
		return this.objectReference;
	}

	@Override
	public void setObjectReference(final IAdtObjectReference objectReference) {
		this.objectReference = objectReference;

	}

	@Override
	public boolean supportsDataPreview() {
		final ObjectType objectType = getObjectType();
		return objectType != null ? objectType.supportsDataPreview() : false;
	}

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		final T adapted = super.getAdapter(adapter);
		if (adapted != null) {
			return adapted;
		}
		if (this.objectReference == null) {
			return null;
		}
		if (adapter == IProjectProvider.class) {
			try {
				return adapter.cast(AdtWhereUsedAdapterFactory.adaptToProjectProvider(this.objectReference));
			} catch (final ClassCastException exc) {
			}
		} else if (adapter == com.sap.adt.tools.core.IAdtObjectReference.class) {
			try {
				return adapter.cast(AdtWhereUsedAdapterFactory.adaptToNonEmfAdtObjectRef(this.objectReference));
			} catch (final ClassCastException exc) {
			}
		} else if (adapter == IDestinationProvider.class) {
			try {
				return adapter.cast(this.objectReference);
			} catch (final ClassCastException exc) {
			}
		}
		return null;
	}

	@Override
	public List<ITreeNode> getChildren() {
		return this.children;
	}

	@Override
	public void setChildren(final List<ITreeNode> children) {
		this.children = children;
	}

	@Override
	public void addChild(final ITreeNode child) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(child);
	}

	@Override
	public String getSizeAsString() {
		return this.children != null ? new DecimalFormat("###,###").format(this.children.size()) : "0";
	}

	@Override
	public int size() {
		return this.children != null ? this.children.size() : 0;
	}

	@Override
	public boolean hasChildren() {
		return this.children != null && !this.children.isEmpty();
	}
}
