package com.devepos.adt.saat.internal.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.devepos.adt.saat.internal.search.model.QueryParameterName;
import com.devepos.adt.saat.internal.util.UriDiscoveryBase;
import com.sap.adt.compatibility.uritemplate.AdtUriTemplateFactory;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * URI discovery for Object Search services
 *
 * @author stockbal
 */
public class ObjectSearchUriDiscovery extends UriDiscoveryBase {
	private static final String DISCOVERY_SCHEME = "http://www.devepos.com/adt/saat/objectsearch";
	private static final String DISCOVERY_RELATION_SEARCH = "http://www.devepos.com/adt/relations/saat/objectsearch";
	private static final String FALLBACK_QUERY_PARAMETER = "{?objectType,query,maxResults}";
	private static final String DISCOVERY_TERM_OBJECT_SEARCH = "search";
	private static final String DISCOVERY_TERM_ANNOTATION_VALUE_HELP = "annotation";
	private static final String DISCOVERY_TERM_ANNOTATION_VALUE_VALUE_HELP = "annotationvalue";
	private static final String DISCOVERY_TERM_DB_ENTITY_VH = "dbentity";
	private static final String DISCOVERY_TERM_CDS_FIELD_VH = "cdsfield";
	private static final String DISCOVERY_TERM_TABLE_FIELD_VH = "tablefield";
	private static final String DISCOVERY_TERM_CDS_TYPE_VH = "cdstype";
	private static final String DISCOVERY_TERM_RELEASE_STATE_VH = "releasestate";
	private static final String DISCOVERY_TERM_CDS_EXTENSION_VH = "cdsextension";
	private static final String NAMED_ITEM_TEMPLATE = "{?maxItemCount,name,description,data}";
	private IAdtUriTemplate fallbackTemplate;

	/**
	 * Creates new URI discovery for the Object Search services
	 *
	 * @param destination
	 */
	public ObjectSearchUriDiscovery(final String destination) {
		super(destination, DISCOVERY_SCHEME);
		final URI objectSearchUri = getObjectSearchUri();
		if (objectSearchUri != null) {
			final String template = objectSearchUri.toString() + FALLBACK_QUERY_PARAMETER;
			this.fallbackTemplate = AdtUriTemplateFactory.createUriTemplate(template);
		} else {
			this.fallbackTemplate = null;
		}
	}

	/**
	 * Retrieves a list of supported parameters for the current destination
	 *
	 * @return
	 */
	public List<QueryParameterName> getSupportedSearchParameters() {
		final IAdtUriTemplate searchUriTemplate = getObjectSearchTemplate();
		if (searchUriTemplate == null) {
			return new ArrayList<>();
		}
		return Stream.of(QueryParameterName.values())
			.filter(p -> searchUriTemplate.containsVariable(p.toString()))
			.collect(Collectors.toList());
	}

	/**
	 * Creates a valid REST resource URI from the given map of parameter values and
	 * the given query string
	 *
	 * @param  parameters map of parameter key and their corresponding values
	 * @param  query
	 * @return            REST resource URI
	 */
	public URI createResourceUriFromTemplate(final Map<String, Object> parameterMap, final String query) {
		final IAdtUriTemplate template = getObjectSearchTemplate();
		URI uri = null;
		if (template != null) {
			for (final String paramKey : parameterMap.keySet()) {
				if (template.containsVariable(paramKey)) {
					final Object paramValue = parameterMap.get(paramKey);
					if (paramValue != null) {
						template.set(paramKey, paramValue);
					}
				}
			}
			if (template.containsVariable(QueryParameterName.QUERY.toString()) && query != null && !query.isEmpty()) {
				template.set(QueryParameterName.QUERY.toString(), query);
			}
			uri = URI.create(template.expand());
		}
		return uri;
	}

	/**
	 * Retrieves URI template for the annotation value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getAnnotationValueHelpTemplate() {
		return getNamedItemTemplateForUri(getAnnotationValueHelpUri());
	}

	/**
	 * Retrieves resource URI for annotation value help
	 *
	 * @return
	 */
	public URI getAnnotationValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_ANNOTATION_VALUE_HELP);
	}

	/**
	 * Retrieves URI template for the annotation value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getAnnotationValueValueHelpTemplate() {
		return getNamedItemTemplateForUri(getAnnotationValueValueHelpUri());
	}

	/**
	 * Retrieves resource URI for annotation value help
	 *
	 * @return
	 */
	public URI getAnnotationValueValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_ANNOTATION_VALUE_VALUE_HELP);
	}

	/**
	 * Retrieves URI template for the CDS field value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getCdsFieldValueHelpTemplate() {
		return getNamedItemTemplateForUri(getCdsFieldValueHelpUri());
	}

	/**
	 * Retrieves resource URI for the CDS field value help
	 *
	 * @return
	 */
	public URI getCdsFieldValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_CDS_FIELD_VH);
	}

	/**
	 * Retrieves URI template for the CDS type value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getCdsTypeValueHelpTemplate() {
		return getNamedItemTemplateForUri(getCdsTypeValueHelpUri());
	}

	/**
	 * Retrieves resource URI for the CDS type value help
	 *
	 * @return
	 */
	public URI getCdsTypeValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_CDS_TYPE_VH);
	}

	/**
	 * Retrieves URI template for the database entity value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getDbEntityValueHelpTemplate() {
		return getNamedItemTemplateForUri(getDbEntityValueHelpUri());
	}

	/**
	 * Retrieves resource URI for the database entity value help
	 *
	 * @return
	 */
	public URI getDbEntityValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_DB_ENTITY_VH);
	}

	/**
	 * Retrieves URI template for the CDS extension value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getCdsExtensionValueHelpTemplate() {
		return getNamedItemTemplateForUri(getCdsExtensionValueHelpUri());
	}

	/**
	 * Retrieves resource URI for the CDS extension value help
	 *
	 * @return
	 */
	public URI getCdsExtensionValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_CDS_EXTENSION_VH);
	}

	/**
	 * Retrieves Resource URI for the DB object search
	 *
	 * @return
	 */
	public URI getObjectSearchUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_OBJECT_SEARCH);
	}

	/**
	 * Retrieve URI template for the object search
	 *
	 * @return
	 */
	public IAdtUriTemplate getObjectSearchTemplate() {
		final IAdtUriTemplate template = getTemplate(DISCOVERY_TERM_OBJECT_SEARCH, DISCOVERY_RELATION_SEARCH);
		return template != null ? template : this.fallbackTemplate;
	}

	/**
	 * Retrieves URI template for the Release state value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getReleaseStateValueHelpTemplate() {
		return getNamedItemTemplateForUri(getReleaseStateValueHelpUri());
	}

	/**
	 * Retrieves resource URI for the Release state value help
	 *
	 * @return
	 */
	public URI getReleaseStateValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_RELEASE_STATE_VH);
	}

	/**
	 * Retrieves URI template for the table field value help
	 *
	 * @return
	 */
	public IAdtUriTemplate getTableFieldValueHelpTemplate() {
		return getNamedItemTemplateForUri(getTableFieldValueHelpUri());
	}

	/**
	 * Retrieves resource URI for the table field value help
	 *
	 * @return
	 */
	public URI getTableFieldValueHelpUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_TABLE_FIELD_VH);
	}

	private IAdtUriTemplate getNamedItemTemplateForUri(final URI uri) {
		IAdtUriTemplate uriTemplate = null;
		if (uri != null) {
			uriTemplate = AdtUriTemplateFactory.createUriTemplate(uri.toString() + NAMED_ITEM_TEMPLATE);
		}
		return uriTemplate;
	}

}
