package com.devepos.adt.saat.internal.search;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.contentassist.SearchParameterProposal;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Search parameter to restrict query results to CDS views which have defined
 * parameters
 *
 * @author stockbal
 */
public class HasParameterSearchParameter implements ISearchParameter, ISearchProposalProvider {
	private final QueryParameterName parameterName;
	private final Image image;

	public HasParameterSearchParameter() {
		this.parameterName = QueryParameterName.HAS_PARAMS;
		this.image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.PARAMETER_PARAM);
	}

	@Override
	public List<IContentProposal> getProposalList(final String query) throws CoreException {
		return Arrays.asList(new SearchParameterProposal("true", this.parameterName, null, null));
	}

	@Override
	public QueryParameterName getParameterName() {
		return this.parameterName;
	}

	@Override
	public Image getImage() {
		return this.image;
	}

	@Override
	public String getLabel() {
		return this.parameterName.getLowerCaseKey();
	}

	@Override
	public String getDescription() {
		return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionParamsParameter_xmsg, new Object[] { getLabel(), "true" });
	}

	@Override
	public boolean supportsPatternValues() {
		return false;
	}

	@Override
	public boolean isBuffered() {
		return true;
	}

	@Override
	public boolean supportsMultipleValues() {
		return false;
	}

	@Override
	public boolean supportsNegatedValues() {
		return false;
	}
}
