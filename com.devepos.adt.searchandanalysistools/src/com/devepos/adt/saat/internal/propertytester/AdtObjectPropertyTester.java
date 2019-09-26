package com.devepos.adt.saat.internal.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import com.devepos.adt.saat.ObjectType;
import com.devepos.adt.saat.internal.util.IAdtObject;

public class AdtObjectPropertyTester extends PropertyTester {
	/**
	 * A property indicating the type of ADT Object
	 */
	private static final String TYPE = "type";

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		if (!(receiver instanceof IAdtObject)) {
			return false;
		}
		final IAdtObject adtObject = (IAdtObject) receiver;
		if (property.equals(TYPE) && expectedValue != null) {
			final ObjectType objType = ObjectType.getFromId((String) expectedValue);
			return objType != null && objType.equals(adtObject.getObjectType());
		}
		return false;
	}

}
