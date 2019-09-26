package com.devepos.adt.saat.internal.elementinfo;

/**
 * Information of an element that supports lazy loading. The loading of the
 * elements is done via the passed {@link IElementInfoProvider}
 *
 * @author stockbal
 */
public class LazyLoadingElementInfo extends ElementInfoBase implements ILazyLoadingElementInfo {

	private IElementInfoProvider provider;
	private LazyLoadingRefreshMode refreshMode;

	public LazyLoadingElementInfo(final String name, final String imageId, final IElementInfoProvider provider) {
		this(name, name, imageId, provider);
	}

	public LazyLoadingElementInfo(final String name, final String displayName, final String imageId,
		final IElementInfoProvider provider) {
		super(name, displayName, imageId, null);
		this.provider = provider;
	}

	@Override
	public IElementInfoProvider getElementInfoProvider() {
		return this.provider;
	}

	@Override
	public void setElementInfoProvider(final IElementInfoProvider infoProvider) {
		this.provider = infoProvider;
	}

	@Override
	public void setContentRefreshMode(final LazyLoadingRefreshMode mode) {
		this.refreshMode = mode;
	}

	@Override
	public LazyLoadingRefreshMode getContentRefreshMode() {
		return this.refreshMode;
	}

}
