package org.sdf.rgb;

import java.util.ArrayList;
import java.util.List;

import org.sdf.log.Log;

public class FRecordSet extends RecordSet {
	public FRecordSet(RecordSet rs, String col, String val) {
		try {
			this.columnInfo = rs.getColumnInfo();

			this.rows = new ArrayList();

			List l = rs.getList();

			int idx = rs.findColumn(col) - 1;
			int cnt = 0;

			if (idx < 0)
				return;
			for (int i = 0; i < l.size(); i++) {
				String[] d = (String[]) l.get(i);

				if (!d[idx].equals(val)) {
					continue;
				}
				// Log.act.info(d[idx] + ":"+ val);
				this.rows.add(d);
				cnt++;
			}
			this.total = cnt;
		} catch (Exception e) {
			Log.sql.err("FilterRecordSet:", e);
		}
	}

	public void clear() {

	}
}
