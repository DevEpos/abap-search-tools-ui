package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.contentassist.SearchParameterProposal;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Implementation for search parameter {@link QueryParameterName#EXTENDED_BY}
 *
 * @author stockbal
 */
public class CdsExtendedBySearchParameter extends NamedItemProposalProvider implements
    ISearchParameter, ISearchProposalProvider {

  public CdsExtendedBySearchParameter(final IAbapProjectProvider projectProvider) {
    super(projectProvider, QueryParameterName.EXTENDED_BY, NamedItemType.CDS_EXTENSION);
  }

  @Override
  protected IContentProposal createProposalFromNamedItem(final INamedItem item,
      final String query) {
    return new SearchParameterProposal(item.getName(), parameterName, null, item.getDescription(),
        null, query);
  }

  @Override
  public List<IContentProposal> getProposalList(final String query) throws CoreException {
    return getProposals(query);
  }

  @Override
  public QueryParameterName getParameterName() {
    return parameterName;
  }

  @Override
  public Image getImage() {
    return SearchAndAnalysisPlugin.getDefault().getImage(IImages.EXTENSION);
  }

  @Override
  public String getLabel() {
    return parameterName.getLowerCaseKey();
  }

  @Override
  public String getDescription() {
    return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionCdsExtendedByParameter_xmsg,
        new Object[] { getLabel(), "X_view" });
  }

  @Override
  public boolean supportsPatternValues() {
    return true;
  }

  @Override
  public boolean isBuffered() {
    return false;
  }

  @Override
  public boolean supportsMultipleValues() {
    return true;
  }

  @Override
  public boolean supportsNegatedValues() {
    return false;
  }
}
