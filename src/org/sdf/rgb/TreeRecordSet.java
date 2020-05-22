package org.sdf.rgb;

import java.util.ArrayList;
import java.util.List;

public class TreeRecordSet {
	public RecordSet rs;
	
	public TreeRecordSet(RecordSet rs, String id, String pid){
		this.rs = rs;
	}
	
	List items = new ArrayList(); 
	
	private void parse(){
		/*
		for(int i=0; rs.next(); i++){
			if(!rs.valid(pid)) items.add(new Integer)
		}
		*/
	}
	
}
