package com.devepos.adt.saat.internal.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

import com.devepos.adt.saat.internal.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.ui.RadioActionGroup.IRadioActionToggleListener;
import com.devepos.adt.saat.internal.util.IImages;

/**
 * Menu with actions to toggle the layout of a view. <br>
 * This action makes only sense if a view has a {@link SashForm} control as its
 * main control
 *
 * @see    ViewLayoutOrientation
 * @author stockbal
 */
public class ToggleViewLayoutAction extends MenuManager implements IRadioActionToggleListener {

	private final RadioActionGroup orientationActionGroup;
	private final SashForm sashForm;
	private final IPreferenceStore prefStore;
	private final String layoutPref;
	private ControlListener resizeListener;
	private final Control resizedControl;

	public ToggleViewLayoutAction(final SashForm sashForm, final Control resizedControl, final String layoutPref) {
		this(sashForm, resizedControl, layoutPref, true, true, true);
	}

	public ToggleViewLayoutAction(final SashForm sashForm, final Control resizedControl, final String layoutPref,
		final boolean horizontal, final boolean vertical, final boolean automatic) {
		super(Messages.ToggleViewLayoutAction_TopLevelMenu_xmit);

		this.prefStore = SearchAndAnalysisPlugin.getDefault().getPreferenceStore();
		this.sashForm = sashForm;
		this.layoutPref = layoutPref;
		this.resizedControl = resizedControl;
		this.orientationActionGroup = createActionGroup(horizontal, vertical, automatic);

		ViewLayoutOrientation initialOrientation = null;
		try {

			initialOrientation = ViewLayoutOrientation.valueOf(this.prefStore.getString(layoutPref));
		} catch (final IllegalArgumentException exc) {
			initialOrientation = automatic ? ViewLayoutOrientation.AUTOMATIC : ViewLayoutOrientation.VERTICAL;
		}

		this.orientationActionGroup.setActionChecked(initialOrientation.name());
		executeLayoutChange(initialOrientation);
		this.orientationActionGroup.contributeToMenuManager(this);
		this.orientationActionGroup.addActionToggledListener(this);

		if (automatic) {
			this.resizeListener = new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent e) {
					executeLayoutChange(ViewLayoutOrientation.AUTOMATIC);
				}
			};
			resizedControl.addDisposeListener(e -> resizedControl.removeControlListener(this.resizeListener));

			if (initialOrientation == ViewLayoutOrientation.AUTOMATIC) {
				resizedControl.addControlListener(this.resizeListener);
			}
		}
	}

	@Override
	public void toggled(final String actionId) {
		final ViewLayoutOrientation orientation = ViewLayoutOrientation.valueOf(actionId);
		if (orientation == null) {
			return;
		}
		if (orientation == ViewLayoutOrientation.AUTOMATIC) {
			this.resizedControl.addControlListener(this.resizeListener);
		} else {
			this.resizedControl.removeControlListener(this.resizeListener);
		}
		executeLayoutChange(orientation);
		this.prefStore.setValue(this.layoutPref, actionId);
	}

	private RadioActionGroup createActionGroup(final boolean horizontal, final boolean vertical, final boolean automatic) {
		final RadioActionGroup actionGroup = new RadioActionGroup();
		if (horizontal) {
			actionGroup.addAction(ViewLayoutOrientation.HORIZONTAL.name(), Messages.ToggleViewLayoutAction_HorizontalOrientation_xmit,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.HORIZONTAL_LAYOUT), false);
		}
		if (vertical) {
			actionGroup.addAction(ViewLayoutOrientation.VERTICAL.name(), Messages.ToggleViewLayoutAction_VerticalOrientation_xmit,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.VERTICAL_LAYOUT), false);
		}
		if (automatic) {
			actionGroup.addAction(ViewLayoutOrientation.AUTOMATIC.name(), Messages.ToggleViewLayoutAction_AutomaticOrientation_xmit,
				SearchAndAnalysisPlugin.getDefault().getImageDescriptor(IImages.AUTOMATIC_LAYOUT), false);
		}
		return actionGroup;
	}

	private void executeLayoutChange(final ViewLayoutOrientation orientation) {
		if (orientation == ViewLayoutOrientation.AUTOMATIC) {
			final Point size = this.resizedControl.getSize();
			if (size.x != 0 && size.y != 0) {
				if (size.x > size.y) {
					setOrientation(ViewLayoutOrientation.HORIZONTAL);
				} else {
					setOrientation(ViewLayoutOrientation.VERTICAL);
				}
			}
		} else {
			setOrientation(orientation);
		}
	}

	private void setOrientation(final ViewLayoutOrientation orientation) {
		if (this.sashForm == null || this.sashForm.isDisposed()) {
			return;
		}
		this.sashForm.setOrientation(orientation.getSwtOrientation());
	}
}
