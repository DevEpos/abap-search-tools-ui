package com.devepos.adt.saat.internal.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Dummy part to hold a page
 *
 * @author stockbal
 */
public class DummyPart implements IWorkbenchPart {
	public DummyPart(final IWorkbenchPartSite site) {
		this.site = site;
	}

	private IWorkbenchPartSite site;

	@Override
	public void dispose() {
		this.site = null;
	}

	@Override
	public IWorkbenchPartSite getSite() {
		return this.site;
	}

	@Override
	public void addPropertyListener(final IPropertyListener listener) {
		/* dummy */}

	@Override
	public void createPartControl(final Composite parent) {
		/* dummy */}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public Image getTitleImage() {
		return null;
	}

	@Override
	public String getTitleToolTip() {
		return null;
	}

	@Override
	public void removePropertyListener(final IPropertyListener listener) {
		/* dummy */}

	@Override
	public void setFocus() {
		/* dummy */}

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		return null;
	}
}