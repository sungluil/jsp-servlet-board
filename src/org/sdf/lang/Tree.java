package org.sdf.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Tree extends TreeItem {
	protected List list;
	private String maxkey = "";

	public Tree(ITreeObject o) {
		super(o);
		list = new ArrayList();
	}

	public TreeItem getClone() {
		Tree item = new Tree(obj.getClone());
		// if( parent == null ) depth = 1;
		if (list == null)
			return item;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			TreeItem t = (TreeItem) list.get(i);
			item.add((TreeItem) t.getClone());
		}
		return item;
	}

	public int getMaxDepth() {
		int count = 1;
		if (list == null || list.size() == 0)
			return count;
		int max = 1;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			TreeItem t = (TreeItem) list.get(i);

			int d = t.getMaxDepth();
			if (max < d)
				max = d;
		}
		return count + max;
	}

	public void setDepth() {
		super.setDepth();

		if (list == null)
			return;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			TreeItem t = (TreeItem) list.get(i);
			t.setDepth();
		}

	}

	public int getRowsCount() {
		int count = 0;
		if (list == null)
			return 1;
		int size = list.size();
		if (size == 0)
			return 1;
		for (int i = 0; i < size; i++) {
			TreeItem item = (TreeItem) list.get(i);
			count += item.getRowsCount(1);
		}

		return count;
	}

	public int getTotalDepth(int depth) {
		int max = 0;
		if (this.depth == depth)
			return 1;
		if (list == null)
			return 1;
		int size = list.size();

		for (int i = 0; i < size; i++) {
			TreeItem item = (TreeItem) list.get(i);
			int d = item.getTotalDepth(depth);
			if (d > max)
				max = d;
		}
		return 1 + max;

	}

	public TreeItem getChildTreeItem(String id) {

		if (list == null)
			return null;
		for (int i = 0; i < list.size(); i++) {
			TreeItem tree = (TreeItem) list.get(i);
			if (tree.equals(id))
				return tree;
		}
		return null;
	}

	public Tree add(TreeItem item) {
		if (list == null)
			list = new ArrayList();

		if (getID().equals(item.getParentID())) {
			item.setParent(this);
			TreeItem sub = getChildTreeItem(item.getID());
			if (sub == null)
				list.add(item);
			else
				sub.setObject(item.getObject());
			return this;
		}

		if (item.getID().equals(getParentID())) {
			Tree p = getParent();
			if (p != null)
				p.list.remove(this);
			((Tree) item).add(this);
			this.setParent((Tree) item);
			return null;
		}

		for (int i = 0; i < list.size(); i++) {
			try {
				Tree tree = (Tree) list.get(i);
				Tree m = tree.add(item);
				if (m != null) {
					return m;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean exists(String id) {
		if (equals(id))
			return true;
		if (list == null)
			return false;

		for (int i = 0; i < list.size(); i++) {
			try {
				Tree tree = (Tree) list.get(i);
				boolean b = tree.exists(id);
				if (b)
					return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	public TreeItem getChild(String id) {
		return getSubTreeItem(id);
	}

	public TreeItem getChild(int i) {
		if (list == null)
			return null;
		try {
			return (TreeItem) list.get(i);
		} catch (Exception e) {
		}
		return null;
	}

	public TreeItem getTreeItem(String id) {
		if (equals(id))
			return this;
		return getSubTreeItem(id);
	}

	public TreeItem getSubTreeItem(String id) {
		if (list == null)
			return null;

		for (int i = 0; i < list.size(); i++) {
			try {
				TreeItem item = (TreeItem) list.get(i);
				TreeItem s = item.getTreeItem(id);
				if (s != null)
					return s;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public List getList() {
		return list;
	}

	public List getChildren() {
		return list;
	}

	public int getItemCount() {
		if (list == null)
			return 0;
		return list.size();
	}

	public int getSubTreeCount() {
		if (list == null)
			return 0;

		int count = 0;

		for (int i = 0; i < list.size(); i++) {
			try {
				Tree tree = (Tree) list.get(i);
				count += tree.getSubTreeCount();
				count++;

			} catch (Exception e) {
			}
		}
		return count;
	}

	public int getSubItemCount() {
		if (list == null)
			return 0;

		int count = 0;

		for (int i = 0; i < list.size(); i++) {
			try {
				Tree tree = (Tree) list.get(i);
				count += tree.getSubItemCount();
			} catch (Exception e) {
				count++;
			}

		}
		return count;
	}

	public boolean hasChild() {
		if (getItemCount() == 0)
			return false;
		return true;
	}

	public boolean canExpand() {
		return true;
	}

	public String getChildMaxKey() {
		return maxkey;
	}

	public void changeAdd(TreeItem item) {
		obj.changeAdd(item.getObject());
		if (parent != null)
			parent.changeAdd(this);
	}

	public void changeRemove(TreeItem item) {
		obj.changeRemove(item.getObject());
		if (parent != null)
			parent.changeRemove(this);
	}

	public void setComarator(Comparator comp) {
		this.comp = comp;
	}

	public void sort() {
		if (comp == null) {
			comp = new Sorter();
		}
		Collections.sort(list, comp);
	}

	public int size() {
		if (list == null)
			return 0;
		return list.size();
	}

	Comparator comp;

}