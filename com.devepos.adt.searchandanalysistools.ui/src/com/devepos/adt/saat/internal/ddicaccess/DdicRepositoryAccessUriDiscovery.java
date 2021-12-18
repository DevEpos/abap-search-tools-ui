package com.devepos.adt.saat.internal.ddicaccess;

import java.net.URI;
import java.util.List;

import com.devepos.adt.base.util.UriDiscoveryBase;
import com.devepos.adt.saat.internal.util.IUriDiscoveryPaths;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * Uri discovery for DDIC Repository Access
 *
 * @author stockbal
 */
public class DdicRepositoryAccessUriDiscovery extends UriDiscoveryBase {
  private static final String DISCOVERY_SCHEME = "http://www.devepos.com/adt/saat/ddicaccess";
  private static final String DISCOVERY_RELATION_DDIC_ACCESS = "http://www.devepos.com/adt/relations/saat/ddicaccess";
  private static final String DISCOVERY_TERM_DDIC_ACCESS = "ddicAccess";

  /**
   * Creates new URI discovery for DDIC Repository Access
   *
   * @param destination
   */
  public DdicRepositoryAccessUriDiscovery(final String destination) {
    super(destination, IUriDiscoveryPaths.OBJECT_SEARCH_DISOCOVERY_PATH, DISCOVERY_SCHEME);
  }

  /**
   * @return Retrieves Resource URI for DDIC Repository access
   */
  public URI getDdicAccessUri() {
    return getUriFromCollectionMember(DISCOVERY_TERM_DDIC_ACCESS);
  }

  /**
   * @return ADT URI template for DDIC Repository Access
   */
  public IAdtUriTemplate getDdicAccessUriTemplate() {
    return getTemplate(DISCOVERY_TERM_DDIC_ACCESS, DISCOVERY_RELATION_DDIC_ACCESS);
  }

  /**
   * Creates a valid REST resource for a DDIC Repository Access
   *
   * @param accessMode type of DDIC Access that should be performed
   * @param objectUri  URI String of an ADT Object
   * @param paths      a list of paths to be resolved
   * @param filters    a list of optional filters to be applied <br>
   *                   each filter must follow the pattern key:value
   * @return REST resource URI
   */
  public URI createDdicAccessResource(final String accessMode, final String objectUri,
      final List<String> paths, final List<String> filters) {
    final IAdtUriTemplate template = getDdicAccessUriTemplate();
    URI uri = null;
    if (template != null) {
      if (template.containsVariable("accessMode")) {
        template.set("accessMode", accessMode);
      }
      if (template.containsVariable("uri") && objectUri != null && !objectUri.isEmpty()) {
        template.set("uri", objectUri);
      }
      if (template.containsVariable("path") && paths != null && !paths.isEmpty()) {
        template.set("path", paths);
      }
      if (template.containsVariable("filter") && filters != null && !filters.isEmpty()) {
        template.set("filter", filters);
      }
      uri = URI.create(template.expand());
    }
    return uri;
  }

}
