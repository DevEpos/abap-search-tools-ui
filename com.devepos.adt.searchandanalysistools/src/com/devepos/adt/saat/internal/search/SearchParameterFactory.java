package com.devepos.adt.saat.internal.search;

import org.eclipse.osgi.util.NLS;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Factory for creating parameters for the Object Search
 *
 * @author stockbal
 */
public class SearchParameterFactory {

	/**
	 * Creates description parameter
	 *
	 * @return the created parameter instance
	 */
	public static ISearchParameter createDescriptionParameter() {
		return new SearchParameter(QueryParameterName.DESCRIPTION,
			NLS.bind(Messages.SearchPatternAnalyzer_DescriptionDescriptionParameter_xmsg,
				new Object[] { QueryParameterName.DESCRIPTION.getLowerCaseKey(), "*material*" }), //$NON-NLS-1$
			SearchAndAnalysisPlugin.getDefault().getImage(IImages.DESCRIPTION_PARAM));
	}

	/**
	 * Creates parameter for restricting CDS View search results to CDS View that
	 * have (not) parameters
	 *
	 * @return the created parameter instance
	 */
	public static ISearchParameter createHasParameterParameter() {
		return new BooleanSearchParameter(QueryParameterName.HAS_PARAMS,
			NLS.bind(Messages.SearchPatternAnalyzer_DescriptionParamsParameter_xmsg,
				new Object[] { QueryParameterName.HAS_PARAMS.getLowerCaseKey(), "true" }), //$NON-NLS-1$
			SearchAndAnalysisPlugin.getDefault().getImage(IImages.PARAMETER_PARAM));
	}

	/**
	 * Creates parameter for restricting CDS View search results to CDS Views with a
	 * certain parameter
	 *
	 * @return the created parameter instance
	 */
	public static ISearchParameter createCdsParamParameter() {
		return new SearchParameter(QueryParameterName.CDS_PARAMETER,
			NLS.bind(Messages.SearchPatternAnalyzer_DescriptionParamParameter_xmsg,
				new Object[] { QueryParameterName.CDS_PARAMETER.getLowerCaseKey(), "p_plant" }), //$NON-NLS-1$
			SearchAndAnalysisPlugin.getDefault().getImage(IImages.PARAMETER_PARAM));
	}

	/**
	 * Creates search parameter for CDS types
	 *
	 * @param  projectProvider provider for ABAP Project
	 * @return                 the created parameter instance
	 */
	public static ISearchParameter createCdsTypeParameter(final IAbapProjectProvider projectProvider) {
		final NamedItemParameter parameter = new NamedItemParameter(projectProvider, QueryParameterName.TYPE,
			NamedItemType.CDS_TYPE, true, ""); //$NON-NLS-1$
		parameter.setDescription(NLS.bind(Messages.SearchPatternAnalyzer_DescriptionCdsTypeParameter_xmsg,
			new Object[] { QueryParameterName.TYPE.getLowerCaseKey(), "function" })); //$NON-NLS-1$
		parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.TYPE_PARAM));
		parameter.setSupportsNegatedValues(true);
		return parameter;
	}

	/**
	 * Creates search parameter for table types
	 *
	 * @param  projectProvider provider for ABAP Project
	 * @return                 the created parameter instance
	 */
	public static ISearchParameter createTableTypeParameter(final IAbapProjectProvider projectProvider) {
		final NamedItemParameter parameter = new NamedItemParameter(projectProvider, QueryParameterName.TYPE,
			NamedItemType.TABLE_TYPE, true, ""); //$NON-NLS-1$
		parameter.setDescription(
			NLS.bind(Messages.SearchPatternAnalyzer_DescriptionTableTypeParameter_xmsg,
				new Object[] { QueryParameterName.TYPE.getLowerCaseKey(), "table" })); //$NON-NLS-1$
		parameter.setImage(SearchAndAnalysisPlugin.getDefault().getImage(IImages.TYPE_PARAM));
//		parameter.setSupportsNegatedValues(true);
		return parameter;
	}
}
