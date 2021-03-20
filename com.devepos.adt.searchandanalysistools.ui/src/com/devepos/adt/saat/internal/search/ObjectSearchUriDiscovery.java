package com.devepos.adt.saat.internal.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.devepos.adt.base.util.UriDiscoveryBase;
import com.devepos.adt.saat.internal.util.IUriDiscoveryPaths;
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
    private static final String DISCOVERY_TERM_OBJECT_SEARCH = "search";
    private static final String NAMED_ITEM_TEMPLATE = "{?maxItemCount,name,description,data}";

    /**
     * Creates new URI discovery for the Object Search services
     *
     * @param destination
     */
    public ObjectSearchUriDiscovery(final String destination) {
        super(destination, IUriDiscoveryPaths.OBJECT_SEARCH_DISOCOVERY_PATH, DISCOVERY_SCHEME);
    }

    /**
     * Retrieves a list of supported parameters for the current destination
     *
     * @return
     */
    public List<QueryParameterName> getSupportedSearchParameters(final SearchType searchType) {
        final IAdtUriTemplate searchUriTemplate = getObjectSearchTemplate(searchType);
        if (searchUriTemplate == null) {
            return new ArrayList<>();
        }
        return Stream.of(QueryParameterName.values())
                .filter(p -> searchUriTemplate.containsVariable(p.toString()))
                .collect(Collectors.toList());
    }

    /**
     * Returns <code>true</code> if the given parameter is supported for the given
     * search type
     *
     * @param searchType the search type
     * @param parameter  the parameter to be checked
     * @return
     */
    public boolean isParameterSupported(final QueryParameterName parameter, final SearchType searchType) {
        final IAdtUriTemplate searchUriTemplate = getObjectSearchTemplate(searchType);
        if (searchUriTemplate == null) {
            return false;
        }
        return searchUriTemplate.containsVariable(parameter.toString());
    }

    /**
     * Creates a valid REST resource URI from the given map of parameter values and
     * the given query string
     *
     * @param parameters map of parameter key and their corresponding values
     * @param query
     * @return REST resource URI
     */
    public URI createResourceUriFromTemplate(final SearchType searchType, final Map<String, Object> parameterMap) {
        final IAdtUriTemplate template = getObjectSearchTemplate(searchType);
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
            uri = URI.create(template.expand());
        }
        return uri;
    }

    public IAdtUriTemplate getTemplateByDiscoveryTerm(final String discoveryTerm) {
        final URI uri = getUriFromCollectionMember(discoveryTerm);
        return uri != null ? getNamedItemTemplateForUri(uri) : null;
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
    public IAdtUriTemplate getObjectSearchTemplate(final SearchType searchType) {
        return getTemplate(DISCOVERY_TERM_OBJECT_SEARCH, DISCOVERY_RELATION_SEARCH + "/" + searchType.getUriTerm());
    }

    private IAdtUriTemplate getNamedItemTemplateForUri(final URI uri) {
        IAdtUriTemplate uriTemplate = null;
        if (uri != null) {
            uriTemplate = AdtUriTemplateFactory.createUriTemplate(uri.toString() + NAMED_ITEM_TEMPLATE);
        }
        return uriTemplate;
    }

}
