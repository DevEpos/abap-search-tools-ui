package com.devepos.adt.saat.internal.search;

import java.net.URI;

import com.devepos.adt.base.util.AdtUtil;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.session.AdtSystemSessionFactory;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;

/**
 * Service to retrieve named items
 *
 * @author stockbal
 */
public class NamedItemService implements INamedItemService {

  private final String destination;

  public NamedItemService(final String destination) {
    this.destination = destination;
  }

  @Override
  public INamedItem[] getNamedItems(final NamedItemType type, final int maxResults) {
    return getNamedItems(type, maxResults, null, null, null);
  }

  @Override
  public INamedItem[] getNamedItems(final NamedItemType type, final int maxResults,
      final String name) {
    return getNamedItems(type, maxResults, name, null, null);
  }

  @Override
  public INamedItem[] getNamedItems(final NamedItemType type, final int maxResults,
      final String name, final String description) {
    return getNamedItems(type, maxResults, name, description, null);
  }

  @Override
  public INamedItem[] getNamedItems(final NamedItemType type, final int maxResults,
      final String name, final String description, final String data) {
    INamedItem[] namedItems = null;
    final IAdtUriTemplate template = getNamedItemTemplate(type);
    if (template != null) {
      fillTemplate(template, maxResults, name, description, data);
      final URI resourceUri = URI.create(template.expand());
      // create resource and fire request
      final IRestResource resource = AdtRestResourceFactory.createRestResourceFactory()
          .createRestResource(resourceUri, AdtSystemSessionFactory.createSystemSessionFactory()
              .createStatelessSession(destination));
      resource.addContentHandler(new NamedItemContentHandler());
      namedItems = resource.get(null, AdtUtil.getHeaders(), INamedItem[].class);
    }
    return namedItems;
  }

  /**
   * Fills the template URI with the given parameter values
   *
   * @param template
   * @param maxResults
   * @param name
   * @param description
   * @param data
   */
  private void fillTemplate(final IAdtUriTemplate template, final int maxResults, final String name,
      final String description, final String data) {
    if (maxResults > 0) {
      template.set("maxItemCount", maxResults);
    }
    if (name != null) {
      template.set("name", name);
    }
    if (description != null) {
      template.set("description", description);
    }
    if (data != null) {
      template.set("data", data);
    }
  }

  /**
   * Retrieves an URI template for the given named item type
   *
   * @param type type of named item
   * @return
   */
  private IAdtUriTemplate getNamedItemTemplate(final NamedItemType type) {
    return new ObjectSearchUriDiscovery(destination).getNamedItemTemplate(type.getDiscoveryTerm());
  }

}
