package com.devepos.adt.saat.internal.navtargets;

import java.net.URI;

import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.internal.util.UriDiscoveryBase;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * URI discovery for Navigation targets of an ADT Object
 *
 * @author stockbal
 */
public class NavigationTargetsUriDiscovery extends UriDiscoveryBase {
	private static final String DISCOVERY_SCHEME = "http://www.devepos.com/adt/saat/navigationtargets";
	private static final String DISCOVERY_RELATION_NAV_TARGETS = "http://www.devepos.com/adt/relations/saat/navigationtargets";
	private static final String DISCOVERY_TERM_NAV_TARGETS = "navigationtargets";

	public NavigationTargetsUriDiscovery(final String destination) {
		super(destination, DISCOVERY_SCHEME);
	}

	/**
	 * @return Retrieves Resource URI for the navigation targets of an ADT object
	 */
	public URI getNavTargetsUri() {
		return getUriFromCollectionMember(DISCOVERY_TERM_NAV_TARGETS);
	}

	/**
	 * @return ADT URI template for the navigation targets of an ADT object
	 */
	public IAdtUriTemplate getNavTargetsTemplate() {
		return getTemplate(DISCOVERY_TERM_NAV_TARGETS, DISCOVERY_RELATION_NAV_TARGETS);
	}

	/**
	 * Creates a valid REST resource URI to read the navigation targets of the ADT
	 * object with the given name and type
	 *
	 * @param  objectName the name of an ADT object
	 * @param  objectType the type of an ADT object
	 * @return            REST resource URI
	 */
	public URI createNavTargetsResourceUri(final String objectName, final ObjectType objectType) {
		final IAdtUriTemplate template = getNavTargetsTemplate();
		URI uri = null;
		if (template != null) {
			if (template.containsVariable("objectName")) {
				template.set("objectName", objectName);
			} else {
				return null;
			}
			if (template.containsVariable("objectType")) {
				template.set("objectType", objectType.getId());
			} else {
				return null;
			}
			uri = URI.create(template.expand());
		}
		return uri;
	}
}
