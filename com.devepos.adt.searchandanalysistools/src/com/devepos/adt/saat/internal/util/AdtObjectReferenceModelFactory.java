package com.devepos.adt.saat.internal.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import com.devepos.adt.saat.IDestinationProvider;
import com.devepos.adt.saat.ObjectType;
import com.sap.adt.tools.core.model.adtcore.IAdtExtension;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Factory for creating instances of {@link IAdtObjectReference}
 *
 * @author stockbal
 */
public class AdtObjectReferenceModelFactory {

	/**
	 * Creates new {@link IAdtObjectReference}
	 *
	 * @return
	 */
	public static IAdtObjectReference createReference() {
		return createReference(null, null, null, null);
	}

	/**
	 * Creates new {@link IAdtObjectReference}
	 *
	 * @param  destinationId the id of a destination
	 * @return
	 */
	public static IAdtObjectReference createReference(final String destinationId) {
		return createReference(destinationId, null, null, null);
	}

	/**
	 * Creates new {@link IAdtObjectReference} with the given attributes
	 *
	 * @param  destinationId the id of a destination
	 * @param  name          the name of the object reference
	 * @param  type          the type of the object reference
	 *                       {@link ObjectType#getAdtExecutionType()}
	 * @param  uri           the URI of the object reference
	 * @return
	 */
	public static IAdtObjectReference createReference(final String destinationId, final String name, final String type,
		final String uri) {
		return new AdtObjectRefImpl(destinationId, name, type, uri);
	}

	private static class AdtObjectRefImpl implements IAdtObjectReference, IDestinationProvider {
		private String name;
		private String uri;
		private String type;
		private String packageName;
		private String description;
		private String parentUri;
		private String destinationId;

		public AdtObjectRefImpl(final String destinationId, final String name, final String type, final String uri) {
			this.destinationId = destinationId;
			this.name = name;
			this.uri = uri;
			this.type = type;
		}

		@Override
		public TreeIterator<EObject> eAllContents() {
			return null;
		}

		@Override
		public EClass eClass() {
			return null;
		}

		@Override
		public EObject eContainer() {
			return null;
		}

		@Override
		public EStructuralFeature eContainingFeature() {
			return null;
		}

		@Override
		public EReference eContainmentFeature() {
			return null;
		}

		@Override
		public EList<EObject> eContents() {
			return null;
		}

		@Override
		public EList<EObject> eCrossReferences() {
			return null;
		}

		@Override
		public Object eGet(final EStructuralFeature arg0) {
			return null;
		}

		@Override
		public Object eGet(final EStructuralFeature arg0, final boolean arg1) {
			return null;
		}

		@Override
		public Object eInvoke(final EOperation arg0, final EList<?> arg1) throws InvocationTargetException {
			return null;
		}

		@Override
		public boolean eIsProxy() {
			return false;
		}

		@Override
		public boolean eIsSet(final EStructuralFeature arg0) {
			return false;
		}

		@Override
		public Resource eResource() {
			return null;
		}

		@Override
		public void eSet(final EStructuralFeature arg0, final Object arg1) {
		}

		@Override
		public void eUnset(final EStructuralFeature arg0) {
		}

		@Override
		public EList<Adapter> eAdapters() {
			return null;
		}

		@Override
		public boolean eDeliver() {
			return false;
		}

		@Override
		public void eNotify(final Notification arg0) {
		}

		@Override
		public void eSetDeliver(final boolean arg0) {
		}

		@Override
		public String getDescription() {
			return this.description;
		}

		@Override
		public IAdtExtension getExtension() {
			return null;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getPackageName() {
			return this.packageName;
		}

		@Override
		public String getParentUri() {
			return this.parentUri;
		}

		@Override
		public String getType() {
			return this.type;
		}

		@Override
		public String getUri() {
			return this.uri;
		}

		@Override
		public void setDescription(final String description) {
			this.description = description;
		}

		@Override
		public void setExtension(final IAdtExtension arg0) {
		}

		@Override
		public void setName(final String name) {
			this.name = name;
		}

		@Override
		public void setPackageName(final String packageName) {
			this.packageName = packageName;
		}

		@Override
		public void setParentUri(final String parentUri) {
			this.parentUri = parentUri;
		}

		@Override
		public void setType(final String type) {
			this.type = type;
		}

		@Override
		public void setUri(final String uri) {
			this.uri = uri;
		}

		@Override
		public String getDestinationId() {
			return this.destinationId;
		}

		@Override
		public void setDestinationId(final String destinationId) {
			this.destinationId = destinationId;
		}

		@Override
		public String getSystemId() {
			if (this.destinationId == null || this.destinationId.isEmpty()) {
				return null;
			}
			final String[] destination = getDestinationId().split("_");
			return destination[0];
		}
	}
}