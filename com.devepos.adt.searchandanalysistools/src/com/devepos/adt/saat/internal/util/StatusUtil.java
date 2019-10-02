package com.devepos.adt.saat.internal.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class for {@link Status}
 *
 * @author stockbal
 */
public class StatusUtil {
	/**
	 * @param  status the status code to be converted to an image
	 * @return        the {@link Image} for the given Status code or
	 *                <code>null</code>
	 */
	public static Image getImageForStatus(final int status) {
		switch (status) {
		case IStatus.INFO:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		case IStatus.ERROR:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		case IStatus.OK:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		case IStatus.WARNING:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		default:
			return null;
		}
	}
}
