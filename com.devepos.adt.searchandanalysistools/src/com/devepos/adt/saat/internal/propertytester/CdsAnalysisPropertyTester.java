package com.devepos.adt.saat.internal.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

import com.devepos.adt.saat.internal.util.AdtUtil;
import com.devepos.adt.saat.internal.util.IAdtObject;

/**
 * Property test for evaluating if the CDS Analysis is possible
 * 
 * @author stockbal
 */
public class CdsAnalysisPropertyTester extends PropertyTester {

	public CdsAnalysisPropertyTester() {
	}

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		if ("isFeatureAvailable".equals(property) && receiver instanceof IAdtObject) {
			final IProject project = ((IAdtObject) receiver).getProject();
			if (project != null) {
				return AdtUtil.isCdsAnalysisAvailable(project);
			}
		}
		return false;
	}

}
