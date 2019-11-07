package com.devepos.adt.saat.internal.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.ui.IFileEditorInput;

import com.devepos.adt.saat.internal.IDestinationProvider;
import com.devepos.adt.saat.internal.ObjectType;
import com.devepos.adt.saat.internal.tree.IAdtObjectReferenceNode;
import com.sap.adt.project.IProjectProvider;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

public class DataPreviewAdtObjectAdapterFactory implements IAdapterFactory {
	private static final Class<?>[] ADAPTER_LIST;

	static {
		ADAPTER_LIST = new Class[] { IAdtObject.class };
	}

	@Override
	public <T> T getAdapter(final Object adaptableObject, final Class<T> adapterType) {
		if (adapterType != IAdtObject.class) {
			return null;
		}
		if (adaptableObject instanceof ITextSelection) {
		} else if (adaptableObject instanceof IFileEditorInput) {
			final IFile file = ((IFileEditorInput) adaptableObject).getFile();
			final IAdtObjectReference objRef = Adapters.adapt(file, IAdtObjectReference.class);
			if (objRef == null) {
				return null;
			}
			final ObjectType objectType = ObjectType.getFromAdtType(objRef.getType());
			if (objectType == null || !objectType.supportsDataPreview()) {
				return null;
			}
			return adapterType.cast(new AdtObject(objRef.getName(), objRef, objectType, file.getProject()));
		} else if (adaptableObject instanceof TreeNode) {
			final IAdtObjectReference objRef = Adapters.adapt(adaptableObject, IAdtObjectReference.class);
			if (objRef == null) {
				return null;
			}
			final ObjectType objType = ObjectType.getFromAdtType(objRef.getType());
			if (objType == null || !objType.supportsDataPreview()) {
				return null;
			}
			final IProjectProvider projectProvider = Adapters.adapt(adaptableObject, IProjectProvider.class);
			return adapterType.cast(
				new AdtObject(objRef.getName(), objRef, objType, projectProvider != null ? projectProvider.getProject() : null));
		} else if (adaptableObject instanceof IAdtObjectReferenceNode) {
			final IAdtObjectReferenceNode objRefNode = (IAdtObjectReferenceNode) adaptableObject;
			final ObjectType objectType = objRefNode.getObjectType();
			/*
			 * Check for support data preview is not mandatory at this position
			 */
			if (objectType == null) {
				return null;
			}
			final IDestinationProvider destProvider = objRefNode.getAdapter(IDestinationProvider.class);
			if (destProvider != null && destProvider.getDestinationId() != null) {
				final IAbapProjectProvider projectProvider = AbapProjectProviderAccessor
					.getProviderForDestination(destProvider.getDestinationId());
				if (projectProvider != null) {
					return adapterType.cast(new AdtObject(objRefNode.getName(), objRefNode.getObjectReference(), objectType,
						projectProvider.getProject()));
				}
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
