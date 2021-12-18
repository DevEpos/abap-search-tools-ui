package com.devepos.adt.saat.internal.search.contentassist;

import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Handler to signal content changes
 *
 * @author stockbal
 *
 */
public interface IContentChangeListener {

  /**
   * Notify subscribers of the content change
   *
   * @param resultList the list of updated content proposals
   */
  void notifyContentChange(List<IContentProposal> resultList);
}
