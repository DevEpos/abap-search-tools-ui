package com.devepos.adt.saat.internal.search;

import org.eclipse.jface.fieldassist.IContentProposal;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.saat.internal.search.contentassist.SearchParameterProposal;

/**
 * Proposal Provider which provides database entities like database tables,
 * database views and CDS views
 */
public class DatabaseEntityProposalProvider extends NamedItemProposalProvider {

  public DatabaseEntityProposalProvider(final IAbapProjectProvider projectProvider,
      final QueryParameterName parameterName) {
    super(projectProvider, parameterName, NamedItemType.DB_ENTITY);
  }

  @Override
  protected IContentProposal createProposalFromNamedItem(final INamedItem item,
      final String query) {
    final ObjectType type = ObjectType.getFromId(item.getData());
    return new SearchParameterProposal(item.getName(), parameterName, null, item.getDescription(),
        type, query);
  }

}
