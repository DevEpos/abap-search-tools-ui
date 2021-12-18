package com.devepos.adt.saat.internal.search;

import java.util.List;

import org.eclipse.osgi.util.NLS;

import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfo;
import com.devepos.adt.base.elementinfo.IElementInfoProvider;
import com.devepos.adt.saat.internal.elementinfo.ElementInfoRetrievalServiceFactory;
import com.devepos.adt.saat.internal.messages.Messages;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Element info provider to load the child elements of an element in the result
 * tree of the Object Search
 *
 * @author stockbal
 */
public class ObjectSearchElementInfoProvider implements IElementInfoProvider {
  private final IAdtObjectReference adtObjectRef;
  private final String destinationId;

  public ObjectSearchElementInfoProvider(final String destinationId,
      final IAdtObjectReference adtObjectRef) {
    this.adtObjectRef = adtObjectRef;
    this.destinationId = destinationId;
  }

  @Override
  public List<IElementInfo> getElements() {
    final IAdtObjectReferenceElementInfo elementInfo = ElementInfoRetrievalServiceFactory
        .createService()
        .retrieveElementInformation(destinationId, adtObjectRef);

    if (elementInfo != null) {
      return elementInfo.getChildren();
    }
    return null;
  }

  @Override
  public String getProviderDescription() {
    return NLS.bind(Messages.ElementInfoProvider_RetrievingElementInfoDescription_xmsg, adtObjectRef
        .getName());
  }

}
