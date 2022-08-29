package com.devepos.adt.saat.internal.cdsanalysis.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.base.destinations.IDestinationProvider;
import com.devepos.adt.base.elementinfo.IAdtObjectReferenceElementInfo;
import com.devepos.adt.saat.internal.cdsanalysis.CdsAnalysisType;

/**
 * Result of CDS Analysis
 *
 * @author stockbal
 */
public abstract class CdsAnalysis {

  protected final IAdtObjectReferenceElementInfo adtObjectInfo;
  private boolean isResultLoaded;

  /**
   * Creates new result instance for the given ADT object element information and
   * the analysis mode
   *
   * @param adtObjectInfo the ADT Object to be analyzed
   * @param mode          the analysis mode
   */
  public CdsAnalysis(final IAdtObjectReferenceElementInfo adtObjectInfo) {
    this.adtObjectInfo = adtObjectInfo;
  }

  public void addCdsAnalysisResultListener(final ICdsAnalysisResultListener l) {

  }

  /**
   * @return the adtObjectInfo of this analysis result
   */
  public IAdtObjectReferenceElementInfo getAdtObjectInfo() {
    return adtObjectInfo;
  }

  /**
   * Returns an image descriptor to proper the purpose of this analysis page
   * <p>
   * The default implementation returns <code>null</code>. <br>
   * Subclasses should override this method
   * </p>
   *
   * @return
   */
  public Image getImage() {
    return null;
  }

  /**
   * Returns an image to proper describe the purpose of this analysis page
   * <p>
   * The default implementation returns <code>null</code>. <br>
   * Subclasses should override this method
   * </p>
   *
   * @return
   */
  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  /**
   * Returns the label for this CDS Analysis page
   *
   * @return
   */
  public String getLabel() {
    String systemId = null;
    final IDestinationProvider destProvider = adtObjectInfo.getAdapter(IDestinationProvider.class);
    if (destProvider != null) {
      systemId = destProvider.getSystemId();
    }

    if (systemId == null) {
      return String.format("%s: '%s'", getLabelPrefix(), adtObjectInfo.getDisplayName()); //$NON-NLS-1$
    }
    return String.format("%s: '%s' [%s]", getLabelPrefix(), adtObjectInfo.getDisplayName(), //$NON-NLS-1$
        systemId);

  }

  /**
   * Returns the current Input for
   *
   * @return
   */
  public abstract Object getResult();

  /**
   * @return the type of CDS analysis
   */
  public abstract CdsAnalysisType getType();

  /**
   * Returns <code>true</code> if the result of the analysis was already
   * determined
   *
   * @return <code>true</code> if the result of the analysis was already
   *         determined
   */
  public boolean isResultLoaded() {
    return isResultLoaded;
  }

  /**
   * Refreshes this analysis
   */
  public abstract void refreshAnalysis();

  /**
   * Updates the loaded status of the result
   *
   * @param isResultLoaded <code>true</code> if the result is loaded
   */
  public void setResultLoaded(final boolean isResultLoaded) {
    this.isResultLoaded = isResultLoaded;
  }

  /**
   * Returns a prefix for the label of this analysis
   *
   * @return
   */
  protected abstract String getLabelPrefix();
}
