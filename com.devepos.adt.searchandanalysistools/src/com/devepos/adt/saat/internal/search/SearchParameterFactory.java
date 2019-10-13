package com.devepos.adt.saat.internal.search;

import org.eclipse.osgi.util.NLS;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
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
				new Object[] { QueryParameterName.DESCRIPTION.getLowerCaseKey(), "*material*" }),
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
				new Object[] { QueryParameterName.HAS_PARAMS.getLowerCaseKey(), "true" }),
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
				new Object[] { QueryParameterName.CDS_PARAMETER.getLowerCaseKey(), "p_plant" }),
			SearchAndAnalysisPlugin.getDefault().getImage(IImages.PARAMETER_PARAM));
	}
}
