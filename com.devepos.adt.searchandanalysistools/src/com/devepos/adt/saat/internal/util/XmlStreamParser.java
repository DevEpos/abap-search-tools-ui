package com.devepos.adt.saat.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Parser for XML stream
 *
 * @author stockbal
 */
public class XmlStreamParser {

	private final XMLStreamReader xsr;
	private final List<String> elementsWithText;

	public XmlStreamParser(final XMLStreamReader xsr, final String... elementsWithText) {
		this.xsr = xsr;
		this.elementsWithText = new ArrayList<>();
		if (elementsWithText != null) {
			this.elementsWithText.addAll(Arrays.asList(elementsWithText));
		}
	}

	/**
	 * Parses the XML file from the given reader and returns the root element
	 *
	 * @param  xsr the reader to parse the XML
	 * @return     the root element
	 */
	public IXmlElement parseXML() {
		return parse();
	}

	/**
	 * Parse current element
	 *
	 * @return
	 */
	private Element parse() {
		Element element = null;

		while (this.xsr.getEventType() != XMLStreamConstants.END_DOCUMENT) {
			try {
				if (this.xsr.getEventType() == XMLStreamConstants.START_ELEMENT) {
					if (element != null) {
						element.getChildren().add(parse());
						this.xsr.next();
					} else {
						element = new Element();
						final String elementName = this.xsr.getLocalName();
						element.setName(elementName);
						fillAttributes(element);
						if (this.elementsWithText.contains(elementName)) {
							try {
								element.setText(this.xsr.getElementText());
							} catch (final XMLStreamException e) {
							}
						}
						fillNamespaces(element);
						if (this.xsr.getEventType() == XMLStreamConstants.END_ELEMENT) {
							return element;
						}
						this.xsr.next();
					}
				} else if (this.xsr.getEventType() == XMLStreamConstants.END_ELEMENT) {
					return element;
				} else {
					this.xsr.next();
				}

			} catch (final XMLStreamException | NoSuchElementException e) {
				e.printStackTrace();
				if (this.xsr != null) {
					try {
						this.xsr.close();
					} catch (final XMLStreamException e1) {
					}
				}
			}
		}
		return element;
	}

	private void fillNamespaces(final IXmlElement element) {
		element.setNamespace(this.xsr.getNamespaceURI());
		element.setPrefix(this.xsr.getPrefix());
		for (int i = 0; i < this.xsr.getNamespaceCount(); i++) {
			final String prefix = this.xsr.getNamespacePrefix(i);
			if (prefix != null) {
				final String namespaceURI = this.xsr.getNamespaceURI(i);
				element.getNamespaces().put(prefix, namespaceURI);
			}

		}
	}

	/**
	 * Fill attributes at this element
	 *
	 * @param element
	 */
	private void fillAttributes(final IXmlElement element) {
		for (int i = 0; i < this.xsr.getAttributeCount(); i++) {
			final Attribute attribute = new Attribute();
			attribute.setNamespace(this.xsr.getAttributeNamespace(i));
			attribute.setName(this.xsr.getAttributeLocalName(i));
			attribute.setValue(this.xsr.getAttributeValue(i));
			element.getAttributes().add(attribute);
		}
	}

	private static class Element implements IXmlElement {
		private String text;
		private String name;
		private String namespacePrefix;
		private String namespaceURI;
		private Map<String, String> namespaces;
		private List<IXmlAttribute> attributes;
		private List<IXmlElement> children;

		@Override
		public List<IXmlElement> getChildren() {
			if (this.children == null) {
				this.children = new ArrayList<>();
			}
			return this.children;
		}

		@Override
		public IXmlElement getFirstChild() {
			return getChildren().stream().findFirst().orElse(null);
		}

		@Override
		public boolean hasChildren() {
			return !getChildren().isEmpty();
		}

		@Override
		public boolean hasChild(final String tagName) {
			if (this.children == null || this.children.isEmpty()) {
				return false;
			}
			return this.children.stream().anyMatch(c -> c.getName().equals(tagName));
		}

		@Override
		public String getAttributeValue(final String attributeName) {
			return getAttributes().stream()
				.filter(a -> a.getName().equals(attributeName))
				.findFirst()
				.orElse(new Attribute())
				.getValue();
		}

		@Override
		public String getAttributeValue(final String namespacePrefix, final String attributeName) {
			return getAttributes().stream()
				.filter(a -> a.getName().equals(attributeName) && a.getNamespace().equals(namespacePrefix))
				.findFirst()
				.orElse(new Attribute())
				.getValue();
		}

		@Override
		public void setPrefix(final String prefix) {
			this.namespacePrefix = prefix;
		}

		@Override
		public void setNamespace(final String namespaceURI) {
			this.namespaceURI = namespaceURI;
		}

		@Override
		public List<IXmlAttribute> getAttributes() {
			if (this.attributes == null) {
				this.attributes = new ArrayList<>();
			}
			return this.attributes;
		}

		@Override
		public boolean hasAttributes() {
			return !getAttributes().isEmpty();
		}

		@Override
		public Map<String, String> getNamespaces() {
			if (this.namespaces == null) {
				this.namespaces = new HashMap<>();
			}
			return this.namespaces;
		}

		@Override
		public String getPrefix() {
			return this.namespacePrefix;
		}

		@Override
		public String getNamespaceURI() {
			return this.namespaceURI;
		}

		/**
		 * @return the text
		 */
		@Override
		public String getText() {
			return this.text;
		}

		/**
		 * @param text the text to set
		 */
		@Override
		public void setText(final String text) {
			this.text = text;
		}

		/**
		 * @return the name
		 */
		@Override
		public String getName() {
			return this.name;
		}

		/**
		 * @param name the name to set
		 */
		@Override
		public void setName(final String name) {
			this.name = name;
		}

	}

	private static class Attribute implements IXmlAttribute {
		private String name;
		private String value;
		private String namespace;

		/**
		 * @return the name
		 */
		@Override
		public String getName() {
			return this.name;
		}

		/**
		 * @param name the name to set
		 */
		@Override
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * @return the value
		 */
		@Override
		public String getValue() {
			return this.value;
		}

		/**
		 * @param value the value to set
		 */
		@Override
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the namespace
		 */
		@Override
		public String getNamespace() {
			return this.namespace;
		}

		/**
		 * @param namespace the namespace to set
		 */
		@Override
		public void setNamespace(final String namespace) {
			this.namespace = namespace;
		}

	}
}
