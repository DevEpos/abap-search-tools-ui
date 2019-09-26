package com.devepos.adt.saat.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.sap.adt.communication.content.ContentHandlerException;
import com.sap.adt.communication.message.ByteArrayMessageBody;
import com.sap.adt.communication.message.IMessageBody;

public class AdtStaxContentHandlerUtility {
	public static final String XML_VERSION_1_0 = "1.0";
	public static final String NO_CONTENT = "no content";
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private ByteArrayOutputStream stream = null;
	private boolean withCDataEvent = false;

	/**
	 * Retrieve attribute with the given name and XML Stream reader. If the
	 * attribute could not be found a {@link ContentHandlerException} will be thrown
	 *
	 * @param  xsr           the stream reader to used to read the attribute
	 * @param  attributeName the attribute to be used
	 * @return               the found value of the given attribute
	 */
	public String getAttribute(final XMLStreamReader xsr, final String attributeName) {
		final String attributeValue = xsr.getAttributeValue(null, attributeName);
		if (attributeValue == null) {
			throw new ContentHandlerException("Attribute " + attributeName + " not set");
		}
		return attributeValue;
	}

	/**
	 * Parses the XML from the message body
	 *
	 * @param  body             the message body of the rest response
	 * @param  elementsWithText an optional list of elements which possess text
	 *                          content
	 * @return                  the parsed root element
	 */
	public IXmlElement parseXML(final IMessageBody body, final String... elementsWithText) {
		try {
			return new XmlStreamParser(getXMLStreamReader(body), elementsWithText).parseXML();
		} catch (final XMLStreamException exc) {
		}
		return null;
	}

	/**
	 * Perform a pre serialization check. Throws an exception if the given
	 * dataObject is <code>null</code>
	 *
	 * @param dataObject
	 */
	public void serializeCheck(final Object dataObject) {
		if (dataObject == null) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Checks if the given charset is <code>null</code> and sets it to the default
	 * charset "UTF-8" if it is
	 *
	 * @param  charset
	 * @return
	 */
	public Charset checkCharsetNotNull(Charset charset) {
		if (charset == null) {
			charset = DEFAULT_CHARSET;
		}
		return charset;
	}

	/**
	 * Retrieves and an XML stream writer and writes the document start tag
	 *
	 * @param  charset            the charset to be used
	 * @param  version            the xml version
	 * @return
	 * @throws XMLStreamException
	 */
	public XMLStreamWriter getXMLStreamWriterAndStartDocument(Charset charset, final String version) throws XMLStreamException {
		this.stream = new ByteArrayOutputStream();

		charset = checkCharsetNotNull(charset);

		final XMLStreamWriter xsw = getXMLStreamWriter(this.stream, charset);

		xsw.writeStartDocument(charset.name(), version);

		return xsw;
	}

	/**
	 * Retrieves an XML stream writer for the given output stream
	 *
	 * @param  stream             the output stream to be used for the XML writer
	 * @param  charset            the charset to be used
	 * @return
	 * @throws XMLStreamException
	 */
	public XMLStreamWriter getXMLStreamWriter(final OutputStream stream, final Charset charset) throws XMLStreamException {
		final XMLOutputFactory xof = XMLOutputFactory.newInstance();

		final XMLStreamWriter xsw = xof.createXMLStreamWriter(stream, charset.name());

		return xsw;
	}

	/**
	 * Retrieves an XML stream reader for the given message body
	 *
	 * @param  body               used to get the XML stream reader
	 * @return
	 * @throws XMLStreamException
	 */
	public XMLStreamReader getXMLStreamReader(final IMessageBody body) throws XMLStreamException {
		if (body == null || body.getContentLength() == 0L) {
			throw new IllegalArgumentException("no content");
		}
		try {
			final InputStream is = body.getContent();
			return getXMLStreamReader(is);
		} catch (final IOException e) {
			throw new ContentHandlerException(e.getMessage(), e);
		}
	}

	public XMLStreamReader getXMLStreamReaderWithCDataEvent(final IMessageBody body) throws XMLStreamException {
		this.withCDataEvent = true;
		return getXMLStreamReader(body);
	}

	public XMLStreamReader getXMLStreamReader(final InputStream stream) throws XMLStreamException {
		if (stream == null) {
			throw new IllegalArgumentException("no content");
		}
		final XMLInputFactory xif = XMLInputFactory.newInstance();
		if (this.withCDataEvent) {
			xif.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
		}
		return xif.createXMLStreamReader(stream);
	}

	public XMLStreamReader getXMLStreamReader(final String content) throws XMLStreamException {
		if (content == null || content.length() == 0) {
			throw new IllegalArgumentException("no content");
		}
		final XMLInputFactory xif = XMLInputFactory.newInstance();

		return xif.createXMLStreamReader(new StringReader(content));
	}

	public void closeXMLStreamWriter(final XMLStreamWriter xsw) {
		if (xsw != null) {
			try {
				xsw.close();
			} catch (final XMLStreamException localXMLStreamException) {
			}
		}
		try {
			this.stream.close();
		} catch (final IOException localIOException) {
		}
	}

	public void closeXMLStreamReader(final XMLStreamReader xsr) {
		if (xsr != null) {
			try {
				xsr.close();
			} catch (final XMLStreamException localXMLStreamException) {
			}
		}
	}

	public IMessageBody createMessageBody(final String supportedContentType) {
		final byte[] data = this.stream.toByteArray();
		return new ByteArrayMessageBody(supportedContentType, data);
	}
}
