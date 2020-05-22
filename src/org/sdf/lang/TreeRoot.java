/*****************************************************************
 * 
 ******************************************************************
 */
package org.sdf.lang;

import java.util.ArrayList;
import java.util.Comparator;

public class TreeRoot extends Tree {
	Comparator comp;

	public TreeRoot(ITreeObject o) {
		super(o);
		comp = new Sorter();
	}

	public Tree add(TreeItem item) {
		if (list == null)
			list = new ArrayList();

		Tree ptree = null;
		for (int i = 0; i < list.size(); i++) {
			try {
				Tree tree = (Tree) list.get(i);

				if (tree.getParentID() != null
						&& item.getID().equals(tree.getParentID())) {
					tree.setParent((Tree) item);
					list.remove(tree);
					continue;
				}

				ptree = tree.add(item);

				if (ptree != null)
					break;
			} catch (Exception e) {
			}
		}
		if (ptree == null) {
			item.setParent(this);
			((Tree) item).setComarator(comp);
			list.add(item);
		}
		return this;
	}

	public void init(ITreeObject[] objs) {
		for (int i = 0; i < objs.length; i++) {
			add(new Tree(objs[i]));
		}
	}

	public boolean isRoot() {
		return true;
	}

}