package com.devepos.adt.saat.internal.cdsanalysis;

import java.net.URI;
import java.util.Map;

import com.devepos.adt.saat.internal.util.UriDiscoveryBase;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * URI discovery for CDS Analysis
 *
 * @author stockbal
 */
public class CdsAnalysisUriDiscovery extends UriDiscoveryBase {
	private static final String DISCOVERY_SCHEME = "http://www.devepos.com/adt/saat/cds/analysis"; //$NON-NLS-1$
	private static final String DISCOVERY_RELATION_CDS_ANALYSIS = "http://www.devepos.com/adt/relations/saat/cds/analysis"; //$NON-NLS-1$
	private static final String DISCOVERY_TERM_CDS_ANALYSIS = "cdsanalysis"; //$NON-NLS-1$

	public CdsAnalysisUriDiscovery(final String destination) {
		super(destination, DISCOVERY_SCHEME);
	}

	/**
	 * @return Retrieves Resource URI for the CDS Analysis Resource
	 */
	public URI getCdsAnalysisUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_CDS_ANALYSIS);
	}

	/**
	 * @return ADT URI template for the CDS Analysis Resource
	 */
	public IAdtUriTemplate getCdsAnalysisTemplate() {
		return getTemplate(DISCOVERY_TERM_CDS_ANALYSIS, DISCOVERY_RELATION_CDS_ANALYSIS);
	}

	/**
	 * Creates a valid REST resource URI to perform a CDS analysis for the given CDS
	 * View
	 *
	 * @param  cdsViewName  name of a CDS view
	 * @param  parameterMap map of parameters for URI template
	 * @return              REST resource URI
	 */
	public URI createCdsAnalysisResourceUri(final String cdsViewName, final Map<String, Object> parameterMap) {
		final IAdtUriTemplate template = getCdsAnalysisTemplate();
		URI uri = null;
		if (template != null) {
			if (template.containsVariable("cdsViewName")) { //$NON-NLS-1$
				template.set("cdsViewName", cdsViewName); //$NON-NLS-1$
			}
			fillTemplateWithParams(template, parameterMap);
			uri = URI.create(template.expand());
		}
		return uri;
	}
}
