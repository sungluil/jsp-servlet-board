/*****************************************************************
 * Class : Column.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park / jeros@epitomie.com
 *****************************************************************/

package org.sdf.rgb;

public class Column implements Cloneable {
	String name;
	String label;
	int type;

	int size = -1;

	public Column(String name) {
		setName(name);
		setLabel(name);
	}

	public Column(String name, String label) {
		// Oracle?? ê²½ì?? nameê³? label?? ???¼í??ê²? alias ë¡? ???? ??.
		// MariaDB, MySQL?? ê²½ì?? column?? name, alias ê°? label ë¡? ???? ??.
		// ??ì²´ì???¼ë? name?? label ê°????¼ë? ?¬ì?©í??ê³? ???´ì??
		// set ???? ë¶?ë¶????? label -> name?¼ë? ??ì²? ??.
		// 2019.07.03 jw

		//this(name);	// jw
		this(label);	// jw
		setLabel(label);
	}

	public Column(String name, String label, String size) {
		this(name, label);
		setSize(size);
	}

	public Column(String name, String label, int type) {
		this(name, label);
		setType(type);
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public boolean equals(String name) {
		try {
			if (name.equals(this.name))
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	public void setName(String name) {
		this.name = name.toUpperCase();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setSize(String size) {
		try {
			setSize(Integer.parseInt(size));
		} catch (Exception e) {
		}
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		if (label == null || label.trim().equals(""))
			return name;
		return label;
	}

	public int getSize() {
		return size;
	}

	public String toString() {
		return "[ " + name + ", " + label + ", " + size + "]";
	}

	public Object clon() {
		return new Column(name, label, size);
	}
}