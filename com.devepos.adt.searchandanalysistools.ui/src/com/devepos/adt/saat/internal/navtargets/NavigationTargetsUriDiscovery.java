package com.devepos.adt.saat.internal.navtargets;

import java.net.URI;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.util.UriDiscoveryBase;
import com.devepos.adt.saat.internal.util.IUriDiscoveryPaths;
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
    super(destination, IUriDiscoveryPaths.OBJECT_SEARCH_DISOCOVERY_PATH, DISCOVERY_SCHEME);
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
   * @param objectName the name of an ADT object
   * @param objectType the type of an ADT object
   * @return REST resource URI
   */
  public URI createNavTargetsResourceUri(final String objectName, final ObjectType objectType) {
    final IAdtUriTemplate template = getNavTargetsTemplate();
    URI uri = null;
    if (template != null) {
      if (!template.containsVariable("objectName")) {
        return null;
      }
      template.set("objectName", objectName);
      if (!template.containsVariable("objectType")) {
        return null;
      }
      template.set("objectType", objectType.getId());
      uri = URI.create(template.expand());
    }
    return uri;
  }
}
