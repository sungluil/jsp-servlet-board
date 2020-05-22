package org.sdf.rgb;

import org.sdf.rdb.RecordSet;

public class Distinctor {

	public static Integer[] invalidRows(RecordSet rs, int[] dupColumns,
			int compColumn) {
		java.util.Vector rows = new java.util.Vector();
		rs.reset();

		int num = rs.getCurIndex();

		while (rs.next()) {
			int curIndex = rs.getCurIndex();
			String[] values = new String[dupColumns.length];
			int compValue = rs.getInt(compColumn);

			for (int i = 0; i < values.length; i++) {
				values[i] = rs.get(dupColumns[i]);
			}

			int idx = exists(rs, dupColumns, values);
			if (idx >= 0) {
				if (compValue <= rs.getInt(compColumn))
					rows.addElement(new Integer(curIndex));
			}
			rs.setCurIndex(curIndex);
		}
		Integer[] tmp = new Integer[rows.size()];
		rows.copyInto(tmp);
		return tmp;
	}

	public static Integer[] validRows(RecordSet rs, int[] dupColumns,
			int compColumn) {

		java.util.Vector rows = new java.util.Vector();
		rs.reset();

		while (rs.next()) {
			int curIndex = rs.getCurIndex();

			String[] values = new String[dupColumns.length];
			int compValue = rs.getInt(compColumn);

			for (int i = 0; i < values.length; i++) {
				values[i] = rs.get(dupColumns[i]);
			}

			int idx = exists(rs, dupColumns, values);
			if (idx == -1) {
				rows.addElement(new Integer(curIndex));
			} else {
				if (compValue > rs.getInt(compColumn))
					rows.addElement(new Integer(idx));
			}
			rs.setCurIndex(curIndex);
		}
		Integer[] tmp = new Integer[rows.size()];
		rows.copyInto(tmp);
		return tmp;
	}

	public static int exists(RecordSet rs, int[] dupColumns, String[] values) {

		while (rs.next()) {
			boolean dup = true;
			for (int i = 0; i < values.length; i++) {
				if (!values[i].equals(rs.get(dupColumns[i]))) {
					dup = false;
					break;
				}
			}
			if (dup) {
				int idx = rs.getCurIndex();
				return idx;
			}
		}
		return -1;
	}

}