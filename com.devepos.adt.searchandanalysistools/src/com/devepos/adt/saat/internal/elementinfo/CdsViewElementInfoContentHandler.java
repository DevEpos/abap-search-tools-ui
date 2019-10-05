package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.saat.CdsSourceType;
import com.devepos.adt.saat.IDestinationProvider;
import com.devepos.adt.saat.internal.analytics.OpenWithAnalysisForOfficeExecutable;
import com.devepos.adt.saat.internal.analytics.OpenWithQueryMonitorExecutable;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.AdtObjectReferenceDeserializer;
import com.devepos.adt.saat.internal.search.ObjectSearchElementInfoProvider;
import com.devepos.adt.saat.internal.util.AdtObjectReferenceFactory;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.internal.util.IXmlElement;
import com.devepos.adt.saat.internal.util.IXmlTags;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content Handler for deserializing the Element information of a CDS View
 *
 * @author stockbal
 */
public class CdsViewElementInfoContentHandler extends AdtObjectElementInfoContentHandlerBase {
	private final boolean createSecondaryElementsFolder;
	private final boolean hasAnalyticsService;
	private boolean isQuery;

	public CdsViewElementInfoContentHandler(final String destinationId, final boolean createSecondaryElementsFolder,
		final boolean hasAnalyticsService) {
		super(destinationId);
		this.createSecondaryElementsFolder = createSecondaryElementsFolder;
		this.hasAnalyticsService = hasAnalyticsService;
	}

	@Override
	public IAdtObjectReferenceElementInfo deserialize(final IMessageBody messageBody,
		final Class<? extends IAdtObjectReferenceElementInfo> clazz) {

		try {
			processCdsViewInfo(this.utility.parseXML(messageBody));
			return this.elementInfo;
		} catch (final Exception e) {
		}
		return null;
	}

	private void processCdsViewInfo(final IXmlElement rootElement) {
		createElementInfo(rootElement);

		for (final IXmlElement child : rootElement.getChildren()) {
			switch (child.getName()) {
			case IXmlTags.EL_PROPERTIES:
				addCollection(deserializeProperties(child));
				break;
			case IXmlTags.EL_CDS_AMDP_REFERENCE:
				deserializeAMDPReference(child);
				break;
			case IXmlTags.EL_CDS_ASSOCIATIONS:
				deserializeAssociations(child);
				break;
			case IXmlTags.EL_CDS_SELECT_FROM:
				deserializeSelectFromEntities(child);
				break;
			}
		}

		/*
		 * add action/navigation information if this CDS view is an analytical query
		 * view
		 */
		if (this.isQuery && this.hasAnalyticsService) {
			final ElementInfoCollection openInTargets = new ElementInfoCollection(
				Messages.ElementInformation_CustomNavigationTarget_xtit, IImages.EXTERNAL_TOOLS);
			openInTargets.getChildren()
				.add(new ActionElementInfo(Messages.ElementInformation_AnalysisForOfficeTarget_xtit, IImages.EXCEL_APPLICATION,
					new OpenWithAnalysisForOfficeExecutable(this.destinationId, this.elementInfo.getName())));
			openInTargets.getChildren()
				.add(new ActionElementInfo(Messages.ElementInformation_QueryMonitorTarget_xtit, IImages.ANALYTICAL_QUERY,
					new OpenWithQueryMonitorExecutable(this.destinationId, this.elementInfo.getName())));
			this.elementInfo.getChildren().add(openInTargets);
		}

		// add additional collection to lazy load additional information
		if (this.createSecondaryElementsFolder) {
			final ILazyLoadingElementInfo secondaryElementsFolder = new LazyLoadingElementInfo(
				Messages.ElementInformation_SecondaryObjInfo_xtit, IImages.VIRTUAL_FOLDER,
				new CdsSecondaryObjectsProvider(this.destinationId, this.elementInfo.getName()));
			secondaryElementsFolder.setContentRefreshMode(LazyLoadingRefreshMode.ROOT_AND_NON_LAZY_CHILDREN);
			this.elementInfo.getChildren().add(secondaryElementsFolder);
		}

	}

	@Override
	protected void createElementInfo(final IXmlElement rootElement) {
		super.createElementInfo(rootElement);
		// check if this CDS view has the type query
		final String isQueryProp = rootElement.getAttributeValue(IXmlTags.AT_CDS_IS_QUERY);
		if (isQueryProp != null && !isQueryProp.isEmpty()) {
			this.isQuery = Boolean.parseBoolean(isQueryProp);
		}
	}

	private IElementInfoCollection deserializeProperties(final IXmlElement propertiesEl) {
		final IElementInfoCollection propertiesCollection = ElementInfoXMLExtractor.deserializeProperties(propertiesEl);
		final String apiState = propertiesEl.getAttributeValue(IXmlTags.AT_CDS_API_STATE);
		if (apiState != null && !apiState.isEmpty()) {

			propertiesCollection.getChildren()
				.add(new SimpleElementInfo(Messages.ElementInformation_APIState_xtit, Messages.ElementInformation_APIState_xtit,
					IImages.API_PARAM, apiState));
		}
		return propertiesCollection;
	}

	private void deserializeAMDPReference(final IXmlElement child) {
		final IAdtObjectReference objectRef = AdtObjectReferenceDeserializer.deserializeFromElement(child.getFirstChild());
		if (objectRef != null) {
			if (objectRef instanceof IDestinationProvider) {
				((IDestinationProvider) objectRef).setDestinationId(this.destinationId);
			}
			final IElementInfoCollection classesColl = new ElementInfoCollection(
				Messages.ElementInformation_ClassesCollection_xtit, IImages.TYPE_GROUP);
			final IAdtObjectReferenceElementInfo objectRefInfo = new AdtObjectReferenceElementInfo(objectRef.getName(),
				objectRef.getName(), objectRef.getDescription());
			objectRefInfo.setAdtObjectReference(objectRef);
			classesColl.getChildren().add(objectRefInfo);

			addCollection(classesColl);
		}
	}

	private void deserializeSelectFromEntities(final IXmlElement selectFromEl) {
		final IElementInfoCollection selectFromCollection = new ElementInfoCollection(
			Messages.ElementInformation_SelectFromCollection_xtit, IImages.SELECT_FROM_PARAM);

		for (final IXmlElement child : selectFromEl.getChildren()) {
			final IAdtObjectReferenceElementInfo selectFrom = ElementInfoXMLExtractor.deserializeAdtObjectInfo(this.destinationId,
				child);

			if (selectFrom != null) {
				addExtendedInfo(child, selectFrom);
				selectFrom.setElementInfoProvider(
					new ObjectSearchElementInfoProvider(this.destinationId, selectFrom.getAdtObjectReference()));
				selectFromCollection.getChildren().add(selectFrom);
			}
		}
		addCollection(selectFromCollection);
	}

	private void deserializeAssociations(final IXmlElement associationsEl) {
		final IElementInfoCollection associationColl = new ElementInfoCollection(
			Messages.ElementInformation_AssociationsCollection_xtit, IImages.ASSOCIATION);
		for (final IXmlElement child : associationsEl.getChildren()) {
			final IAdtObjectReferenceElementInfo association = deserializeAssociation(child);
			if (association != null) {
				associationColl.getChildren().add(association);
			}
		}
		addCollection(associationColl);

	}

	private IAdtObjectReferenceElementInfo deserializeAssociation(final IXmlElement associationEl) {
		IAdtObjectReferenceElementInfo adtObjRefElInfo = null;

		final String assocName = associationEl.getAttributeValue(IXmlTags.AT_NAME);
		final String assocRawName = associationEl.getAttributeValue(IXmlTags.AT_RAW_NAME);

		final IXmlElement adtObjectInfo = associationEl.getFirstChild();
		if (adtObjectInfo != null) {
			final String description = adtObjectInfo.getAttributeValue(IXmlTags.AT_DESCRIPTION);
			final String name = adtObjectInfo.getAttributeValue(IXmlTags.AT_NAME);
			final String rawName = adtObjectInfo.getAttributeValue(IXmlTags.AT_RAW_NAME);
			final String uri = adtObjectInfo.getAttributeValue(IXmlTags.AT_URI);
			final String adtType = adtObjectInfo.getAttributeValue(IXmlTags.AT_ADT_TYPE);
			final String packageName = adtObjectInfo.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);

			if (assocName != null && !assocName.isEmpty()) {
				final String elementRawName = String.format("%s (%s)", assocRawName, rawName); //$NON-NLS-1$
				adtObjRefElInfo = new AdtObjectReferenceElementInfo(assocName, elementRawName, description);
			} else {
				adtObjRefElInfo = new AdtObjectReferenceElementInfo(name, rawName, description);
			}
			addExtendedInfo(adtObjectInfo, adtObjRefElInfo);
			final IAdtObjectReference adtObjectRef = AdtObjectReferenceFactory.createReference(this.destinationId, name, adtType,
				uri);
			adtObjectRef.setPackageName(packageName);
			adtObjRefElInfo.setAdtObjectReference(adtObjectRef);
			adtObjRefElInfo.setElementInfoProvider(new ObjectSearchElementInfoProvider(this.destinationId, adtObjectRef));
		}
		return adtObjRefElInfo;
	}

	private void addExtendedInfo(final IXmlElement element, final IAdtObjectReferenceElementInfo objRefInfo) {
		final String sourceType = element.getAttributeValue(IXmlTags.AT_SOURCE_TYPE);
		final String apiState = element.getAttributeValue(IXmlTags.AT_CDS_API_STATE);
		final boolean sourceTypeFound = sourceType != null && !sourceType.isEmpty();
		if (sourceTypeFound || apiState != null) {
			final ExtendedAdtObjectInfo extendedInfo = new ExtendedAdtObjectInfo();
			if (sourceTypeFound) {
				extendedInfo.setSourceType(CdsSourceType.getFromId(sourceType));
			}
			extendedInfo.setApiState(apiState);
			objRefInfo.setAdditionalInfo(extendedInfo);
		}
	}

}
