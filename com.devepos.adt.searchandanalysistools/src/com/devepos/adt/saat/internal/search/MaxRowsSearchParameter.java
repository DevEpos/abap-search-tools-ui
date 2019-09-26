package com.devepos.adt.saat.internal.search;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.contentassist.SearchParameterProposal;
import com.devepos.adt.saat.internal.search.model.QueryParameterName;
import com.devepos.adt.saat.internal.util.IImages;

public class MaxRowsSearchParameter implements ISearchParameter, ISearchProposalProvider, IValidatable {

	private final QueryParameterName parameterName;
	private final Image image;
	private static final List<String> PROPOSALS;

	static {
		PROPOSALS = Arrays.asList("50", "100", "150", "500", "1000", "1500", "2000", "5000"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
	}

	public MaxRowsSearchParameter() {
		this.parameterName = QueryParameterName.MAX_ROWS;
		this.image = SearchAndAnalysisPlugin.getDefault().getImage(IImages.MAX_ROWS_PARAM);
	}

	@Override
	public List<IContentProposal> getProposalList(final String query) throws CoreException {
		if (query == null || query.isEmpty()) {
			return PROPOSALS.stream()
				.map((number) -> new SearchParameterProposal(number, this.parameterName, null, null))
				.collect(Collectors.toList());
		} else {
			return PROPOSALS.stream()
				.filter((number) -> number.startsWith(query))
				.map((number) -> new SearchParameterProposal(number, this.parameterName, null, query))
				.collect(Collectors.toList());
		}
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
		return NLS.bind(Messages.SearchPatternAnalyzer_DescriptionMaxRowsParameter_xmsg, new Object[] { getLabel(), "100" }); //$NON-NLS-1$
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
	public void validate(final Object value) throws CoreException {
		try {
			Integer.parseInt((String) value);
		} catch (final NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, SearchAndAnalysisPlugin.PLUGIN_ID,
				NLS.bind(Messages.SearchPatternAnalyzer_ErrorParameterValueNotNumeric_xmsg, getLabel()), e));
		}
	}

	@Override
	public boolean supportsNegatedValues() {
		return false;
	}
}
