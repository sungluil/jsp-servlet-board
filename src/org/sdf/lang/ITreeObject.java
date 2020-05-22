package org.sdf.lang;

public interface ITreeObject extends IData {
	public String getID();

	public String getParentID();

	public String getName();

	public String getDescription();

	public int getOrder();

	public ITreeObject getClone();

	public String getType();

	public TreeItem getTreeItem();

	public void setTreeItem(TreeItem item);

	public void changeAdd(ITreeObject sub);

	public void changeRemove(ITreeObject sub);
}