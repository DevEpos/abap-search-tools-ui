package com.devepos.adt.saat.internal.analytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;

/**
 * Content handler to deserialize an Analysis for Office launcher file
 *
 * @author stockbal
 */
public class AnalysisForOfficeLauncherContentHandler implements IContentHandler<String> {

  @Override
  public String deserialize(final IMessageBody body, final Class<? extends String> clazz) {
    try {
      final InputStream is = body.getContent();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      final StringBuilder out = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        out.append(line);
      }
      reader.close();
      return out.toString();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public String getSupportedContentType() {
    return AdtMediaType.APPLICATION_XML;
  }

  @Override
  public Class<String> getSupportedDataType() {
    return String.class;
  }

  @Override
  public IMessageBody serialize(final String arg0, final Charset arg1) {
    return null;
  }

}
