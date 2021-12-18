package com.devepos.adt.saat.internal.search;

import javax.management.modelmbean.XMLParseException;
import javax.xml.stream.XMLStreamReader;

import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.util.IXmlElement;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

public class AdtObjectReferenceDeserializer {
  public static final String NS_URI_ADT_CORE = "http://www.sap.com/adt/core";

  /**
   * Deserialize ADT Object reference via given XMLStreamReader
   *
   * @param xsr
   * @return deserialized adt object reference
   * @throws XMLParseException
   */
  public static IAdtObjectReference deserializeFromStream(final XMLStreamReader xsr,
      final boolean checkForMandatoryAttributes) throws XMLParseException {
    final IAdtObjectReference objectReference = AdtObjectReferenceModelFactory.createReference();

    final String nameSpace = NS_URI_ADT_CORE;
    final String refStr = xsr.getAttributeValue(nameSpace, "uri");

    if (refStr != null && refStr.length() > 0) {
      objectReference.setUri(refStr);
    } else if (checkForMandatoryAttributes) {
      throw new XMLParseException("URI must not be empty");
    }
    objectReference.setName(xsr.getAttributeValue(nameSpace, "name"));
    objectReference.setType(xsr.getAttributeValue(nameSpace, "type"));
    final String parentStr = xsr.getAttributeValue(nameSpace, "parentUri");
    if (parentStr != null && parentStr.length() > 0) {
      objectReference.setParentUri(parentStr);
    }
    objectReference.setPackageName(xsr.getAttributeValue(nameSpace, "packageName"));
    if (objectReference.getPackageName() == null) {
      objectReference.setPackageName(xsr.getAttributeValue(nameSpace, "package"));
    }
    objectReference.setDescription(xsr.getAttributeValue(nameSpace, "description"));

    return objectReference;
  }

  /**
   * Deserialize ADT Object from the given XML element
   *
   * @param element an XML element
   * @return an ADT object reference
   */
  public static IAdtObjectReference deserializeFromElement(final IXmlElement element) {
    if (element == null) {
      return null;
    }
//		Assert.isNotNull(element);
    final IAdtObjectReference objectReference = AdtObjectReferenceModelFactory.createReference();

    objectReference.setUri(element.getAttributeValue("uri"));
    objectReference.setName(element.getAttributeValue("name"));
    objectReference.setType(element.getAttributeValue("type"));
    objectReference.setParentUri(element.getAttributeValue("parentUri"));
    objectReference.setPackageName(element.getAttributeValue("packageName"));
    objectReference.setDescription(element.getAttributeValue("description"));
    return objectReference;
  }
}
