package com.devepos.adt.saat.internal.tree;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

/**
 * Tree node that represents an ABAP Development package
 *
 * @author stockbal
 */
public class PackageNode extends AdtObjectReferenceNode {

	private String childCountString;
	private int childCount;

	public PackageNode() {
		super(null);
	}

	public PackageNode(final String name, final String description, final IAdtObjectReference objectReference) {
		super(name, name, description, objectReference);
	}

	@Override
	public String getSizeAsString() {
		if (this.childCountString == null) {
			determineNonPackageCount(getChildren());
			this.childCountString = new DecimalFormat("###,###").format(this.childCount);
		}
		return this.childCountString;
	}

	public List<PackageNode> getSubPackages() {
		final List<PackageNode> subPackages = new LinkedList<>();
		determineSubPackages(subPackages, getChildren());
		return subPackages;
	}

	private void determineSubPackages(final List<PackageNode> subPackages, final List<ITreeNode> children) {
		if (children == null) {
			return;
		}
		for (final ITreeNode childNode : children) {
			if (childNode instanceof PackageNode) {
				final PackageNode packageNode = (PackageNode) childNode;
				subPackages.add(packageNode);
				determineSubPackages(subPackages, packageNode.getChildren());
			}
		}
	}

	private void determineNonPackageCount(final List<ITreeNode> nodes) {
		if (nodes == null) {
			return;
		}
		for (final ITreeNode childNode : nodes) {
			if (!(childNode instanceof PackageNode)) {
				this.childCount++;
			} else {
				determineNonPackageCount(((ICollectionTreeNode) childNode).getChildren());
			}
		}
	}

}
