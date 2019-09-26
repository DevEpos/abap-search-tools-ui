package com.devepos.adt.saat.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

/**
 * Describes a Set of Toggle Actions where only one Action can be active at a
 * time
 *
 * @author stockbal
 */
public class RadioActionGroup extends ActionGroup {
	/**
	 * Listener which notifies the subscriber when an action in the Radio Action
	 * Group was toggled
	 *
	 * @author stockbal
	 */
	@FunctionalInterface
	public interface IRadioActionToggleListener {
		/**
		 * This method will be called when the action with the id <code>actionId</code>
		 * was toggled
		 *
		 * @param actionId id of the toggled Action
		 */
		void toggled(String actionId);
	}

	private ToggleAction toggledAction;
	private final List<ToggleAction> actions;
	private final List<IRadioActionToggleListener> actionListener;

	public RadioActionGroup() {
		this.actions = new ArrayList<>();
		this.actionListener = new ArrayList<>();
	}

	/**
	 * Returns <code>true</code> if the action with the given id
	 * <code>actionId</code>
	 *
	 * @param  actionId the action Id to be checked for the toggled state
	 * @return          <code>true</code> if the action with the given id
	 *                  <code>actionId</code>
	 */
	public boolean isActionToggled(final String actionId) {
		final Action action = this.actions.stream().filter(a -> a.actionId.equals(actionId)).findFirst().orElse(null);
		if (action != null) {
			return action.isChecked();
		}
		return false;
	}

	/**
	 * Returns the Id of currently toggled action
	 *
	 * @return the Id of currently toggled action
	 */
	public String getToggledActionId() {
		return this.toggledAction != null ? this.toggledAction.actionId : null;
	}

	/**
	 * Enables/Disables the action with the given <code>actionId</code> in this
	 * {@link RadioActionGroup}.
	 *
	 * @param actionId the Id of the action to be enabled/disabled
	 * @param enable   if <code>true</code> the action will be enabled
	 */
	public void enableAction(final String actionId, final boolean enable) {
		final ToggleAction actionToEnable = this.actions.stream()
			.filter(a -> a.actionId.equals(actionId))
			.findFirst()
			.orElse(null);
		if (actionToEnable != null) {
			actionToEnable.setEnabled(enable);
			if (!enable && actionToEnable.isChecked()) {
				// search for another action to be toggled
				final ToggleAction newToggleAction = this.actions.stream()
					.filter(a -> !a.actionId.equals(actionId))
					.findFirst()
					.orElse(null);
				if (newToggleAction != null) {
					newToggleAction.setChecked(true);
					this.toggledAction = newToggleAction;
				} else {
					this.toggledAction = null;
				}
			}
		}

	}

	/**
	 * Adds new action to the Radio Action Group
	 *
	 * @param actionId        the Id of the Action to be added to the group
	 * @param tooltip         the tool tip of the action
	 * @param imageDescriptor the image descriptor of the action
	 * @param toggled         if <code>true</code> the created action will be
	 *                        toggled
	 */
	public void addAction(final String actionId, final String tooltip, final ImageDescriptor imageDescriptor,
		final boolean toggled) {
		final ToggleAction toggleAction = new ToggleAction(actionId, tooltip, imageDescriptor);
		toggleAction.setChecked(toggled);
		if (toggled) {
			this.toggledAction = toggleAction;
		}
		this.actions.add(toggleAction);
	}

	/**
	 * Adds the items of this Radio Action Group to the provided tool bar manager
	 *
	 * @param tbm a tool bar manager where the radio actions should be added to
	 */
	public void contributeToToolbar(final IToolBarManager tbm) {
		for (final Action radioAction : this.actions) {
			tbm.add(radioAction);
		}
	}

	@Override
	public void fillActionBars(final IActionBars actionBars) {
		contributeToToolbar(actionBars.getToolBarManager());
	}

	/**
	 * Adds the given action toggled listener to this <code>RadioActionGroup</code>
	 * <br>
	 * Has no effect if the same listener was already added
	 *
	 * @param l the listener to be added
	 */
	public void addActionToggledListener(final IRadioActionToggleListener l) {
		this.actionListener.add(l);
	}

	/**
	 * Removes the given action toggled listener from this
	 * <code>RadioActionGroup</code> <br>
	 * Has no effect if the same listener was already removed
	 *
	 * @param l the listener to be removed
	 */
	public void removeActionToggledListener(final IRadioActionToggleListener l) {
		this.actionListener.remove(l);
	}

	private void toggleAction(final String actionId) {
//		boolean isAlreadyToggled = false;
		for (final ToggleAction action : this.actions) {
			if (actionId.equals(action.actionId)) {
				this.toggledAction = action;
//				if (action.isChecked()) {
//					isAlreadyToggled = true;
//				}
//			} else {
//				action.setChecked(false);
			}
		}
//		if (!isAlreadyToggled) {
		fireToggled(actionId);
//		}
	}

	private void fireToggled(final String actionId) {
		for (final IRadioActionToggleListener l : this.actionListener) {
			l.toggled(actionId);
		}
	}

	private final class ToggleAction extends Action {
		private final String actionId;

		public ToggleAction(final String actionId, final String tooltip, final ImageDescriptor imageDescriptor) {
			super(tooltip, AS_RADIO_BUTTON);
			setImageDescriptor(imageDescriptor);
			this.actionId = actionId;
		}

		@Override
		public void run() {
			if (isChecked()) {
				toggleAction(this.actionId);
			}
		}
	}

}
