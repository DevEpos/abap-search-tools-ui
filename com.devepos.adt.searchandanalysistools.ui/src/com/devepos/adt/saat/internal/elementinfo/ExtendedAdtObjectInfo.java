package com.devepos.adt.saat.internal.elementinfo;

import com.devepos.adt.saat.internal.IDataSourceType;
import com.devepos.adt.saat.internal.search.IExtendedAdtObjectInfo;

public class ExtendedAdtObjectInfo implements IExtendedAdtObjectInfo {

  private boolean isReleased;
  private String owner;
  private IDataSourceType sourceType;

  /**
   * Sets the owner of this ADT object
   *
   * @param owner the owner
   */
  public void setOwner(final String owner) {
    this.owner = owner;
  }

  /**
   * Sets the API State of the ADT Object
   *
   * @param apiState the API state. It can only have the following values:
   *                 <ul>
   *                 <li>{@link IExtendedAdtObjectInfo#API_STATE_DEPRECATED}</li>
   *                 <li>{@link IExtendedAdtObjectInfo#API_STATE_RELEASED}</li>
   *                 </ul>
   */
  public void setApiState(final String apiState) {
    if (apiState == null) {
      return;
    }
    if (API_STATE_DEPRECATED.equals(apiState)) {
      isReleased = false;
    } else if (API_STATE_RELEASED.equals(apiState)) {
      isReleased = true;
    }
  }

  /**
   * @param sourceType the sourceType to set
   */
  public void setSourceType(final IDataSourceType sourceType) {
    this.sourceType = sourceType;
  }

  @Override
  public boolean isReleased() {
    return isReleased;
  }

  @Override
  public IDataSourceType getSourceType() {
    return sourceType;
  }

  @Override
  public String getOwner() {
    return owner;
  }
}