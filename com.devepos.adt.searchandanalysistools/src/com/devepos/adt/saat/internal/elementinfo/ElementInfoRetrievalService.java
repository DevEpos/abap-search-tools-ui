package com.devepos.adt.saat.internal.elementinfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.devepos.adt.saat.IAdtObjectTypeConstants;
import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.analytics.AnalysisForOfficeUriDiscovery;
import com.devepos.adt.saat.internal.preferences.IPreferences;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.internal.util.StringUtil;
import com.sap.adt.cds.ddl.internal.ddlsources.elementinfo.DdlElementInfoService;
import com.sap.adt.cds.ddl.internal.ddlsources.service.DdlDdicRepositoryAccessFactory;
import com.sap.adt.cds.ddl.internal.ddlsources.service.IDdlDdicRepositoryAccess;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;
import com.sap.adt.ddic.internal.elementinfo.SbdElementInfoService;
import com.sap.adt.tools.abapsource.internal.sources.codeelementinformation.ICodeElement;
import com.sap.adt.tools.abapsource.internal.sources.codeelementinformation.ICodeElement.ICodeElementProperty;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.cds.ddl.parser.ISemanticCodeCompletionRepositoryAccess.DdlCompletionScope;

/**
 * Service implementation for element information retrieval
 *
 * @author stockbal
 */
class ElementInfoRetrievalService implements IElementInfoRetrievalService {
	private static final String KEY_PROP = "ddicIsKey"; //$NON-NLS-1$
	private static final String DATA_TYPE_PROP = "ddicDataType"; //$NON-NLS-1$
	private static final String CLIENT_DATATYPE = "clnt"; //$NON-NLS-1$

	public ElementInfoRetrievalService() {
	}

	@Override
	public List<IElementInfo> getElementColumnInformation(final String destinationId, final String objectName,
		final ObjectType type) {

		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
			return null;
		}
		if (type == ObjectType.CDS_VIEW) {
			final DdlElementInfoService infoService = new DdlElementInfoService();
			final ICodeElement element = infoService.getElementInfo(projectProvider.getProject(), false,
				Arrays.asList(objectName), null);
			if (element != null) {
				return createCdsColumnInformation(element);
			}

		} else if (type == ObjectType.TABLE || type == ObjectType.VIEW) {
			final SbdElementInfoService ddicService = new SbdElementInfoService(destinationId);
			final ICodeElement element = ddicService.getElementInfo(objectName,
				type == ObjectType.VIEW ? IAdtObjectTypeConstants.VIEW_SIMPLE_TYPE : IAdtObjectTypeConstants.TABLE_SIMPLE_TYPE,
				null, null);
			if (element != null) {
				return createTableColumnInformation(element);
			}
		}
		return null;
	}

	@Override
	public IAdtObjectReference getColumnUri(final String destinationId, final String objectName, final String column) {
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
			return null;
		}
		final IDdlDdicRepositoryAccess ddicAccess = DdlDdicRepositoryAccessFactory.createDdicRepositoryAccess();
		// normally this call should find exactly one column as a concrete column name
		// will be supplied
		final List<IAdtObjectReference> cols = ddicAccess.getElementProposalsExactMatch(projectProvider.getProject(), null,
			Arrays.asList(String.format("%s.%s", objectName, column)), DdlCompletionScope.COLUMNS);
		if (cols != null && !cols.isEmpty()) {
			return cols.get(0);
		}
		return null;
	}

	@Override
	public IAdtObjectReferenceElementInfo retrieveElementInformation(final String destinationId,
		final IAdtObjectReference objectReference) {
		// TODO: throw proper exception
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
			return null;
		}

		IAdtObjectReferenceElementInfo elementInfo = null;

		final String objectName = objectReference.getName();
		final Map<String, Object> paramsMap = new HashMap<>();
		final String type = objectReference.getType();
		ObjectType objectType = null;
		IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = null;
		final ElementInfoUriDiscovery uriDiscovery = new ElementInfoUriDiscovery(projectProvider.getDestinationId());
		final AnalysisForOfficeUriDiscovery anlyticsURIDiscovery = new AnalysisForOfficeUriDiscovery(
			projectProvider.getDestinationId());

		if (type.equals(ObjectType.CDS_VIEW.getAdtExecutionType())) {
			adtObjectHandler = new CdsViewElementInfoContentHandler(destinationId,
				uriDiscovery.getCDSSecondaryElementInfoTemplate() != null, anlyticsURIDiscovery.getLauncherTemplate() != null);
			paramsMap.put("showAssocName", //$NON-NLS-1$
				SearchAndAnalysisPlugin.getDefault().getPreferenceStore().getBoolean(IPreferences.SHOW_FULL_ASSOCIATION_NAME));
			objectType = ObjectType.CDS_VIEW;
		} else if (type.equals(ObjectType.TABLE.getAdtExecutionType())) {
			adtObjectHandler = new TableElementInfoContentHandler(destinationId);
			objectType = ObjectType.TABLE;
		} else if (type.equals(ObjectType.VIEW.getAdtExecutionType())) {
			adtObjectHandler = new ViewElementInfoContentHandler(destinationId);
			objectType = ObjectType.VIEW;
		}

		if (objectType != null || adtObjectHandler != null) {

			final URI resourceUri = uriDiscovery.createElementInfoResourceUri(objectName, objectType, paramsMap);

			final ISystemSession session = projectProvider.createStatelessSession();
			final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
				.createRestResource(resourceUri, session);
			restResource.addContentHandler(adtObjectHandler);

			try {
				elementInfo = restResource.get(null, AdtUtil.getHeaders(), IAdtObjectReferenceElementInfo.class);
			} catch (final ResourceException exc) {
				exc.printStackTrace();
			}
		}
		return elementInfo;
	}

	@Override
	public IElementInfoCollection retrieveCDSSecondaryElements(final String destinationId, final String cdsViewName) {
		// TODO: throw proper exception
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
			return null;
		}
		IElementInfoCollection secondaryElementInfo = null;

		final IContentHandler<IElementInfoCollection> handler = new CdsSecondaryElementInfoContentHandler(destinationId);
		final URI resourceUri = new ElementInfoUriDiscovery(projectProvider.getDestinationId())
			.createCDSSecondaryElementInfoResourceURI(cdsViewName);

		final ISystemSession session = projectProvider.createStatelessSession();
		final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
			.createRestResource(resourceUri, session);
		restResource.addContentHandler(handler);

		try {
			secondaryElementInfo = restResource.get(null, AdtUtil.getHeaders(), IElementInfoCollection.class);
		} catch (final ResourceException exc) {
			exc.printStackTrace();
		}
		return secondaryElementInfo;
	}

	@Override
	public IAdtObjectReferenceElementInfo retrieveBasicElementInformation(final String destinationId, final String objectName,
		final ObjectType objectType) {
		if (destinationId == null || objectType == null) {
			return null;
		}
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (projectProvider == null || !projectProvider.ensureLoggedOn()) {
			return null;
		}
		final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new BasicElementInfoContentHandler(
			destinationId);
		final ElementInfoUriDiscovery uriDiscovery = new ElementInfoUriDiscovery(projectProvider.getDestinationId());

		final Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("basicInfoOnly", "X");
		final URI resourceUri = uriDiscovery.createElementInfoResourceUri(objectName, objectType, paramsMap);

		final ISystemSession session = projectProvider.createStatelessSession();
		final IRestResource restResource = AdtRestResourceFactory.createRestResourceFactory()
			.createRestResource(resourceUri, session);
		restResource.addContentHandler(adtObjectHandler);

		IAdtObjectReferenceElementInfo elementInfo = null;
		try {
			elementInfo = restResource.get(null, AdtUtil.getHeaders(), IAdtObjectReferenceElementInfo.class);
		} catch (final ResourceException exc) {
			exc.printStackTrace();
		}
		return elementInfo;
	}

	/*
	 * Create list of element information from table column information
	 */
	private List<IElementInfo> createTableColumnInformation(final ICodeElement element) {
		final List<ICodeElement> children = element.getCodeElementChildren();
		if (children == null || children.isEmpty()) {
			return null;
		}
		final List<ICodeElement> filteredChildren = children.stream()
			.filter(ce -> ce.getType().equals(IAdtObjectTypeConstants.TABLE_FIELD_TYPE))
			.collect(Collectors.toList());
		if (filteredChildren.isEmpty()) {
			return null;
		}
		return createColumnInformationList(filteredChildren);
	}

	/*
	 * Create list of element information from CDS column information
	 */
	private List<IElementInfo> createCdsColumnInformation(final ICodeElement element) {
		final List<ICodeElement> children = element.getCodeElementChildren();
		if (children == null || children.isEmpty()) {
			return null;
		}
		final List<ICodeElement> filteredChildren = children.stream()
			.filter(ce -> ce.getType().equals(IAdtObjectTypeConstants.CDS_VIEW_FIELD_TYPE))
			.collect(Collectors.toList());
		if (filteredChildren.isEmpty()) {
			return null;
		}
		return createColumnInformationList(filteredChildren);
	}

	/*
	 * Create column information from the list of code element entries
	 */
	private List<IElementInfo> createColumnInformationList(final List<ICodeElement> filteredChildren) {
		final List<IElementInfo> fields = new ArrayList<>();
		for (final ICodeElement fieldElement : filteredChildren) {
			final ICodeElementProperty ddicTypeProp = fieldElement.getProperty(DATA_TYPE_PROP);
			if (ddicTypeProp != null && CLIENT_DATATYPE.equalsIgnoreCase(ddicTypeProp.getValue())) {
				continue;
			}
			final ICodeElementProperty isKeyProp = fieldElement.getProperty(KEY_PROP);
			String fieldImageId = IImages.COLUMN;
			if (isKeyProp != null && "true".equalsIgnoreCase(isKeyProp.getValue())) {
				fieldImageId = IImages.KEY_COLUMN;
			}
			final IElementInfo fieldElemInfo = new SimpleElementInfo(fieldElement.getName(), fieldElement.getName(), fieldImageId,
				StringUtil.unescapeHtmlChars(fieldElement.getDocumentationString()));
			fields.add(fieldElemInfo);
		}
		return fields;
	}

}
