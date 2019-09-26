package com.devepos.adt.saat.internal.elementinfo;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.devepos.adt.saat.CdsSourceType;
import com.devepos.adt.saat.IDestinationProvider;
import com.devepos.adt.saat.internal.search.AdtObjectReferenceDeserializer;
import com.devepos.adt.saat.internal.util.AdtObjectReferenceFactory;
import com.devepos.adt.saat.internal.util.AdtStaxContentHandlerUtility;
import com.devepos.adt.saat.internal.util.IXmlElement;
import com.devepos.adt.saat.internal.util.IXmlTags;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content handler for the object search
 *
 * @author stockbal
 */
public class ObjectSearchContentHandler implements IContentHandler<IAdtObjectReferenceElementInfo[]> {
	private static final String EL_OBJECT_RESULTS = "objects";
	private static final String AT_OBJECT_NAME = "objectName";
	private static final String AT_RAW_OBJECT_NAME = "rawObjectName";
	private static final String AT_DESCRIPTION = "description";
	private static final String AT_SOURCE_TYPE = "sourceType";
	private final String destinationId;
	protected final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();

	public ObjectSearchContentHandler(final String destinationId) {
		this.destinationId = destinationId;
	}

	@Override
	public IAdtObjectReferenceElementInfo[] deserialize(final IMessageBody body,
		final Class<? extends IAdtObjectReferenceElementInfo[]> dataType) {
		final List<IAdtObjectReferenceElementInfo> result = new ArrayList<>();

		final IXmlElement rootElement = this.utility.parseXML(body, IXmlTags.EL_PROPERTY);

		if (rootElement != null) {
			if (rootElement.getName().equals(EL_OBJECT_RESULTS)) {
				deserializeObjectSearchResult(rootElement, result);
			} else if (rootElement.getName().equals(IXmlTags.EL_ELEMENT_INFOS)) {
				deserializeElementInfos(rootElement, result);
			}
		}

		return result.toArray(new IAdtObjectReferenceElementInfo[result.size()]);
	}

	@Override
	public String getSupportedContentType() {
		return AdtMediaType.APPLICATION_XML;
	}

	@Override
	public Class<IAdtObjectReferenceElementInfo[]> getSupportedDataType() {
		return IAdtObjectReferenceElementInfo[].class;
	}

	@Override
	public IMessageBody serialize(final IAdtObjectReferenceElementInfo[] arg0, final Charset arg1) {
		return null;
	}

	private void deserializeObjectSearchResult(final IXmlElement rootElement, final List<IAdtObjectReferenceElementInfo> result) {
		// each child of "objects" is "object"
		for (final IXmlElement objectElement : rootElement.getChildren()) {
			final String name = objectElement.getAttributeValue(AT_OBJECT_NAME);
			final String rawName = objectElement.getAttributeValue(AT_RAW_OBJECT_NAME);
			final String description = objectElement.getAttributeValue(AT_DESCRIPTION);
			final String sourceType = objectElement.getAttributeValue(AT_SOURCE_TYPE);

			final IAdtObjectReferenceElementInfo adtObjInfo = new AdtObjectReferenceElementInfo(name, rawName, description);

			// deserialize ADT Object references
			final IAdtObjectReference objectReference = AdtObjectReferenceDeserializer
				.deserializeFromElement(objectElement.getFirstChild());
			if (objectReference != null && objectReference instanceof IDestinationProvider) {
				((IDestinationProvider) objectReference).setDestinationId(this.destinationId);
			}
			adtObjInfo.setAdtObjectReference(objectReference);
			if (sourceType != null && !sourceType.isEmpty()) {
				final ExtendedAdtObjectInfo extendedInfo = new ExtendedAdtObjectInfo();
				extendedInfo.setSourceType(CdsSourceType.getFromId(sourceType));
				adtObjInfo.setAdditionalInfo(extendedInfo);
			}

			adtObjInfo.setElementInfoProvider(new ObjectSearchElementInfoProvider(this.destinationId, objectReference));
			result.add(adtObjInfo);
		}
	}

	private void deserializeElementInfos(final IXmlElement rootElement, final List<IAdtObjectReferenceElementInfo> result) {
		for (final IXmlElement elementInfoElement : rootElement.getChildren()) {
			final String name = elementInfoElement.getAttributeValue(IXmlTags.AT_NAME);
			final String rawName = elementInfoElement.getAttributeValue(IXmlTags.AT_RAW_NAME);
			final String description = elementInfoElement.getAttributeValue(IXmlTags.AT_DESCRIPTION);
			final String packageName = elementInfoElement.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
			final String uri = elementInfoElement.getAttributeValue(IXmlTags.AT_URI);
			final String type = elementInfoElement.getAttributeValue(IXmlTags.AT_TYPE);
			final IAdtObjectReferenceElementInfo elementInfo = new AdtObjectReferenceElementInfo(name, rawName, description);

			if (name != null && !name.isEmpty() && uri != null && !uri.isEmpty() && type != null && !type.isEmpty()) {
				final IAdtObjectReference adtObjectRef = AdtObjectReferenceFactory.createReference(this.destinationId, name, type,
					uri);
				adtObjectRef.setPackageName(packageName);
				elementInfo.setAdtObjectReference(adtObjectRef);
				elementInfo.setElementInfoProvider(new ObjectSearchElementInfoProvider(this.destinationId, adtObjectRef));
			}
			addAdditionalResultInformation(elementInfoElement, elementInfo);
			result.add(elementInfo);
		}
	}

	private void addAdditionalResultInformation(final IXmlElement elementInfoElement,
		final IAdtObjectReferenceElementInfo elementInfo) {
		if (!elementInfoElement.hasChildren()) {
			return;
		}
		final IXmlElement propertiesEl = elementInfoElement.getChildren()
			.stream()
			.filter(child -> child.getName().equals(IXmlTags.EL_PROPERTIES))
			.findFirst()
			.orElse(null);
		if (propertiesEl == null || propertiesEl.getChildren() == null) {
			return;
		}
		final ExtendedAdtObjectInfo extendedInfo = new ExtendedAdtObjectInfo();
		for (final IXmlElement propertyEl : propertiesEl.getChildren()) {
			final String key = propertyEl.getAttributeValue(IXmlTags.AT_KEY);
			final String value = propertyEl.getText();
			switch (key) {
			case "API_STATE":
				if (value != null && !value.isEmpty()) {
					extendedInfo.setReleased(true);

				}
			case "SOURCE_TYPE":
				if (value != null && !value.isEmpty()) {
					extendedInfo.setSourceType(CdsSourceType.getFromId(value));
				}
			}
		}
		elementInfo.setAdditionalInfo(extendedInfo);
	}

}
