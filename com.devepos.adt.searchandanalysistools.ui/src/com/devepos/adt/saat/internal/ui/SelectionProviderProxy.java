package com.devepos.adt.saat.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * A selection provider for view parts with more that one viewer. Tracks the
 * focus of the viewers to provide the correct selection
 *
 * @author stockbal
 */
public class SelectionProviderProxy implements IPostSelectionProvider {

  private final List<StructuredViewer> viewers;

  private StructuredViewer viewerInFocus;
  private final ListenerList<ISelectionChangedListener> selectionChangedListeners;
  private final ListenerList<ISelectionChangedListener> postSelectionChangedListeners;

  /**
   * Creates new Proxy SelectionProvider
   */
  public SelectionProviderProxy() {
    this(null);
  }

  /**
   * Creates new Proxy SelectionProvider
   *
   * @param viewerInFocus the viewer currently in focus or <code>null</code>
   * @param viewers       list of viewers that can provide a selection
   */
  public SelectionProviderProxy(final StructuredViewer viewerInFocus,
      final StructuredViewer... viewers) {
    this.viewers = new ArrayList<>();

    selectionChangedListeners = new ListenerList<>();
    postSelectionChangedListeners = new ListenerList<>();
    this.viewerInFocus = viewerInFocus;

    if (viewers != null) {
      for (final StructuredViewer viewer : viewers) {
        addViewer(viewer);
      }
    }
  }

  /**
   * Adds new viewer to selection provider proxy
   *
   * @param viewer the viewer to be added
   */
  public void addViewer(final StructuredViewer viewer) {
    addViewer(viewer, false);
  }

  /**
   * Adds new viewer to selection provider proxy
   *
   * @param viewer    the viewer to be added
   * @param gainFocus if <code>true</code> the viewer to be added will be getting
   *                  the focus
   */
  public void addViewer(final StructuredViewer viewer, final boolean gainFocus) {
    if (viewers.contains(viewer)) {
      return;
    }
    final InternalListener listener = new InternalListener();
    viewer.addSelectionChangedListener(listener);
    viewer.addPostSelectionChangedListener(new InternalPostSelectionListener());
    final Control control = viewer.getControl();
    control.addFocusListener(listener);
    viewers.add(viewer);

    if (gainFocus) {
      setViewerInFocus(viewer);
    }
  }

  public void setViewerInFocus(final StructuredViewer viewer) {
    if (viewerInFocus != null) {
      propagateFocusChanged(viewer);
    } else {
      viewerInFocus = viewer;
    }
  }

  private final void propagateFocusChanged(final StructuredViewer viewer) {
    if (viewer != viewerInFocus) { // OK to compare by identity
      viewerInFocus = viewer;
      fireSelectionChanged();
      firePostSelectionChanged();
    }
  }

  private void doFocusChanged(final Widget control) {
    for (final StructuredViewer fViewer : viewers) {
      if (fViewer.getControl() == control) {
        propagateFocusChanged(fViewer);
        return;
      }
    }
  }

  private final void doPostSelectionChanged(final SelectionChangedEvent event) {
    final ISelectionProvider provider = event.getSelectionProvider();
    if (provider == viewerInFocus) {
      firePostSelectionChanged();
    }
  }

  private final void doSelectionChanged(final SelectionChangedEvent event) {
    final ISelectionProvider provider = event.getSelectionProvider();
    if (provider == viewerInFocus) {
      fireSelectionChanged();
    }
  }

  private void fireSelectionChanged() {
    if (selectionChangedListeners != null) {
      final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

      for (final ISelectionChangedListener listener : selectionChangedListeners) {
        listener.selectionChanged(event);
      }
    }
  }

  private void firePostSelectionChanged() {
    if (postSelectionChangedListeners != null) {
      final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

      for (final ISelectionChangedListener listener : postSelectionChangedListeners) {
        listener.selectionChanged(event);
      }
    }
  }

  /*
   * @see ISelectionProvider#addSelectionChangedListener
   */
  @Override
  public void addSelectionChangedListener(final ISelectionChangedListener listener) {
    selectionChangedListeners.add(listener);
  }

  /*
   * @see ISelectionProvider#removeSelectionChangedListener
   */
  @Override
  public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
    selectionChangedListeners.remove(listener);
  }

  @Override
  public void addPostSelectionChangedListener(final ISelectionChangedListener listener) {
    postSelectionChangedListeners.add(listener);
  }

  @Override
  public void removePostSelectionChangedListener(final ISelectionChangedListener listener) {
    postSelectionChangedListeners.remove(listener);
  }

  /*
   * @see ISelectionProvider#getSelection
   */
  @Override
  public ISelection getSelection() {
    if (viewerInFocus != null) {
      return viewerInFocus.getSelection();
    }
    return StructuredSelection.EMPTY;
  }

  /*
   * @see ISelectionProvider#setSelection
   */
  @Override
  public void setSelection(final ISelection selection) {
    if (viewerInFocus != null) {
      viewerInFocus.setSelection(selection);
    }
  }

  public void setSelection(final ISelection selection, final boolean reveal) {
    if (viewerInFocus != null) {
      viewerInFocus.setSelection(selection, reveal);
    }
  }

  /**
   * Returns the viewer in focus or null if no viewer has the focus
   *
   * @return returns the current viewer in focus
   */
  public StructuredViewer getViewerInFocus() {
    return viewerInFocus;
  }

  private class InternalListener implements ISelectionChangedListener, FocusListener {
    /*
     * @see ISelectionChangedListener#selectionChanged
     */
    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
      doSelectionChanged(event);
    }

    /*
     * @see FocusListener#focusGained
     */
    @Override
    public void focusGained(final FocusEvent e) {
      doFocusChanged(e.widget);
    }

    /*
     * @see FocusListener#focusLost
     */
    @Override
    public void focusLost(final FocusEvent e) {
      // do not reset due to focus behavior on GTK
      // fViewerInFocus= null;
    }
  }

  private class InternalPostSelectionListener implements ISelectionChangedListener {
    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
      doPostSelectionChanged(event);
    }

  }
}
