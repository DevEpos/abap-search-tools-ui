package com.devepos.adt.saat.internal.elementinfo;

import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.saat.internal.IDestinationProvider;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Information about an ADT Object reference
 *
 * @author stockbal
 */
public class AdtObjectReferenceElementInfo extends ElementInfoBase implements IAdtObjectReferenceElementInfo {

	private IAdtObjectReference objectReference;
	private List<IElementInfo> children;
	private boolean lazyLoading = true;
	private IElementInfoProvider infoProvider;
	private LazyLoadingRefreshMode refreshMode;

	public AdtObjectReferenceElementInfo() {
		this(null);
	}

	public AdtObjectReferenceElementInfo(final String name) {
		this(name, null, null);
	}

	public AdtObjectReferenceElementInfo(final String name, final String displayName, final String description) {
		super(name, displayName, null, description);
	}

	@Override
	public IAdtObjectReference getAdtObjectReference() {
		return this.objectReference;
	}

	@Override
	public String getUri() {
		return this.objectReference != null ? this.objectReference.getUri() : "";
	}

	@Override
	public String getAdtType() {
		return this.objectReference != null ? this.objectReference.getType() : "";
	}

	@Override
	public String getImageId() {
		if (this.objectReference != null) {
			return AdtUtil.getImageForAdtType(this.objectReference.getType());
		}
		return super.getImageId();
	}

	@Override
	public void setAdtObjectReference(final IAdtObjectReference objectReference) {
		this.objectReference = objectReference;
	}

	@Override
	public List<IElementInfo> getChildren() {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		return this.children;
	}

	@Override
	public boolean hasChildren() {
		return this.children != null && !this.children.isEmpty();
	}

	@Override
	public boolean hasLazyLoadingSupport() {
		return this.lazyLoading;
	}

	@Override
	public void setLazyLoadingSupport(final boolean lazyLoading) {
		this.lazyLoading = lazyLoading;
	}

	@Override
	public IElementInfoProvider getElementInfoProvider() {
		return this.infoProvider;
	}

	@Override
	public void setElementInfoProvider(final IElementInfoProvider infoProvider) {
		this.infoProvider = infoProvider;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AdtObjectReferenceElementInfo)) {
			return false;
		}

		final AdtObjectReferenceElementInfo adtObjectInfo2 = (AdtObjectReferenceElementInfo) obj;
		if (this.name.equals(adtObjectInfo2.getName())) {
			// check destination
			return compareDestination(adtObjectInfo2.getAdtObjectReference());
		}
		return false;
	}

	@Override
	public void setContentRefreshMode(final LazyLoadingRefreshMode mode) {
		this.refreshMode = mode;
	}

	@Override
	public LazyLoadingRefreshMode getContentRefreshMode() {
		return this.refreshMode != null ? this.refreshMode : IAdtObjectReferenceElementInfo.super.getContentRefreshMode();
	}

	private boolean compareDestination(final IAdtObjectReference objRef) {
		String destinationId1 = null;
		String destinationId2 = null;
		if (this.objectReference != null && this.objectReference instanceof IDestinationProvider) {
			destinationId1 = ((IDestinationProvider) this.objectReference).getDestinationId();
		}

		if (objRef != null && objRef instanceof IDestinationProvider) {
			destinationId2 = ((IDestinationProvider) objRef).getDestinationId();
		}
		if (destinationId1 == null && destinationId2 == null) {
			return true;
		} else if (destinationId1 != null && destinationId2 != null) {
			return destinationId1.equals(destinationId2);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = this.name == null ? 0 : this.name.hashCode();
		if (this.objectReference != null && this.objectReference instanceof IDestinationProvider) {
			final String destinationId = ((IDestinationProvider) this.objectReference).getDestinationId();
			result = 31 * result + (destinationId == null ? 0 : destinationId.hashCode());
		}
		return result;
	}

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		if (adapter == IDestinationProvider.class) {
			if (this.objectReference != null) {
				try {
					return adapter.cast(this.objectReference);
				} catch (final ClassCastException exc) {
				}
			}
		} else {
			return super.getAdapter(adapter);
		}
		return null;
	}

	@Override
	public int size() {
		return this.children == null ? 0 : this.children.size();
	}

	@Override
	public IElementInfo getChild(final String name) {
		if (!hasChildren() || name == null) {
			return null;
		}
		return this.children.stream().filter(el -> name.equals(name)).findFirst().orElse(null);
	}

	@Override
	public boolean hasChild(final String name) {
		if (!hasChildren() || name == null) {
			return false;
		}
		return this.children.stream().anyMatch(el -> name.equals(el.getName()));
	}

}
