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
 * Search parameter to restrict query results to CDS views which are of a
 * certain type e.g. <em>Hierarchy</em>
 *
 * @author stockbal
 */
public class TypeSearchParameter extends NamedItemProposalProvider implements ISearchParameter, ISearchProposalProvider {
	private final Image image;

	public TypeSearchParameter(final IAbapProjectProvider projectProvider) {
		super(projectProvider, QueryParameterName.TYPE, NamedItemType.CDS_TYPE, true);
		this.image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.TYPE_PARAM);
	}

	@Override
	public List<IContentProposal> getProposalList(final String query) throws CoreException {
		return getProposals("", query);
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
		return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionCdsTypeParameter_xmsg, new Object[] { getLabel(), "function" });
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
		return true;
	}

	@Override
	public boolean supportsNegatedValues() {
		return true;
	}
}
