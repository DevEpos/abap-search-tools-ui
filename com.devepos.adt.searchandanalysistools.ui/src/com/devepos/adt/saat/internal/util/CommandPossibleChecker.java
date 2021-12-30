package com.devepos.adt.saat.internal.util;

import java.util.List;
import java.util.stream.Collectors;

import com.devepos.adt.base.ObjectType;
import com.devepos.adt.base.ui.adtobject.IAdtObject;
import com.devepos.adt.base.ui.util.AdtUIUtil;
import com.devepos.adt.saat.internal.ICommandConstants;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

public class CommandPossibleChecker {

  private boolean hasSelection;
  private boolean hasSingleSelection;
  private boolean selectionSupportsDataPreview;
  private List<IAdtObject> selectedAdtObjects;

  public CommandPossibleChecker() {
    this(true);
  }

  public CommandPossibleChecker(final boolean supportsDataPreviewOnly) {
    evaluateSelection(supportsDataPreviewOnly);
  }

  /**
   * Returns <code>true</code> if at least 1 ADT object is selected
   *
   * @return
   */
  public boolean hasSelection() {
    return hasSelection;
  }

  /**
   * Returns <code>true</code> if at least 1 ADT object is selected
   *
   * @param supportsDataPreview if <code>true</code> the selected object must
   *                            support the Data preview function
   * @return
   */
  public boolean hasSelection(final boolean supportsDataPreview) {
    if (!hasSelection) {
      return false;
    }
    return supportsDataPreview ? selectionSupportsDataPreview : true;
  }

  /**
   * Returns <code>true</code> if exactly one ADT object is selected
   *
   * @return
   */
  public boolean hasSingleSelection() {
    return hasSingleSelection;
  }

  /**
   * Returns <code>true</code> if exactly one ADT object is selected
   *
   * @param supportsDataPreview if <code>true</code> the selected object must
   *                            support the Data preview function
   * @return
   */
  public boolean hasSingleSelection(final boolean supportsDataPreview) {
    if (!hasSingleSelection) {
      return false;
    }

    return supportsDataPreview ? selectionSupportsDataPreview : true;
  }

  /**
   * Returns <code>true</code> if the command with the given id should be enabled
   *
   * @param commandId unique ID of a command
   * @return
   */
  public boolean canCommandBeEnabled(final String commandId) {
    switch (commandId) {
    case ICommandConstants.CDS_TOP_DOWN_ANALYSIS:
      return isSingleCdsSelection() && FeatureTester.isCdsTopDownAnalysisAvailable(
          selectedAdtObjects.get(0).getProject());
    case ICommandConstants.USED_ENTITIES_ANALYSIS:
      return isSingleCdsSelection() && FeatureTester.isCdsUsedEntitiesAnalysisAvailable(
          selectedAdtObjects.get(0).getProject());
    case ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS:
      return hasSingleSelection(true);
    case ICommandConstants.FIELD_ANALYSIS:
      return hasSingleSelection(true);
    case ICommandConstants.OPEN_IN_DB_BROWSER:
      return hasSelection(true) && FeatureTester.isSapGuiDbBrowserAvailable(selectedAdtObjects);
    }
    return false;
  }

  /**
   * Returns a list of the selected ADT object references or <code>null</code> if
   * there is no selection
   *
   * @see CommandPossibleChecker#hasSelection()
   * @return
   */
  public List<IAdtObjectReference> getSelectedAdtObjectRefs() {
    if (!hasSelection()) {
      return null;
    }
    return selectedAdtObjects.stream().map(IAdtObject::getReference).collect(Collectors.toList());
  }

  protected void evaluateSelection(final boolean supportsDataPreviewOnly) {
    selectedAdtObjects = AdtUIUtil.getAdtObjectsFromSelection(supportsDataPreviewOnly);
    hasSelection = selectedAdtObjects != null && !selectedAdtObjects.isEmpty();
    if (!hasSelection) {
      return;
    }
    hasSingleSelection = selectedAdtObjects.size() == 1;

    selectionSupportsDataPreview = true;

    for (final IAdtObject selected : selectedAdtObjects) {
      if (!selected.getObjectType().supportsDataPreview()) {
        selectionSupportsDataPreview = false;
        break;
      }
    }
  }

  private boolean isSingleCdsSelection() {
    return hasSingleSelection() && selectedAdtObjects.get(0)
        .getObjectType() == ObjectType.DATA_DEFINITION;
  }

}
