package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.tools.base.project.IAbapProjectProvider;

/**
 * Search parameter to restrict results to entities which have a certain field
 *
 * @author stockbal
 */
public class FieldSearchParameter extends NamedItemProposalProvider implements ISearchParameter, ISearchProposalProvider {
	private final Image image;

	public FieldSearchParameter(final IAbapProjectProvider projectProvider, final NamedItemType namedItemType) {
		super(projectProvider, QueryParameterName.FIELD_NAME, namedItemType);
		this.image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.FIELD_PARAM);
	}

	@Override
	public List<IContentProposal> getProposalList(final String query) throws CoreException {
		return getProposals(query);
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
		return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionFieldParameter_xmsg, new Object[] { getLabel(), "matnr" });
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
