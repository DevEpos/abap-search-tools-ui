package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Provider for search proposals
 *
 * @author stockbal
 *
 */
public interface ISearchProposalProvider {
  /**
   * Retrieves a list of parameter proposals for the given query string
   *
   * @param query the query String for which search parameters should be retrieved
   * @return a List of search parameter proposals
   * @throws CoreException
   */
  List<IContentProposal> getProposalList(final String query) throws CoreException;
}
