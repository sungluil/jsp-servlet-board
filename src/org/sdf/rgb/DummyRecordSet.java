package org.sdf.rgb;

import java.util.List;

public class DummyRecordSet extends RecordSet{
	public void setColumnInfo(ColumnInfo cinfo){
		this.columnInfo = cinfo;
	}
	
	public void setValues(List rows){
		this.rows = rows;
	}
}
