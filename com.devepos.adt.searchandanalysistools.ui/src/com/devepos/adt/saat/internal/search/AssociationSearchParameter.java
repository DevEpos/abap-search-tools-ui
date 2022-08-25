/**
 *
 */
package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.base.project.IAbapProjectProvider;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Search parameter for restricting search to result which have a specific
 * database entity as association
 *
 * @author stockbal
 */
public class AssociationSearchParameter extends DatabaseEntityProposalProvider implements
    ISearchParameter, ISearchProposalProvider {

  private final Image image;

  /**
   *
   */
  public AssociationSearchParameter(final IAbapProjectProvider projectProvider) {
    super(projectProvider, QueryParameterName.ASSOCIATED_IN);
    image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.USED_AS_ASSOCICATION_PARAM);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.devepos.adt.saat.internal.search.ISearchProposalProvider#
   * getProposalList(java.lang.String)
   */
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
    return image;
  }

  @Override
  public String getLabel() {
    return parameterName.getLowerCaseKey();
  }

  @Override
  public String getDescription() {
    return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionAssociationParameter_xmsg,
        new Object[] { getLabel(), "I_PRODUCT" });
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
    return true;
  }
}
