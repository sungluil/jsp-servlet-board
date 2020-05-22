package org.sdf.lang;

import java.util.*;

public class TreeItem implements Sorting {

	protected Tree parent;
	protected int depth;

	protected ITreeObject obj;

	public TreeItem(ITreeObject o) {
		setObject(o);
		if (o != null)
			o.setTreeItem(this);
	}

	public TreeItem getClone() {
		TreeItem item = new TreeItem(obj.getClone());
		return item;
	}

	public boolean equals(String id) {
		if (id == null || this.getID() == null)
			return false;
		return getID().equals(id);
	}

	public int getMaxDepth() {
		return 1;
	}

	public TreeItem getTreeItem(String id) {
		if (equals(id))
			return this;
		return null;
	}

	public int getRowsCount(int depth) {
		return 1;
	}

	public int getTotalDepth(int depth) {
		return 1;
	}

	public void setDepth() {
		if (parent == null)
			depth = 0;
		else
			this.depth = parent.depth + 1;
	}

	public void setParent(Tree p) {
		this.parent = p;
		this.depth = p.depth + 1;

		parent.changeAdd(this);
	}

	public int order() {
		return obj.getOrder();
	}

	public void removeParent() {
		if (parent != null) {
			parent.changeRemove(this);
		}
		this.depth = 0;
	}

	public String getName() {
		return obj.getName();
	}

	public String getLabel() {
		return obj.getName();
	}

	public String getParentID() {
		return obj.getParentID();
	}

	public String getID() {
		return obj.getID();
	}

	public String getDescription() {
		return obj.getDescription();
	}

	public Tree getParent() {
		return parent;
	}

	public void setObject(ITreeObject o) {
		this.obj = o;
	}

	public ITreeObject getObject() {
		return obj;
	}

	public ITreeObject get() {
		if (obj == null)
			return null;
		return obj;
	}

	public int getDepth() {
		return depth;
	}

	public boolean hasChild() {
		return false;
	}

	public boolean canExpand() {
		return false;
	}

	public boolean isRoot() {
		return false;
	}

	public int getItemCount() {
		return 0;
	}

	public int getSubItemCount() {
		return 0;
	}

	public int getSubTreeCount() {
		return 0;
	}

	public String getClassName() {
		return getClass().getName();
	}

	public String getFullName(int level) {
		System.out.println(level + ":" + getDepth() + ":" + this);
		if (level >= getDepth()) {

			return getName();
		}
		if (parent == null) {

			return getName();
		}

		return parent.getFullName(level) + "/" + getName();
	}

	public String getFullName(int level, String delim) {

		if (level >= getDepth()) {

			return getName();
		}
		if (parent == null) {

			return getName();
		}

		return parent.getFullName(level, delim) + delim + getName();
	}

	public String getFullName(String delim) {
		if (parent == null)
			return getName();
		return parent.getFullName(delim) + delim + getName();
	}

	public String getFullName() {
		if (parent == null)
			return getName();
		return parent.getFullName() + "/" + getName();
	}

	public String getFullID() {
		if (parent == null)
			return getID();
		return parent.getFullID() + "." + getID();
	}

	public String identy() {
		return getID() + ":" + getLabel() + ":" + getDepth();
	}

	public String toString() {
		return getClassName() + "[" + getID() + ":" + getParentID() + ":"
				+ getName() + ":" + getDepth() + "]";
	}

}