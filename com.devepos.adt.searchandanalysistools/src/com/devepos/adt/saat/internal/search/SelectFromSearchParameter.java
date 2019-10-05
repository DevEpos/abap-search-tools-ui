package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Search parameter to restrict query results to CDS views which select data
 * from a specific database entity
 *
 * @author stockbal
 */
public class SelectFromSearchParameter extends DatabaseEntityProposalProvider
	implements ISearchParameter, ISearchProposalProvider {

	private final Image image;

	public SelectFromSearchParameter(final IAbapProjectProvider projectProvider) {
		super(projectProvider, QueryParameterName.SELECT_SOURCE_IN);
		this.image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.SELECT_FROM_PARAM);
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
		return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionFromParameter_xmsg, new Object[] { getLabel(), "mara" });
	}

	@Override
	public boolean supportsPatternValues() {
		return true;
	}

	@Override
	public List<IContentProposal> getProposalList(final String query) throws CoreException {
		return getProposals(query);
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
