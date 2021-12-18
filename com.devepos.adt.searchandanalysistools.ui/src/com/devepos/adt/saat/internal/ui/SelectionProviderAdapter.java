package com.devepos.adt.saat.internal.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;

/**
 * Adapter for a SelectionProvider
 *
 * @author stockbal
 */
public class SelectionProviderAdapter implements ISelectionProvider, ISelectionChangedListener {
  private Viewer viewer;
  private final ArrayList<ISelectionChangedListener> fListeners = new ArrayList<>(5);

  public void setViewer(final Viewer viewer) {
    this.viewer = viewer;
  }

  @Override
  public void addSelectionChangedListener(final ISelectionChangedListener listener) {
    fListeners.add(listener);
  }

  @Override
  public ISelection getSelection() {
    return viewer != null ? viewer.getSelection() : null;
  }

  @Override
  public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
    fListeners.remove(listener);
  }

  @Override
  public void setSelection(final ISelection selection) {
    if (viewer != null) {
      viewer.setSelection(selection);
    }
  }

  @Override
  public void selectionChanged(final SelectionChangedEvent event) {
    // forward to my listeners
    final SelectionChangedEvent wrappedEvent = new SelectionChangedEvent(this, event
        .getSelection());
    for (final ISelectionChangedListener listener : fListeners) {
      listener.selectionChanged(wrappedEvent);
    }
  }

}