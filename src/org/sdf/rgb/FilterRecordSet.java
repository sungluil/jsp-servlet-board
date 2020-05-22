package org.sdf.rgb;

import java.util.ArrayList;
import java.util.List;

import org.sdf.log.Log;

public class FilterRecordSet extends RecordSet {
	public FilterRecordSet(RecordSet rs, String col, String val) {

		this.columnInfo = rs.getColumnInfo();

		this.rows = new ArrayList();
		String[] record = new String[this.columnInfo.getColumnCount()];
		List l = rs.getList();
		Log.act.info("S RecordSet :" + l.size());
		int idx = rs.findColumn(col) - 1;
		int cnt = 0;
		for (int i = 0; i < l.size(); i++) {

			String[] d = (String[]) l.get(i);
			Log.act.info(d[idx] + ":" + val);
			if (!d[idx].equals(val)) {
				continue;
			}

			this.rows.add(d);
			cnt++;
		}
		this.total = cnt;

	}
}
