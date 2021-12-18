package com.devepos.adt.saat.internal.elementinfo;

import java.util.HashMap;
import java.util.Map;

import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;

/**
 * Utility for ADT Content Handlers
 *
 * @author stockbal
 */
public class ContentHandlerUtil {
  /**
   * Deserializes property {@link Map} from an XML Element collection
   *
   * @param xmlElement xml element which potentially holds a list of properties
   *                   (key/value pairs)
   * @return a Map of all found properties
   */
  public static Map<String, String> deserializeProperties(final IXmlElement xmlElement) {
    final Map<String, String> properties = new HashMap<>();
    final IXmlElement propertiesEl = xmlElement.getName().equals(IXmlTags.EL_PROPERTIES)
        ? xmlElement
        : xmlElement.getChildren()
            .stream()
            .filter(c -> c.getName().equals(IXmlTags.EL_PROPERTIES))
            .findFirst()
            .orElse(null);
    if (propertiesEl != null) {
      for (final IXmlElement propertyEl : propertiesEl.getChildren()) {
        final String key = propertyEl.getAttributeValue(IXmlTags.AT_KEY);
        final String value = propertyEl.getText();
        if (key != null && value != null) {
          properties.put(key, value);
        }
      }
    }
    return properties;
  }
}
