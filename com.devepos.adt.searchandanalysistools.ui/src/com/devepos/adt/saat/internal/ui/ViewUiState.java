package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.viewers.ISelection;

/**
 * Basic UI State for a view.<br>
 * Only stores the current selection of the view
 *
 * @author stockbal
 */
public class ViewUiState {

  protected ISelection selection;

  /**
   * @return the selection
   */
  public ISelection getSelection() {
    return selection;
  }

  /**
   * @param selection the selection to set
   */
  public void setSelection(final ISelection selection) {
    this.selection = selection;
  }

}
