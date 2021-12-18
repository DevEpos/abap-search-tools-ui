package com.devepos.adt.saat.internal.search;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.search.contentassist.SearchParameterProposal;

/**
 * Parameter for object search that only supports <code>true</code> and
 * <code>false</code>
 *
 * @author stockbal
 */
public class BooleanSearchParameter extends SearchParameter implements ISearchProposalProvider {

  public BooleanSearchParameter(final QueryParameterName parameterName, final String description,
      final Image image) {
    super(parameterName, description, image, false, false, false);
  }

  @Override
  public List<IContentProposal> getProposalList(final String query) throws CoreException {
    return Arrays.asList(new SearchParameterProposal(Boolean.TRUE.toString(), getParameterName(),
        null, null), new SearchParameterProposal(Boolean.FALSE.toString(), getParameterName(), null,
            null));
  }

}
