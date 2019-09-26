package com.devepos.adt.saat.internal.cdsanalysis;

import java.net.URI;

import com.devepos.adt.saat.internal.util.UriDiscoveryBase;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * URI discovery for Field Analysis
 *
 * @author stockbal
 */
public class FieldAnalysisUriDiscovery extends UriDiscoveryBase {
	private static final String DISCOVERY_SCHEME = "http://www.devepos.com/adt/saat/dbfield/hierarchy"; //$NON-NLS-1$
	private static final String DISCOVERY_RELATION_FIELD_ANALYSIS = "http://www.devepos.com/adt/relations/saat/dbfield/hierarchy"; //$NON-NLS-1$
	private static final String DISCOVERY_TERM_FIELD_ANALYSIS = "db-fields-hierarchy"; //$NON-NLS-1$
	private static final String FIELD_PARAMETER = "field"; //$NON-NLS-1$
	private static final String MODE_PARAMETER = "mode"; //$NON-NLS-1$
	private static final String NAME_PARAMETER = "name"; //$NON-NLS-1$
	private static final String SEARCH_CALC_FIELDS_PARAMETER = "searchCalcFields"; //$NON-NLS-1$
	private static final String SEARCH_DB_VIEWS_PARAMETER = "searchDbViewUsages"; //$NON-NLS-1$

	public FieldAnalysisUriDiscovery(final String destination) {
		super(destination, DISCOVERY_SCHEME);
	}

	/**
	 * @return Retrieves Resource URI for the Database field Analysis
	 */
	public URI getFieldAnalysisUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_FIELD_ANALYSIS);
	}

	/**
	 * @return ADT URI template for the Database field Analysis Resource
	 */
	public IAdtUriTemplate getFieldAnalysisTemplate() {
		return getTemplate(DISCOVERY_TERM_FIELD_ANALYSIS, DISCOVERY_RELATION_FIELD_ANALYSIS);
	}

	/**
	 * Creates a valid REST resource URI to perform a top-down analysis for the
	 * given CDS View field
	 *
	 * @param  cdsViewName name of a CDS view
	 * @param  field       name of a field in the given CDS view
	 * @return             REST resource URI
	 */
	public URI createTopDownCdsAnalysisResourceUri(final String cdsViewName, final String field) {
		URI uri = null;
		final IAdtUriTemplate template = createResourceUriTemplate(cdsViewName, field, "hierarchy"); //$NON-NLS-1$
		if (template != null) {
			uri = URI.create(template.expand());
		}
		return uri;
	}

	/**
	 * Creates a valid REST resource URI to perform a Where-Used Analysis for the
	 * given Database entity field
	 *
	 * @param  objectName       name of a Database entity
	 * @param  field            name of a field in the given database entity
	 * @param  searchCalcFields if <code>true</code> calculated fields should be
	 *                          considered during analysis
	 * @param  searchDbViews    if <code>true</code> database views should be
	 *                          considered during analysis
	 * @return                  REST resource URI
	 */
	public URI createWhereUsedAnalysisResourceUri(final String objectName, final String field, final boolean searchCalcFields,
		final boolean searchDbViews) {
		URI uri = null;
		final IAdtUriTemplate template = createResourceUriTemplate(objectName, field, "whereUsed"); //$NON-NLS-1$
		if (template != null) {
			if (template.containsVariable(SEARCH_CALC_FIELDS_PARAMETER) && searchCalcFields) {
				template.set(SEARCH_CALC_FIELDS_PARAMETER, "X");
			}
			if (template.containsVariable(SEARCH_DB_VIEWS_PARAMETER) && searchDbViews) {
				template.set(SEARCH_DB_VIEWS_PARAMETER, "X");
			}
			uri = URI.create(template.expand());
		}
		return uri;
	}

	private IAdtUriTemplate createResourceUriTemplate(final String name, final String field, final String mode) {
		final IAdtUriTemplate template = getFieldAnalysisTemplate();
		if (template != null) {
			if (template.containsVariable(NAME_PARAMETER)) {
				template.set(NAME_PARAMETER, name);
			}
			if (template.containsVariable(FIELD_PARAMETER)) {
				template.set(FIELD_PARAMETER, field);
			}
			if (template.containsVariable(MODE_PARAMETER)) {
				template.set(MODE_PARAMETER, mode);
			}
		}
		return template;
	}
}
