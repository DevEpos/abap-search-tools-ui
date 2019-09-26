package com.devepos.adt.saat.internal.tree;

/**
 * Element which will be shown in tree viewer if an error occurred during the
 * loading of some child elements
 *
 * @author stockbal
 */
public class LoadingErrorNode extends TreeNodeBase {
	private final Throwable exception;

	/**
	 * @param exception
	 * @param message
	 */
	public LoadingErrorNode(final ITreeNode parent, final String message, final Throwable exception) {
		super(null, message != null ? message : "Error during content loading occurred", null, parent);
		this.exception = exception;
	}

	public Throwable getException() {
		return this.exception;
	}

	@Override
	public String getImageId() {
		// TODO Auto-generated method stub
		return null;
	}

}