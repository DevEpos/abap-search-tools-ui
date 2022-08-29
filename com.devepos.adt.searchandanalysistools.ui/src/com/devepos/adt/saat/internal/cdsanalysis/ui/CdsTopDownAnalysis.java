package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.base.ui.tree.LazyLoadingAdtObjectReferenceNode;
import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;
import com.devepos.adt.saat.internal.cdsanalysis.CdsTopDownElementInfoProvider;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Top Down analysis for a CDS view
 *
 * @author stockbal
 */
public class CdsTopDownAnalysis extends CdsAnalysis {

  private LazyLoadingAdtObjectReferenceNode cdsNode;
  private ICdsTopDownSettingsUi settings;

  public CdsTopDownAnalysis(final IAdtObjectReferenceElementInfo cdsObjInfo) {
    super(cdsObjInfo);
    final LazyLoadingAdtObjectReferenceNode node = new LazyLoadingAdtObjectReferenceNode(cdsObjInfo
        .getName(), cdsObjInfo.getDisplayName(), cdsObjInfo.getDescription(), cdsObjInfo
            .getAdtObjectReference(), null);
    cdsNode = node;
    settings = CdsAnalysisSettingsFactory.createCdsTopDownSettings();
  }

  public void createElementInfoProvider() {
    final IDestinationProvider destProvider = adtObjectInfo.getAdapter(IDestinationProvider.class);
    cdsNode.setElementInfoProvider(new CdsTopDownElementInfoProvider(destProvider != null
        ? destProvider.getDestinationId()
        : null, adtObjectInfo.getName(), settings));
    cdsNode.setNodeValue(adtObjectInfo.getAdditionalInfo());
  }

  @Override
  public Image getImage() {
    return SearchAndAnalysisPlugin.getDefault().getImage(IImages.TOP_DOWN);
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.TOP_DOWN);
  }

  @Override
  public String getLabelPrefix() {
    return Messages.CdsTopDownAnalysisView_ViewLabel_xfld;
  }

  @Override
  public Object getResult() {
    return new Object[] { cdsNode };
  }

  /**
   * @return the settings for top down analysis
   */
  public ICdsTopDownSettingsUi getSettings() {
    return settings;
  }

  @Override
  public CdsAnalysisType getType() {
    return CdsAnalysisType.TOP_DOWN;
  }

  @Override
  public void refreshAnalysis() {
    if (cdsNode == null) {
      return;
    }
    cdsNode.resetLoadedState();
  }

}
