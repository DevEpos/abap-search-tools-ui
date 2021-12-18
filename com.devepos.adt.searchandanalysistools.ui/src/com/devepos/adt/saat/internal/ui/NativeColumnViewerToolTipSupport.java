package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Native Tooltip support for a tree Viewer
 *
 * @author stockbal
 */
public class NativeColumnViewerToolTipSupport extends ColumnViewerToolTipSupport {
  private final ColumnViewer viewer;
  private final Listener keyDownListener;

  public static void enableFor(final ColumnViewer treeViewer) {
    new NativeColumnViewerToolTipSupport(treeViewer);
  }

  protected NativeColumnViewerToolTipSupport(final ColumnViewer viewer) {
    super(viewer, ToolTip.NO_RECREATE, true);
    this.viewer = viewer;
    keyDownListener = this::handleKeyDownEvent;
    activate();
  }

  @Override
  public void activate() {
    super.activate();
    viewer.getControl().addListener(SWT.KeyDown, keyDownListener);
  }

  @Override
  public void deactivate() {
    super.deactivate();
    if (keyDownListener != null) {
      viewer.getControl().removeListener(SWT.KeyDown, keyDownListener);
    }
  }

  private void handleKeyDownEvent(final Event event) {
    hide();
  }

}