package org.sdf.lang;

import java.util.Comparator;

public class Sorter implements Comparator {
	public int compare(Object o1, Object o2) {
		Sorting s1 = (Sorting) o1;
		Sorting s2 = (Sorting) o2;
		return s1.order() - s2.order();
	}
}