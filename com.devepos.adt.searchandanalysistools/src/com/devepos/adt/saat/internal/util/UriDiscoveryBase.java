package com.devepos.adt.saat.internal.util;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.sap.adt.communication.resources.ResourceForbiddenException;
import com.sap.adt.compatibility.discovery.AdtDiscoveryFactory;
import com.sap.adt.compatibility.discovery.IAdtDiscovery;
import com.sap.adt.compatibility.discovery.IAdtDiscoveryCollectionMember;
import com.sap.adt.compatibility.model.templatelink.IAdtTemplateLink;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * Base for URI discovery
 *
 * @author stockbal
 */
public abstract class UriDiscoveryBase {

	private static final String DISCOVERY_PATH = "/devepos/saat/discovery";
	private final String discoveryScheme;
	protected final IAdtDiscovery discovery;

	public UriDiscoveryBase(final String destination, final String discoveryScheme) {
		this.discoveryScheme = discoveryScheme;
		this.discovery = AdtDiscoveryFactory.createDiscovery(destination, URI.create(DISCOVERY_PATH));
	}

	/**
	 * Retrieves URI of a collection member for the given term
	 *
	 * @param  discoveryTerm the term to be used to find the collection member
	 * @return
	 */
	protected URI getUriFromCollectionMember(final String discoveryTerm) {
		URI uri = null;
		try {
			final IAdtDiscoveryCollectionMember collectionMember = this.discovery.getCollectionMember(this.discoveryScheme,
				discoveryTerm, null);
			if (collectionMember != null) {
				uri = collectionMember.getUri();
			}
		} catch (final ResourceForbiddenException e) {
			e.printStackTrace();
		}
		return uri;
	}

	/**
	 * Retrieves an {@link IAdtUriTemplate} for the given <b>scheme</b>, <b>term</b>
	 * and <b>relation</b>
	 *
	 * @param  term     the term used to get the collection member
	 * @param  relation the URL relation to get the correct template link
	 * @return
	 */
	protected IAdtUriTemplate getTemplate(final String term, final String relation) {
		IAdtUriTemplate template = null;
		final IAdtDiscoveryCollectionMember collectionMember = this.discovery.getCollectionMember(this.discoveryScheme, term,
			new NullProgressMonitor());
		if (collectionMember != null) {
			final IAdtTemplateLink templateLink = collectionMember.getTemplateLink(relation);
			if (templateLink != null) {
				template = templateLink.getUriTemplate();
			}
		}
		return template;
	}

	/**
	 * Fills the template with parameters in provided {@link Map}
	 *
	 * @param template the ADT URI template
	 * @param params   a Map of parameters for the template
	 */
	protected void fillTemplateWithParams(final IAdtUriTemplate template, final Map<String, Object> params) {
		if (params == null || template == null) {
			return;
		}

		for (final String key : params.keySet()) {
			if (template.containsVariable(key)) {
				fillTemplateValue(template, key, params.get(key));
			}
		}
	}

	private void fillTemplateValue(final IAdtUriTemplate template, final String paramater, final Object value) {
		if (value == null) {
			return;
		}
		template.set(paramater, value);
	}
}