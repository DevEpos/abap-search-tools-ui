package com.devepos.adt.saat.internal.cdsanalysis;

import java.util.List;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.adtobject.AdtObjectReferenceModelFactory;
import com.devepos.adt.base.elementinfo.AdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.ElementInfoCollection;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoCollection;
import com.devepos.adt.base.util.IXmlElement;
import com.devepos.adt.base.util.IXmlTags;
import com.devepos.adt.saat.internal.CdsSourceType;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.elementinfo.AdtObjectElementInfoContentHandlerBase;
import com.devepos.adt.saat.internal.elementinfo.ExtendedAdtObjectInfo;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Content Handler for CDS Top Down Analysis
 *
 * @author stockbal
 */
public class CdsTopDownAnalysisContentHandler extends AdtObjectElementInfoContentHandlerBase {
  private ICdsTopDownSettings settings;

  public CdsTopDownAnalysisContentHandler(final String destinationId,
      ICdsTopDownSettings settings) {
    super(destinationId);
    this.settings = settings;
  }

  @Override
  public IAdtObjectReferenceElementInfo deserialize(final IMessageBody messageBody,
      final Class<? extends IAdtObjectReferenceElementInfo> clazz) {

    try {
      deserializeNodeInfo(utility.parseXML(messageBody, IXmlTags.EL_PROPERTY), null, true);
      return elementInfo;
    } catch (final Exception e) {
    }
    return null;
  }

  protected void deserializeNodeInfo(final IXmlElement element, IElementInfo parent,
      final boolean isRoot) {
    IElementInfo elementInfo = null;
    final String name = element.getAttributeValue(IXmlTags.AT_NAME);
    final String rawName = element.getAttributeValue(IXmlTags.AT_RAW_NAME);
    final String description = element.getAttributeValue(IXmlTags.AT_DESCRIPTION);
    final String packageName = element.getAttributeValue(IXmlTags.AT_PACKAGE_NAME);
    final String uri = element.getAttributeValue(IXmlTags.AT_URI);
    final String type = element.getAttributeValue(IXmlTags.AT_TYPE);

    if (uri != null && !uri.isEmpty() && type != null && !type.isEmpty()) {
      final IAdtObjectReferenceElementInfo adtObjRefInfo = new AdtObjectReferenceElementInfo(name,
          rawName, description);
      final IAdtObjectReference adtObjectRef = AdtObjectReferenceModelFactory.createReference(
          destinationId, name, type, uri);
      adtObjectRef.setPackageName(packageName);
      adtObjRefInfo.setAdtObjectReference(adtObjectRef);
      final ObjectType objectType = ObjectType.getFromAdtType(adtObjectRef.getType());
      if (objectType != null && objectType != ObjectType.DATA_DEFINITION) {
        adtObjRefInfo.setLazyLoadingSupport(false);
      } else {
        adtObjRefInfo.setElementInfoProvider(new CdsTopDownElementInfoProvider(destinationId, name,
            settings));
      }
      elementInfo = adtObjRefInfo;
    } else {
      elementInfo = createRelationalNode(element, name);
    }

    if (elementInfo == null) {
      return;
    }

    if (isRoot) {
      this.elementInfo = (IAdtObjectReferenceElementInfo) elementInfo;
      parent = elementInfo;
    } else if (parent instanceof IElementInfoCollection) {
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
      case "ASSOCIATIONS": //$NON-NLS-1$
        rawName = Messages.CdsAnalysis_NodeNameAssociations;
        imageId = IImages.ASSOCIATION;
        break;
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
      /*
       * In case a separate folder for the select part of the top down analysis makes
       * more sense during active "Show associations" option
       */
//			case "FROM":
//				rawName = "Select From";
//				imageId = IImages.DATA_SOURCE;
      }
      return new ElementInfoCollection(name, rawName, SearchAndAnalysisPlugin.getDefault()
          .getImage(imageId), null);
    }
    return null;
  }

  private void setSqlRelationalInfo(final List<IXmlElement> children,
      final IElementInfo elementInfo) {
    if (children == null) {
      return;
    }
    final SqlRelation relationInfo = new SqlRelation();

    String associationName = null;
    boolean isAssociation = false;

    for (final IXmlElement propertyEl : children) {
      final String attribute = propertyEl.getAttributeValue(IXmlTags.AT_KEY);
      if ("TYPE".equals(attribute)) { //$NON-NLS-1$
        relationInfo.type = propertyEl.getText();
      } else if ("RELATION".equals(attribute)) { //$NON-NLS-1$
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
        case "ASSOCIATION": //$NON-NLS-1$
          relationInfo.relation = Messages.CdsAnalysis_SqlRelationAssociation;
          isAssociation = true;
          break;
        }
      } else if ("ALIAS".equals(attribute)) { //$NON-NLS-1$
        relationInfo.aliasName = propertyEl.getText();
      } else if ("API_STATE".equals(attribute)) { //$NON-NLS-1$
        relationInfo.setApiState(propertyEl.getText());
      } else if ("SOURCE_TYPE".equals(attribute)) { //$NON-NLS-1$
        relationInfo.setSourceType(CdsSourceType.getFromId(propertyEl.getText()));
      } else if ("ASSOCIATION_NAME".equals(attribute)) { //$NON-NLS-1$
        associationName = propertyEl.getText();
      }
    }
    if (isAssociation && associationName != null && !associationName.isEmpty()) {
      elementInfo.setDisplayName(String.format("%s (%s)", associationName, elementInfo
          .getDisplayName()));
    }

    if (relationInfo != null && relationInfo.type != null) {
      elementInfo.setAdditionalInfo(relationInfo);
    }
  }

  private void setAdditionalInfo(final IXmlElement element, final IElementInfo elementInfo) {
    if (!element.hasChild(IXmlTags.EL_PROPERTIES)) {
      return;
    }
    for (final IXmlElement propertiesEl : element.getChildren()) {
      if (IXmlTags.EL_PROPERTIES.equals(propertiesEl.getName())) {
        setSqlRelationalInfo(propertiesEl.getChildren(), elementInfo);
      }
    }
  }

  private static final class SqlRelation extends ExtendedAdtObjectInfo implements ISqlRelationInfo {

    public String relation;
    public String type;
    public String aliasName;

    @Override
    public String getRelation() {
      return relation;
    }

    @Override
    public String getType() {
      return type;
    }

    @Override
    public String getAliasName() {
      return aliasName;
    }

  }
}
