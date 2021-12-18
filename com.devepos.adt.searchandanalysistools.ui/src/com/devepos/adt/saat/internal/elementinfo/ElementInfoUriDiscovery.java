package com.devepos.adt.saat.internal.elementinfo;

import java.net.URI;
import java.util.Map;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.util.UriDiscoveryBase;
import com.devepos.adt.saat.internal.util.IUriDiscoveryPaths;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * URI discovery for element info
 *
 * @author stockbal
 */
public class ElementInfoUriDiscovery extends UriDiscoveryBase {
  private static final String DISCOVERY_SCHEME = "http://www.devepos.com/adt/saat/elementinfo";
  private static final String DISCOVERY_RELATION_ELEMENT_INFO = "http://www.devepos.com/adt/relations/saat/elementinfo";
  private static final String DISCOVERY_RELATION_ELEMENT_INFO_BY_URI = "http://www.devepos.com/adt/relations/saat/elementinfo/byUri";
  private static final String DISCOVERY_RELATION_CDS_SEC_ELEMENT_INFO = "http://www.devepos.com/adt/relations/saat/elementinfo/cds/secondary";
  private static final String DISCOVERY_TERM_ELEMENT_INFO = "elementinfo";

  /**
   * Creates new URI discovery for the element information service
   *
   * @param destination
   */
  public ElementInfoUriDiscovery(final String destination) {
    super(destination, IUriDiscoveryPaths.OBJECT_SEARCH_DISOCOVERY_PATH, DISCOVERY_SCHEME);
  }

  /**
   * @return Retrieves Resource URI for the element information service
   */
  public URI getElementInfoUri() {
    return getUriFromCollectionMember(DISCOVERY_TERM_ELEMENT_INFO);
  }

  /**
   * @return ADT URI template for the element information service
   */
  public IAdtUriTemplate getElementInfoTemplate() {
    return getTemplate(DISCOVERY_TERM_ELEMENT_INFO, DISCOVERY_RELATION_ELEMENT_INFO);
  }

  /**
   * @return ADT URI template for the element information service
   */
  public IAdtUriTemplate getElementInfoByUriTemplate() {
    return getTemplate(DISCOVERY_TERM_ELEMENT_INFO, DISCOVERY_RELATION_ELEMENT_INFO_BY_URI);
  }

  /**
   * @return ADT URI template for retrieving CDS element information
   */
  public IAdtUriTemplate getCDSSecondaryElementInfoTemplate() {
    return getTemplate(DISCOVERY_TERM_ELEMENT_INFO, DISCOVERY_RELATION_CDS_SEC_ELEMENT_INFO);

  }

  /**
   * Creates a valid REST resource URI for the given name and object type
   *
   * @param name       the name of the object for which element infos should be
   *                   retrieved
   * @param objectType the type of the object
   * @param params     map of additional query parameters
   * @return REST resource URI
   */
  public URI createElementInfoResourceUri(final String name, final ObjectType objectType,
      final Map<String, Object> params) {
    final IAdtUriTemplate template = getElementInfoTemplate();
    URI uri = null;
    if (template != null) {
      if (template.containsVariable("objectName")) {
        template.set("objectName", name);
      }
      if (template.containsVariable("objectType")) {
        template.set("objectType", objectType.getId());
      }
      fillTemplateWithParams(template, params);
      uri = URI.create(template.expand());
    }
    return uri;
  }

  /**
   * Creates a valid REST resource URI for the given object URI
   *
   * @param objectUri the URI of an ADT object
   * @param params    map of additional query parameters
   * @return REST resource URI
   */
  public URI createElementInfoResourceUri(final String objectUri,
      final Map<String, Object> params) {
    final IAdtUriTemplate template = getElementInfoByUriTemplate();
    URI uri = null;
    if (template != null) {
      if (template.containsVariable("objectUri")) {
        template.set("objectUri", objectUri);
      }
      fillTemplateWithParams(template, params);
      uri = URI.create(template.expand());
    }
    return uri;
  }

  /**
   * Creates REST resource URI for retrieving element information of a CDS view
   *
   * @param cdsViewName name of a CDS view
   * @return the created REST resource URI
   */
  public URI createCDSSecondaryElementInfoResourceURI(final String cdsViewName) {
    final IAdtUriTemplate template = getCDSSecondaryElementInfoTemplate();
    URI uri = null;
    if (template != null) {
      if (template.containsVariable("objectName")) {
        template.set("objectName", cdsViewName);
      }
      uri = URI.create(template.expand());
    }
    return uri;
  }
}
