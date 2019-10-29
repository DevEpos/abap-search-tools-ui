package com.devepos.adt.saat.internal.cdsanalysis;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.util.AbapProjectProviderAccessor;
import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAbapProjectProvider;
import com.sap.adt.communication.content.IContentHandler;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.ResourceException;
import com.sap.adt.communication.session.ISystemSession;

/**
 * Service for analysing CDS Views
 *
 * @author stockbal
 */
public class CdsAnalysisService implements ICdsAnalysisService {

	@Override
	public IAdtObjectReferenceElementInfo loadTopDownAnalysis(final String cdsView, final boolean loadAssociations,
		final String destinationId) {
		final Map<String, Object> parameters = new HashMap<>();
		if (loadAssociations) {
			parameters.put("withAssociations", "X"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return loadAnalysis(cdsView, destinationId, parameters, false);
	}

	@Override
	public IAdtObjectReferenceElementInfo loadUsedEntitiesAnalysis(final String cdsView, final String destinationId) {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("usageAnalysis", "X"); //$NON-NLS-1$ //$NON-NLS-2$
		return loadAnalysis(cdsView, destinationId, parameters, true);
	}

	@Override
	public IElementInfo loadTopDownFieldAnalysis(final String cdsView, final String field, final String destinationId) {
		if (destinationId == null || cdsView == null || field == null) {
			return null;
		}
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (!projectProvider.ensureLoggedOn()) {
			return null;
		}
		final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new FieldAnalysisContentHandler(destinationId,
			true);
		final FieldAnalysisUriDiscovery uriDiscovery = new FieldAnalysisUriDiscovery(projectProvider.getDestinationId());

		final URI resourceUri = uriDiscovery.createTopDownCdsAnalysisResourceUri(cdsView, field);

		return getFieldAnalysis(projectProvider, adtObjectHandler, resourceUri);
	}

	@Override
	public IElementInfo loadWhereUsedFieldAnalysis(final String objectName, final String field, final boolean searchCalcFields,
		final boolean searchDbViews, final String destinationId) {
		if (destinationId == null || objectName == null || field == null) {
			return null;
		}
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (!projectProvider.ensureLoggedOn()) {
			return null;
		}
		final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new FieldAnalysisContentHandler(destinationId,
			false, searchCalcFields, searchDbViews);
		final FieldAnalysisUriDiscovery uriDiscovery = new FieldAnalysisUriDiscovery(projectProvider.getDestinationId());

		final URI resourceUri = uriDiscovery.createWhereUsedAnalysisResourceUri(objectName, field, searchCalcFields,
			searchDbViews);

		return getFieldAnalysis(projectProvider, adtObjectHandler, resourceUri);
	}

	/*
	 * Retrieves the field analysis result with GET
	 */
	private IAdtObjectReferenceElementInfo getFieldAnalysis(final IAbapProjectProvider projectProvider,
		final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler, final URI resourceUri) {
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
	 * Load analysis information
	 */
	private IAdtObjectReferenceElementInfo loadAnalysis(final String cdsView, final String destinationId,
		final Map<String, Object> parameters, final boolean usageAnalysis) {
		if (destinationId == null || cdsView == null) {
			return null;
		}
		final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor.getProviderForDestination(destinationId);
		if (!projectProvider.ensureLoggedOn()) {
			return null;
		}
		final IContentHandler<IAdtObjectReferenceElementInfo> adtObjectHandler = new CdsAnalysisContentHandler(destinationId,
			usageAnalysis);
		final CdsAnalysisUriDiscovery uriDiscovery = new CdsAnalysisUriDiscovery(projectProvider.getDestinationId());

		final URI resourceUri = uriDiscovery.createCdsAnalysisResourceUri(cdsView, parameters);

		return getFieldAnalysis(projectProvider, adtObjectHandler, resourceUri);
	}
}
