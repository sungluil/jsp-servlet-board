package org.sdf.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.*;

/**
 * <PRE>
 * 프로젝트명    : Help Desk 확대 구축
 * 프로그램개요 : 
 * 관련 테이블   : 
 * 클래스명 	    : FileUtil
 * 작성자 	        : 박성호
 * 작성일 	        : 2005. 6. 14.
 * 비고 		        : 
 * 개정이력  	    : 2005. 6. 14. 박성호, v1.0, 최초작성
 * 
 * 
 * </PRE>
 */

public class FileUtil {
	public static HashMap loadProperties(String fname) {

		BufferedReader r = null;
		try {
			File f = new File(fname);
			r = new BufferedReader(new FileReader(f));
			HashMap map = new HashMap();
			while (r.ready()) {
				String line = r.readLine();
				if (line == null)
					break;
				line = line.trim();
				if (line.length() == 0)
					continue;
				char c = line.charAt(0);
				if (c == '#')
					continue;

				String[] arr = StringUtil.getArray(line, '=', true);

				// System.out.println(arr.length);
				map.put(arr[0], arr[1]);
			}
			return map;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				r.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	long lastModifed = 0L;
	String fname;
	HashMap map;

	public boolean isModified() {
		File file = new File(fname);
		long time = file.lastModified();
		return (time > lastModifed);
	}

	public HashMap load() {
		if (map == null || isModified()) {
			map = FileUtil.loadProperties(fname);
		}

		return map;
	}

	public FileUtil(String fname) {
		this.fname = fname;
	}
}
