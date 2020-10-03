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
		return this.hasSelection;
	}

	/**
	 * Returns <code>true</code> if at least 1 ADT object is selected
	 *
	 * @param  supportsDataPreview if <code>true</code> the selected object must
	 *                             support the Data preview function
	 * @return
	 */
	public boolean hasSelection(final boolean supportsDataPreview) {
		if (!this.hasSelection) {
			return false;
		}
		return supportsDataPreview ? this.selectionSupportsDataPreview : true;
	}

	/**
	 * Returns <code>true</code> if exactly one ADT object is selected
	 *
	 * @return
	 */
	public boolean hasSingleSelection() {
		return this.hasSingleSelection;
	}

	/**
	 * Returns <code>true</code> if exactly one ADT object is selected
	 *
	 * @param  supportsDataPreview if <code>true</code> the selected object must
	 *                             support the Data preview function
	 * @return
	 */
	public boolean hasSingleSelection(final boolean supportsDataPreview) {
		if (!this.hasSingleSelection) {
			return false;
		}

		return supportsDataPreview ? this.selectionSupportsDataPreview : true;
	}

	/**
	 * Returns <code>true</code> if the command with the given id should be enabled
	 *
	 * @param  commandId unique ID of a command
	 * @return
	 */
	public boolean canCommandBeEnabled(final String commandId) {
		switch (commandId) {
		case ICommandConstants.CDS_TOP_DOWN_ANALYSIS:
			return isSingleCdsSelection()
				&& FeatureTester.isCdsTopDownAnalysisAvailable(this.selectedAdtObjects.get(0).getProject());
		case ICommandConstants.USED_ENTITIES_ANALYSIS:
			return isSingleCdsSelection()
				&& FeatureTester.isCdsUsedEntitiesAnalysisAvailable(this.selectedAdtObjects.get(0).getProject());
		case ICommandConstants.WHERE_USED_IN_CDS_ANALYSIS:
			return hasSingleSelection(true);
		case ICommandConstants.FIELD_ANALYSIS:
			return hasSingleSelection(true);
		case ICommandConstants.OPEN_IN_DB_BROWSER:
			return hasSelection(true) && FeatureTester.isSapGuiDbBrowserAvailable(this.selectedAdtObjects);
		}
		return false;
	}

	/**
	 * Returns a list of the selected ADT object references or <code>null</code> if
	 * there is no selection
	 *
	 * @see    CommandPossibleChecker#hasSelection()
	 * @return
	 */
	public List<IAdtObjectReference> getSelectedAdtObjectRefs() {
		if (!hasSelection()) {
			return null;
		}
		return this.selectedAdtObjects.stream().map(IAdtObject::getReference).collect(Collectors.toList());
	}

	protected void evaluateSelection(final boolean supportsDataPreviewOnly) {
		this.selectedAdtObjects = AdtUIUtil.getAdtObjectsFromSelection(supportsDataPreviewOnly);
		this.hasSelection = this.selectedAdtObjects != null && !this.selectedAdtObjects.isEmpty();
		if (!this.hasSelection) {
			return;
		}
		this.hasSingleSelection = this.selectedAdtObjects.size() == 1;

		this.selectionSupportsDataPreview = true;

		for (final IAdtObject selected : this.selectedAdtObjects) {
			if (!selected.getObjectType().supportsDataPreview()) {
				this.selectionSupportsDataPreview = false;
				break;
			}
		}
	}

	private boolean isSingleCdsSelection() {
		return hasSingleSelection() && this.selectedAdtObjects.get(0).getObjectType() == ObjectType.CDS_VIEW;
	}

}
