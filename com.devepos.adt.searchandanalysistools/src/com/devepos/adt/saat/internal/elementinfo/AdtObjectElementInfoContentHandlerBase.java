package com.devepos.adt.saat.internal.elementinfo;

import java.nio.charset.Charset;

import com.devepos.adt.saat.internal.util.AdtObjectReferenceModelFactory;
import com.devepos.adt.saat.internal.util.AdtStaxContentHandlerUtility;
import com.devepos.adt.saat.internal.util.IXmlElement;
import com.devepos.adt.saat.internal.util.IXmlTags;
import com.sap.adt.communication.content.AdtMediaType;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content handler base for all {@link IAdtObjectReferenceElementInfo} based
 * Content Handler implementations
 *
 * @author stockbal
 */
public abstract class AdtObjectElementInfoContentHandlerBase implements IContentHandler<IAdtObjectReferenceElementInfo> {

	protected final AdtStaxContentHandlerUtility utility = new AdtStaxContentHandlerUtility();
	protected IAdtObjectReferenceElementInfo elementInfo = null;
	protected String destinationId;

	public AdtObjectElementInfoContentHandlerBase(final String destinationId) {
		this.destinationId = destinationId;
	}

	@Override
	public String getSupportedContentType() {
		return AdtMediaType.APPLICATION_XML;
	}

	@Override
	public Class<IAdtObjectReferenceElementInfo> getSupportedDataType() {
		return IAdtObjectReferenceElementInfo.class;
	}

	@Override
	public IMessageBody serialize(final IAdtObjectReferenceElementInfo arg0, final Charset arg1) {
		// no implementation needed for serialization
		return null;
	}

	protected void createElementInfo(final IXmlElement rootElement) {
		final String name = rootElement.getAttributeValue(IXmlTags.AT_NAME);
		final String rawName = rootElement.getAttributeValue(IXmlTags.AT_RAW_NAME);
		final String description = rootElement.getAttributeValue(IXmlTags.AT_DESCRIPTION);
		final String packageName = rootElement.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
		final String uri = rootElement.getAttributeValue(IXmlTags.AT_URI);
		final String type = rootElement.getAttributeValue(IXmlTags.AT_TYPE);
		this.elementInfo = new AdtObjectReferenceElementInfo(name, rawName, description);

		if (name != null && !name.isEmpty() && uri != null && !uri.isEmpty() && type != null && !type.isEmpty()) {
			final IAdtObjectReference adtObjectRef = AdtObjectReferenceModelFactory.createReference(this.destinationId, name, type,
				uri);
			adtObjectRef.setPackageName(packageName);
			this.elementInfo.setAdtObjectReference(adtObjectRef);
		}
	}

	/**
	 * Adds the given collection to element information
	 *
	 * @param collection the collection to be added
	 */
	protected void addCollection(final IElementInfoCollection collection) {
		if (this.elementInfo != null && collection != null) {
			this.elementInfo.getChildren().add(collection);
		}
	}
}