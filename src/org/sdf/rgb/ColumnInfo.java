/*****************************************************************
 * Class : ColumnInfo.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park / jeros@epitomie.com
 *****************************************************************/

package org.sdf.rgb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sdf.secure.DBSecure;

public class ColumnInfo {
	List columns;
	HashMap cols;
	
	public ColumnInfo filter(String[] cols) {
		ColumnInfo info = new ColumnInfo();

		for (int i = 0; i < cols.length; i++) {
			Column col = info.getColumn(cols[i]);
			if (col == null)
				continue;
			info.addColumn((Column) col.clon());
		}
		return info;
	}

	protected boolean isSecure = false;
	protected HashMap secureCols;
	
	protected boolean isSecureColumn(String col){
		if(secureCols == null) return false;
		return secureCols.containsKey(col.toUpperCase());
	}
	
	public int make(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			DBSecure sec = DBSecure.getInstance();
			boolean b = sec.isSecure();
			
			
			int count = rsmd.getColumnCount();
			for (int i = 1; i <= count; i++) {
				Column col = new Column(rsmd.getColumnName(i), rsmd
						.getColumnLabel(i), rsmd.getColumnType(i));
				col.setSize(rsmd.getColumnDisplaySize(i));
				addColumn(col);


				// 2019.07.01 MariaDB?? ê²½ì?? name : ì»¬ë?¼ë?, label alias ë¡? ?¬ë?¼ì?¤ë??ë¡? label ?? ?¬ì?©í????ë¡? ë³?ê²?
				// Oracle ?? ê²½ì?? alias ê°? name, label ???¼í??ê²? ?¬ë?¼ì??.
				//String c = col.name.toUpperCase();
				String c = col.label.toUpperCase();

				if(cols == null) cols = new HashMap();
				cols.put(c, new Integer(i-1));
				if(b){
					if( sec.isSecureColumn(c)){
						if(secureCols == null) secureCols = new HashMap();
						//Log.biz.debug("Secure Column :" + col);
						secureCols.put(c, new Integer(i-1));
						isSecure = true;
					}
				}
				
				//Log.biz.info(">Column Secure Check:" + col.name + ":" + isSecureColumn(col.name));
			}
			return count;
		} catch (Exception e) {
		}
		return 0;
	}

	public void addColumn(String name) {
		addColumn(new Column(name));
	}

	public void addColumn(Column col) {
		if (columns == null)
			columns = new ArrayList();
		columns.add(col);

	}

	public Column getColumn(int index) {
		try {
			return ((Column) columns.get(index));
		} catch (Exception e) {
		}
		return null;
	}

	public Column getColumn(String name) {
		try {
			name = name.toUpperCase();
			Integer idx = (Integer) cols.get(name);
			if(idx == null) return null;
			/*
			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				if (col.equals(name))
					return col;
			}
			*/
			return (Column) columns.get(idx.intValue());
		} catch (Exception e) {
		}
		return null;
	}

	public int findColumn(String name) {
		try {
			name = name.toUpperCase();
			Integer idx = (Integer) cols.get(name);
			if(idx == null) return -1;
			return idx.intValue() + 1;
			
/*			
			int idx = 0;
			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				if (col.equals(name))
					return idx + 1;
				idx++;
			}
*/			
		} catch (Exception e) {
		}
		return -1;
	}

	public String[] getColumnLabels() {
		try {
			String[] labels = new String[getColumnCount()];

			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				labels[i] = col.getLabel();
			}
			return labels;
		} catch (Exception e) {
		}
		return null;
	}

	public String[] getColumnNamesByTypeOrder() {
		try {
			String[] names = new String[getColumnCount()];

			int idx = 0;
			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				if (col.type == Types.CLOB || col.type == Types.BLOB)
					continue;
				names[idx++] = col.getName();
			}
			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				if (col.type != Types.CLOB && col.type != Types.BLOB)
					continue;
				names[idx++] = col.getName();
			}
			return names;
		} catch (Exception e) {
		}
		return null;
	}

	public String[] getColumnLabelsByTypeOrder() {
		try {
			String[] names = new String[getColumnCount()];

			int idx = 0;
			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				if (col.type == Types.CLOB || col.type == Types.BLOB)
					continue;
				names[idx++] = col.getLabel();
			}
			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				if (col.type != Types.CLOB && col.type != Types.BLOB)
					continue;
				names[idx++] = col.getLabel();
			}
			return names;
		} catch (Exception e) {
		}
		return null;
	}

	public String[] getColumnNames() {
		try {
			String[] names = new String[getColumnCount()];

			for (int i = 0; i < columns.size(); i++) {
				Column col = (Column) columns.get(i);
				names[i] = col.getName();
			}
			return names;
		} catch (Exception e) {
		}
		return null;
	}

	public String getColumnName(int index) {
		try {
			return getColumn(index).getName();
		} catch (Exception e) {
		}
		return "";
	}

	public String getColumnLabel(int index) {
		try {
			return getColumn(index).getLabel();
		} catch (Exception e) {
		}
		return "";
	}

	public int getColumnSize(int index) {
		try {
			return getColumn(index).getSize();
		} catch (Exception e) {
		}
		return -1;
	}

	public int getColumnSize(String name) {
		try {
			return getColumn(name).getSize();
		} catch (Exception e) {
		}
		return -1;

	}

	public void setColumnLabels(String[] labels) {
		if (labels == null)
			return;
		for (int i = 0; i < labels.length; i++)
			setColumnLabel(i, labels[i]);
	}

	public void setColumnLabel(int index, String label) {
		try {
			((Column) columns.get(index)).setLabel(label);
		} catch (Exception e) {
		}

	}

	public void setColumnLabels(String[] names, String[] labels) {
		try {
			for (int i = 0; i < names.length; i++) {
				getColumn(names[i]).setLabel(labels[i]);
			}
		} catch (Exception e) {
		}
	}

	public void setColumnLabels(RecordSet rs, String name, String label) {
		while (rs.next()) {
			try {
				getColumn(rs.get(name)).setLabel(rs.get(label));
			} catch (Exception e) {
			}
		}
	}

	public void setColumnLabels(RecordSet rs, int name, int label) {
		while (rs.next()) {
			try {
				getColumn(rs.get(name)).setLabel(rs.get(label));
			} catch (Exception e) {
			}
		}
	}

	public void setColumnSize(int index, String size) {
		try {
			((Column) columns.get(index)).setSize(size);
		} catch (Exception e) {
		}
	}

	public int getColumnCount() {
		try {
			return columns.size();
		} catch (Exception e) {
		}
		return 0;
	}

	public void clear() {
		try {
			columns.clear();
			columns = null;
		} catch (Exception e) {
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		int count = 0;
		if (columns != null)
			count = columns.size();
		buf.append("Column Count : ").append(count).append("\n{ ");
		for (int i = 0; i < count; i++) {
			buf.append(getColumn(i)).append(" ");
		}
		buf.append("}");
		return buf.toString();
	}

}