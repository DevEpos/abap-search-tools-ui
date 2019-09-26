package com.devepos.adt.saat.internal.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Simple {@link ITreeContentProvider} implementation where the root elements do
 * not have any child nodes
 * 
 * @author stockbal
 */
public class SimpleTreeContentProvider implements ITreeContentProvider {

	@Override
	public boolean hasChildren(final Object element) {
		return false;
	}

	@Override
	public Object getParent(final Object element) {
		return null;
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return (Object[]) inputElement;
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		return null;
	}

}
