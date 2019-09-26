package com.devepos.adt.saat.internal.tree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.elementinfo.LazyLoadingRefreshMode;

/**
 * Simple folder node that supports lazy loading
 *
 * @author stockbal
 */
public class LazyLoadingFolderNode extends FolderTreeNode implements ILazyLoadingNode {
	private boolean isLoading;
	private boolean isLoaded;
	private LazyLoadingRefreshMode refreshMode;
	private IElementInfoProvider provider;
	private final List<ILazyLoadingListener> lazyLoadingListeners = new ArrayList<>();

	public LazyLoadingFolderNode(final String name, final IElementInfoProvider elementInfoProvider, final ITreeNode parent,
		final String imageId) {
		this(name, name, elementInfoProvider, imageId, null, parent);
	}

	public LazyLoadingFolderNode(final String name, final String displayName, final IElementInfoProvider elementInfoProvider,
		final String imageId, final String description, final ITreeNode parent) {

		super(name, displayName, description, parent, imageId, null);
		Assert.isNotNull(elementInfoProvider);
		this.provider = elementInfoProvider;
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
	public boolean hasChildren() {
		return true;
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
	public void setElementInfoProvider(final IElementInfoProvider provider) {
		this.provider = provider;
	}

	@Override
	public String getLazyLoadingJobName() {
		return this.provider != null ? this.provider.getProviderDescription() : null;
	}

	@Override
	public void loadChildren() {
		this.isLoading = true;
		final List<IElementInfo> elementInfos = this.provider.getElements();

		if (elementInfos != null) {
			// create the sub nodes
			new TreeNodeCreator(this).createSubNodes(elementInfos);
		}
		this.isLoading = false;
		this.isLoaded = true;
		for (final ILazyLoadingListener l : this.lazyLoadingListeners) {
			l.loadingFinished(this.children.size());
		}
	}

	@Override
	public void addLazyLoadingListener(final ILazyLoadingListener l) {
		this.lazyLoadingListeners.add(l);
	}

	@Override
	public void removeLazyLoadingListener(final ILazyLoadingListener l) {
		this.lazyLoadingListeners.remove(l);

	}

	@Override
	public void setContentRefreshMode(final LazyLoadingRefreshMode mode) {
		this.refreshMode = mode;
	}

	@Override
	public LazyLoadingRefreshMode getContentRefreshMode() {
		if (this.refreshMode != null) {
			return this.refreshMode;
		}
		return ILazyLoadingNode.super.getContentRefreshMode();
	}

}
