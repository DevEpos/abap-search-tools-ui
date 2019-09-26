package com.devepos.adt.saat.internal.search.history;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.devepos.adt.saat.SearchAndAnalysisPlugin;
import com.devepos.adt.saat.internal.messages.Messages;
import com.devepos.adt.saat.internal.search.favorites.NewSearchFavoriteDialog;
import com.devepos.adt.saat.internal.util.IImages;
import com.devepos.adt.saat.search.history.IObjectSearchHistoryEntry;

/**
 * Selection dialog for object search history entries
 *
 * @author stockbal
 */
public class SearchHistorySelectionDialog extends SelectionDialog {
	private static final int REMOVE_ID = IDialogConstants.CLIENT_ID + 1;
	private static final int CREATE_FAVORITE_ID = IDialogConstants.CLIENT_ID + 2;
	private static final int WIDTH_IN_CHARACTERS = 55;
	private static final int BUTTON_CHAR_WIDTH = 15;

	private final List<IObjectSearchHistoryEntry> input;
	private final List<IObjectSearchHistoryEntry> removedEntries;

	private TableViewer viewer;
	private Button removeButton;
	private Button createFavorite;

	public SearchHistorySelectionDialog(final Shell parent) {
		super(parent);
		setTitle(Messages.SearchHistorySelectionDialog_PreviousSearches_xtit);
		setMessage(Messages.SearchHistorySelectionDialog_InfoMessage_xmsg);
		this.input = new ArrayList<>(SearchAndAnalysisPlugin.getDefault().getHistory().getEntries());
		setInitialSelections(this.input.get(0));
		this.removedEntries = new ArrayList<>();
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();

		final List<?> initialSelection = getInitialElementSelections();
		if (initialSelection != null) {
			this.viewer.setSelection(new StructuredSelection(initialSelection));
		}

		validateDialogState();
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		return SearchAndAnalysisPlugin.getDefault().getDialogSettingsSection("DialogBounds_SearchHistorySelectionDialog"); //$NON-NLS-1$
	}

	@Override
	protected int getDialogBoundsStrategy() {
		return DIALOG_PERSISTSIZE;
	}

	@Override
	protected Label createMessageArea(final Composite composite) {
		final Composite parent = new Composite(composite, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).applyTo(parent);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(parent);

		final Label label = new Label(parent, SWT.WRAP);
		label.setText(getMessage());
		GridDataFactory.fillDefaults().span(2, 1).applyTo(label);

		applyDialogFont(label);
		return label;
	}

	/*
	 * Overrides method from Dialog
	 */
	@Override
	protected Control createDialogArea(final Composite container) {
		final Composite ancestor = (Composite) super.createDialogArea(container);

		createMessageArea(ancestor);

		final Composite parent = new Composite(ancestor, SWT.NONE);

		GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);

		this.viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		this.viewer.setContentProvider(new ArrayContentProvider());
		this.viewer.setLabelProvider(new HistoryEntryLabelProvider());
		this.viewer.addSelectionChangedListener(event -> validateDialogState());

		final Table table = this.viewer.getTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				okPressed();
			}
		});
		GridDataFactory.fillDefaults()
			.span(1, 2)
			.hint(convertWidthInCharsToPixels(WIDTH_IN_CHARACTERS), convertHeightInCharsToPixels(15))
			.grab(true, true)
			.applyTo(table);

		this.removeButton = new Button(parent, SWT.PUSH);
		this.removeButton.setText(Messages.SearchHistorySelectionDialog_DeleteHistoryEntry_xbut);
		this.removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				buttonPressed(REMOVE_ID);
			}
		});
		GridDataFactory.fillDefaults()
			.align(SWT.BEGINNING, SWT.BEGINNING)
			.hint(convertWidthInCharsToPixels(BUTTON_CHAR_WIDTH), SWT.DEFAULT)
			.applyTo(this.removeButton);

		this.createFavorite = new Button(parent, SWT.PUSH);
		this.createFavorite.setText(Messages.SearchHistorySelectionDialog_AddFavorite_xbut);
		this.createFavorite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				buttonPressed(CREATE_FAVORITE_ID);
			}
		});
		GridDataFactory.fillDefaults()
			.align(SWT.BEGINNING, SWT.BEGINNING)
			.hint(convertWidthInCharsToPixels(BUTTON_CHAR_WIDTH), SWT.DEFAULT)
			.applyTo(this.createFavorite);

		applyDialogFont(ancestor);

		// set input & selections last, so all the widgets are created.
		this.viewer.setInput(this.input);
		this.viewer.getTable().setFocus();
		return ancestor;
	}

	protected final void validateDialogState() {
		final IStructuredSelection sel = this.viewer.getStructuredSelection();
		final int elementsSelected = sel.toList().size();

		this.removeButton.setEnabled(elementsSelected > 0);
		this.createFavorite.setEnabled(elementsSelected == 1);
		final Button openButton = getOkButton();
		if (openButton != null) {
			openButton.setEnabled(elementsSelected == 1);
		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OPEN_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void buttonPressed(final int buttonId) {
		if (buttonId == REMOVE_ID) {
			final IStructuredSelection selection = this.viewer.getStructuredSelection();
			final Iterator<?> historyEntries = selection.iterator();
			while (historyEntries.hasNext()) {
				final IObjectSearchHistoryEntry curr = (IObjectSearchHistoryEntry) historyEntries.next();
				this.removedEntries.add(curr);
				this.input.remove(curr);
				this.viewer.remove(curr);
			}
			if (this.viewer.getSelection().isEmpty() && !this.input.isEmpty()) {
				this.viewer.setSelection(new StructuredSelection(this.input.get(0)));
			}
			return;
		} else if (buttonId == CREATE_FAVORITE_ID) {
			final IStructuredSelection selection = this.viewer.getStructuredSelection();
			new NewSearchFavoriteDialog(getShell(), ((IObjectSearchHistoryEntry) selection.getFirstElement()).getQuery()).open();
		}
		super.buttonPressed(buttonId);
	}

	/*
	 * Overrides method from Dialog
	 */
	@Override
	protected void okPressed() {
		// Build a list of selected children.
		final ISelection selection = this.viewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			setResult(this.viewer.getStructuredSelection().toList());
		}

		// remove history entries
		for (final IObjectSearchHistoryEntry historyEntry : this.removedEntries) {
			SearchAndAnalysisPlugin.getDefault().getHistory().removeHistoryEntry(historyEntry);
		}
		super.okPressed();
	}

	private static final class HistoryEntryLabelProvider extends LabelProvider {

		@Override
		public String getText(final Object element) {
			return ((IObjectSearchHistoryEntry) element).toString();
		}

		@Override
		public Image getImage(final Object element) {
			final IObjectSearchHistoryEntry historyEntry = (IObjectSearchHistoryEntry) element;

			switch (historyEntry.getQuery().getSearchType()) {
			case CDS_VIEW:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.CDS_VIEW);
			case DB_TABLE_VIEW:
				return SearchAndAnalysisPlugin.getDefault().getImage(IImages.TABLE_DEFINITION);
			default:
				return null;
			}
		}
	}
}
