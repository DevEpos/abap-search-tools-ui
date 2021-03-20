package com.devepos.adt.saat.internal.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Extends a {@link FilteredTree} to always prefix an asterisk character in
 * front of the entered filter string
 *
 * @author stockbal
 */
public class PrefixedAsteriskFilteredTree extends FilteredTree {
    /**
     * Creates new instance of a {@link FilteredTree} which automatically prefixes a
     * <code>*</code> character at the start of the entered filter
     *
     * @param parent    the parent composite for the tree
     * @param treeStyle the style flags for the tree
     * @param filter    the pattern filter instance to be used for filtering the
     *                  tree content
     */
    public PrefixedAsteriskFilteredTree(final Composite parent, final int treeStyle, final PatternFilter filter) {
        super(parent, treeStyle, filter, true);
    }

    @Override
    protected String getFilterString() {
        final String string = super.getFilterString();
        if (string == null) {
            return null;
        }
        if (string.equals(getInitialText())) {
            return string;
        }
        if (string.length() > 0) {
            return "*" + string;
        }
        return string;
    }
}
