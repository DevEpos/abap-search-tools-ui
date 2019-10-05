package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.List;

import com.devepos.adt.saat.CdsSourceType;
import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.internal.elementinfo.AdtObjectElementInfoContentHandlerBase;
import com.devepos.adt.saat.internal.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoCollection;
import com.devepos.adt.saat.internal.elementinfo.ExtendedAdtObjectInfo;
import com.devepos.adt.saat.internal.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfo;
import com.devepos.adt.saat.internal.elementinfo.IElementInfoCollection;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.AdtObjectReferenceFactory;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.internal.util.IXmlElement;
import com.devepos.adt.saat.internal.util.IXmlTags;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content handler for deserializing the Top-down or dependency usages
 * information for a CDS view
 *
 * @author stockbal
 */
public class CdsAnalysisContentHandler extends AdtObjectElementInfoContentHandlerBase {
	private final boolean usageAnalysis;

	public CdsAnalysisContentHandler(final String destinationId, final boolean usageAnalysis) {
		super(destinationId);
		this.usageAnalysis = usageAnalysis;
	}

	@Override
	public IAdtObjectReferenceElementInfo deserialize(final IMessageBody messageBody,
		final Class<? extends IAdtObjectReferenceElementInfo> clazz) {

		try {
			deserializeNodeInfo(this.utility.parseXML(messageBody, IXmlTags.EL_PROPERTY), null, true);
			return this.elementInfo;
		} catch (final Exception e) {
		}
		return null;
	}

	protected void deserializeNodeInfo(final IXmlElement element, IElementInfo parent, final boolean isRoot) {
		IElementInfo elementInfo = null;
		final String name = element.getAttributeValue(IXmlTags.AT_NAME);
		final String rawName = element.getAttributeValue(IXmlTags.AT_RAW_NAME);
		final String description = element.getAttributeValue(IXmlTags.AT_DESCRIPTION);
		final String packageName = element.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
		final String uri = element.getAttributeValue(IXmlTags.AT_URI);
		final String type = element.getAttributeValue(IXmlTags.AT_TYPE);

		if (uri != null && !uri.isEmpty() && type != null && !type.isEmpty()) {
			final IAdtObjectReferenceElementInfo adtObjRefInfo = new AdtObjectReferenceElementInfo(name, rawName, description);
			final IAdtObjectReference adtObjectRef = AdtObjectReferenceFactory.createReference(this.destinationId, name, type,
				uri);
			adtObjectRef.setPackageName(packageName);
			adtObjRefInfo.setAdtObjectReference(adtObjectRef);
			if (this.usageAnalysis) {
				adtObjRefInfo.setLazyLoadingSupport(false);
			} else {
				final ObjectType objectType = ObjectType.getFromAdtType(adtObjectRef.getType());
				if (objectType != null && objectType != ObjectType.CDS_VIEW) {
					adtObjRefInfo.setLazyLoadingSupport(false);
				} else {
					adtObjRefInfo.setElementInfoProvider(new CdsTopDownElementInfoProvider(this.destinationId, name));
				}
			}
			elementInfo = adtObjRefInfo;
		} else {
			if (!this.usageAnalysis) {
				elementInfo = createRelationalNode(element, name);
			}
		}

		if (elementInfo == null) {
			return;
		}

		if (isRoot) {
			this.elementInfo = (IAdtObjectReferenceElementInfo) elementInfo;
			parent = elementInfo;
		} else if (parent != null && parent instanceof IElementInfoCollection) {
			setAdditionalInfo(element, elementInfo);
		}

		if (!isRoot && parent != null && elementInfo != null) {
			((IElementInfoCollection) parent).getChildren().add(elementInfo);
		}

		parent = elementInfo;

		for (final IXmlElement child : element.getChildren()) {
			switch (child.getName()) {
			case IXmlTags.EL_ELEMENT_INFO:
				deserializeNodeInfo(child, parent, false);
				break;
			}
		}

	}

	private IElementInfo createRelationalNode(final IXmlElement element, final String name) {
		if (element.hasChild(IXmlTags.EL_ELEMENT_INFO)) {
			String imageId = null;
			String rawName = null;
			switch (name) {
			case "SELECT": //$NON-NLS-1$
				rawName = Messages.CdsAnalysis_NodeNameSelect;
				imageId = IImages.SELECT_PART;
				break;
			case "UNION": //$NON-NLS-1$
				rawName = Messages.CdsAnalysis_NodeNameUnionSelect;
				imageId = IImages.UNION;
				break;
			case "UNION_ALL": //$NON-NLS-1$
				rawName = Messages.CdsAnalysis_NodeNameUnionSelectAll;
				imageId = IImages.UNION;
				break;
			case "RESULT": //$NON-NLS-1$
				rawName = Messages.CdsAnalysis_NodeNameResult;
				imageId = IImages.JOIN_RESULT_SOURCE;
				break;
			}
			return new ElementInfoCollection(name, rawName, imageId, null);
		}
		return null;
	}

	private void setAdditionalInfo(final IXmlElement element, final IElementInfo elementInfo) {
		if (!element.hasChild(IXmlTags.EL_PROPERTIES)) {
			return;
		}
		for (final IXmlElement propertiesEl : element.getChildren()) {
			if (propertiesEl.getName().equals(IXmlTags.EL_PROPERTIES)) {
				if (this.usageAnalysis) {
					setDatasourceUsageInfo(propertiesEl.getChildren(), elementInfo);
				} else {
					setSqlRelationalInfo(propertiesEl.getChildren(), elementInfo);
				}
			}
		}
	}

	private void setDatasourceUsageInfo(final List<IXmlElement> children, final IElementInfo elementInfo) {
		if (children == null) {
			return;
		}
		final CdsEntityUsageInfo usageInfo = new CdsEntityUsageInfo();

		for (final IXmlElement propertyEl : children) {
			final String attribute = propertyEl.getAttributeValue(IXmlTags.AT_KEY);

			try {
				switch (attribute) {
				case "OCCURRENCE": //$NON-NLS-1$
					usageInfo.occurrence = Integer.parseInt(propertyEl.getText().trim());
					break;
				case "USED_ENTITIES_COUNT": //$NON-NLS-1$
					usageInfo.usedEntitiesCount = Integer.parseInt(propertyEl.getText().trim());
					break;
				case "USED_JOIN_COUNT": //$NON-NLS-1$
					usageInfo.usedJoinCount = Integer.parseInt(propertyEl.getText().trim());
					break;
				case "USED_UNION_COUNT": //$NON-NLS-1$
					usageInfo.usedUnionCount = Integer.parseInt(propertyEl.getText().trim());
					break;
				case "API_STATE": //$NON-NLS-1$
					usageInfo.setApiState(propertyEl.getText());
					break;
				case "SOURCE_TYPE": //$NON-NLS-1$
					usageInfo.setSourceType(CdsSourceType.getFromId(propertyEl.getText()));
					break;
				}
			} catch (final NumberFormatException exc) {
			}
		}
		elementInfo.setAdditionalInfo(usageInfo);
	}

	private void setSqlRelationalInfo(final List<IXmlElement> children, final IElementInfo elementInfo) {
		if (children == null) {
			return;
		}
		final SqlRelation relationInfo = new SqlRelation();

		for (final IXmlElement propertyEl : children) {
			final String attribute = propertyEl.getAttributeValue(IXmlTags.AT_KEY);
			if (attribute.equals("TYPE")) { //$NON-NLS-1$
				relationInfo.type = propertyEl.getText();
			} else if (attribute.equals("RELATION")) { //$NON-NLS-1$
				switch (propertyEl.getText()) {
				case "LEFT_OUTER_JOIN": //$NON-NLS-1$
					relationInfo.relation = Messages.CdsAnalysis_SqlRelationLeftOuterJoin;
					break;
				case "RIGHT_OUTER_JOIN": //$NON-NLS-1$
					relationInfo.relation = Messages.CdsAnalysis_SqlRelationRightOuterJoin;
					break;
				case "FULL_OUTER_JOIN": //$NON-NLS-1$
					relationInfo.relation = Messages.CdsAnalysis_SqlRelationFullOuterJoin;
					break;
				case "INNER_JOIN": //$NON-NLS-1$
					relationInfo.relation = Messages.CdsAnalysis_SqlRelationInnerJoin;
					break;
				case "CROSS_JOIN": //$NON-NLS-1$
					relationInfo.relation = Messages.CdsAnalysis_SqlRelationCrossJoin;
					break;
				case "FROM": //$NON-NLS-1$
					relationInfo.relation = Messages.CdsAnalysis_SqlRelationFrom;
					break;
				}
			} else if (attribute.equals("ALIAS")) { //$NON-NLS-1$
				relationInfo.aliasName = propertyEl.getText();
			} else if (attribute.equals("API_STATE")) { //$NON-NLS-1$
				relationInfo.setApiState(propertyEl.getText());
			} else if (attribute.equals("SOURCE_TYPE")) { //$NON-NLS-1$
				relationInfo.setSourceType(CdsSourceType.getFromId(propertyEl.getText()));
			}
		}

		if (relationInfo != null && relationInfo.type != null) {
			elementInfo.setAdditionalInfo(relationInfo);
		}
	}

	private final class SqlRelation extends ExtendedAdtObjectInfo implements ISqlRelationInfo {

		public String relation;
		public String type;
		public String aliasName;

		@Override
		public String getRelation() {
			return this.relation;
		}

		@Override
		public String getType() {
			return this.type;
		}

		@Override
		public String getAliasName() {
			return this.aliasName;
		}

	}

	private final class CdsEntityUsageInfo extends ExtendedAdtObjectInfo implements ICdsEntityUsageInfo {
		public int occurrence;
		public int usedEntitiesCount;
		public int usedJoinCount;
		public int usedUnionCount;

		@Override
		public int getOccurrence() {
			return this.occurrence;
		}

		@Override
		public int getUsedEntitiesCount() {
			return this.usedEntitiesCount;
		}

		@Override
		public int getUsedJoinCount() {
			return this.usedJoinCount;
		}

		@Override
		public int getUsedUnionCount() {
			return this.usedUnionCount;
		}

	}
}
