/**
 *
 */
package com.devepos.adt.saat.internal.search;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.devepos.adt.base.util.AdtStaxContentHandlerUtility;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;

/**
 * Content handler for all named item resources
 *
 * @author stockbal
 */
public class NamedItemContentHandler implements IContentHandler<INamedItem[]> {
  private static final String EL_NAMED_ITEM = "namedItem";
  private static final String EL_NAME = "name";
  private static final String EL_DESCRIPTION = "description";
  private static final String EL_DATA = "data";

  protected final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();

  @Override
  public INamedItem[] deserialize(final IMessageBody body,
      final Class<? extends INamedItem[]> dataType) {
    XMLStreamReader xsr = null;
    final List<INamedItem> result = new ArrayList<>();

    try {
      xsr = utility.getXMLStreamReader(body);
      NamedItem namedItem = null;

      for (int eventId = xsr.next(); xsr.hasNext(); eventId = xsr.next()) {

        if (eventId != XMLStreamConstants.START_ELEMENT) {
          continue;
        }

        final String name = xsr.getLocalName();

        if (name.equals(EL_NAMED_ITEM)) {
          // finish previous named item
          if (namedItem != null) {
            result.add(namedItem);
          }
          namedItem = new NamedItem();
        } else if (name.equals(EL_NAME)) {
          namedItem.setName(xsr.getElementText());
        } else if (name.equals(EL_DESCRIPTION)) {
          namedItem.setDescription(xsr.getElementText());
        } else if (name.equals(EL_DATA)) {
          namedItem.setData(xsr.getElementText());
        }
      }
      if (namedItem != null) {
        result.add(namedItem);
      }
      utility.closeXMLStreamReader(xsr);
      return result.toArray(new INamedItem[result.size()]);
    } catch (final Exception e) {
      utility.closeXMLStreamReader(xsr);
      return null;
    }
  }

  @Override
  public String getSupportedContentType() {
    return AdtMediaType.APPLICATION_XML;
  }

  @Override
  public Class<INamedItem[]> getSupportedDataType() {
    return INamedItem[].class;
  }

  @Override
  public IMessageBody serialize(final INamedItem[] arg0, final Charset arg1) {
    return null;
  }
}
