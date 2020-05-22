package org.sdf.lang;

import java.util.HashMap;

public class TreeObject extends Data implements ITreeObject {
	public String id;
	public String pid;
	public String name;
	public String label;
	public String descr;
	public String type;
	public int order;
	public boolean used;

	TreeItem item;

	public TreeObject() {

	}

	public TreeObject(String id, String pid, String name, String label,
			String descr) {
		setID(id);
		setParentID(pid);
		setName(name);
		setLabel(name);
		setDescription(descr);
	}

	public TreeObject(String id, String pid, String name, String label,
			String descr, int order) {
		this(id, pid, name, label, descr);
		this.order = order;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public void setParentID(String p_id) {
		this.pid = p_id;
	}

	public String getParentID() {
		return pid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		if (label == null || label.equals(""))
			label = name;
		this.label = label;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {

		return label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TreeItem getTreeItem() {
		return item;
	}

	public void setTreeItem(TreeItem item) {
		this.item = item;
	}

	public void setDescription(String descr) {
		this.descr = descr;
	}

	public void setUsed(boolean b) {
		this.used = used;
	}

	public boolean getUsed() {
		return this.used;
	}

	public String getDescription() {
		return descr;
	}

	public int getOrder() {
		return order;
	}

	public void set(String key, String value) {
		if (key == null || key.length() == 0)
			return;
		if (value == null || value.length() == 0)
			return;
		put(key, value);
	}

	public ITreeObject getClone() {
		TreeObject o = new TreeObject(getID(), getParentID(), getName(),
				getLabel(), getDescription(), getOrder());
		return o;
	}

	public void changeAdd(ITreeObject sub) {

	}

	public void changeRemove(ITreeObject sub) {

	}
}