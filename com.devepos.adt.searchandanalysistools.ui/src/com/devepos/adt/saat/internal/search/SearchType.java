package com.devepos.adt.saat.internal.search;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.util.IImages;

public enum SearchType {
  CDS_VIEW("CDS View", "cds", IImages.CDS_VIEW),
  DB_TABLE_VIEW("Database Table/View", "dbtabview", IImages.TABLE_DEFINITION),
  CLASS_INTERFACE("Class/Interface", "classintf", IImages.CLASS_INTERFACE);

  private final String name;
  private final String uriTerm;
  private final String imageId;

  SearchType(final String description, final String uriTerm, final String imageId) {
    name = description;
    this.uriTerm = uriTerm;
    this.imageId = imageId;
  }

  /**
   * Creates key/value pair array from enum name and description
   *
   * @return key/value pair array from enum name and description
   */
  public static String[][] toNamesAndKeys() {
    final SearchType[] types = SearchType.values();
    final String[][] keyValue = new String[types.length][2];
    for (int i = 0; i < types.length; i++) {
      keyValue[i][0] = types[i].toString();
      keyValue[i][1] = types[i].name();
    }
    return keyValue;
  }

  public String getUriTerm() {
    return uriTerm;
  }

  public ImageDescriptor getImageDescriptor() {
    return SearchAndAnalysisPlugin.getDefault().getImageDescriptor(imageId);
  }

  public Image getImage() {
    return SearchAndAnalysisPlugin.getDefault().getImage(imageId);
  }

  @Override
  public String toString() {
    return name;
  }

}
