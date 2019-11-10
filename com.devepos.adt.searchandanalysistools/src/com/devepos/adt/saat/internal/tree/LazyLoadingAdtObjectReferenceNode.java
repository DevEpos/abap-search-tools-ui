package com.devepos.adt.saat.internal.tree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.elementinfo.LazyLoadingRefreshMode;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * ADT Object Reference node, which supports the lazy loading of its children.
 *
 * @author stockbal
 */
public class LazyLoadingAdtObjectReferenceNode extends AdtObjectReferenceNode implements ILazyLoadingNode {
	private boolean isLoading;
	private boolean isLoaded;
	private IElementInfoProvider provider;
	private LazyLoadingRefreshMode refreshMode;
	private final List<ILazyLoadingListener> lazyLoadingListeners = new ArrayList<>();

	public LazyLoadingAdtObjectReferenceNode(final ITreeNode parent) {
		this("", "", "", null, parent);
	}

	public LazyLoadingAdtObjectReferenceNode(final String name, final String displayName, final String description,
		final IAdtObjectReference objectReference, final ITreeNode parent) {
		super(name, displayName, description, objectReference, parent);
	}

	@Override
	public String getSizeAsString() {
		if (this.isLoading || !this.isLoaded) {
			return "?";
		} else {
			if (this.children != null) {
				return new DecimalFormat("###,###").format(this.children.size());
			} else {
				return "0";
			}
		}
	}

	@Override
	public void resetLoadedState() {
		this.isLoaded = false;
		if (this.children != null) {
			this.children.clear();
		}
		this.children = null;
	}

	@Override
	public boolean isLoading() {
		return this.isLoading;
	}

	@Override
	public boolean isLoaded() {
		return this.isLoaded;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public void loadChildren() {
		this.isLoading = true;
		loadChildrenInternal();
		this.isLoading = false;
		this.isLoaded = true;
		for (final ILazyLoadingListener l : this.lazyLoadingListeners) {
			l.loadingFinished(this.children != null ? this.children.size() : 0);
		}
	}

	@Override
	public void setContentRefreshMode(final LazyLoadingRefreshMode mode) {
		this.refreshMode = mode;
	}

	@Override
	public LazyLoadingRefreshMode getContentRefreshMode() {
		return this.refreshMode != null ? this.refreshMode : ILazyLoadingNode.super.getContentRefreshMode();
	}

	@Override
	public void setElementInfoProvider(final IElementInfoProvider provider) {
		this.provider = provider;
	}

	@Override
	public String getLazyLoadingJobName() {
		return this.provider != null ? this.provider.getProviderDescription() : null;
	}

	/**
	 * Internal logic for loading the child nodes of this tree node
	 */
	protected void loadChildrenInternal() {
		if (this.provider == null) {
			return;
		}
		final List<IElementInfo> elements = this.provider.getElements();
		if (elements == null || elements.isEmpty()) {
			return;
		}

		// create the sub nodes
		new TreeNodeCreator(this).createSubNodes(elements);
	}

	@Override
	public void addLazyLoadingListener(final ILazyLoadingListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		this.lazyLoadingListeners.add(l);
	}

	@Override
	public void removeLazyLoadingListener(final ILazyLoadingListener l) {
		this.lazyLoadingListeners.remove(l);

	}
}
