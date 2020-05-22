package org.sdf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.*;

public class PropUtil {
	public static HashMap load(File f) throws Exception {
		BufferedReader r = null;
		HashMap map = new HashMap();

		try {

			r = new BufferedReader(new FileReader(f));

			String line = null;
			for (int i = 0; r.ready(); i++) {
				line = r.readLine();
				if (line == null)
					break;
				if (line.trim().length() == 0)
					continue;
				if (line.trim().charAt(0) == '#')
					continue;

				String[] arr = StringUtil.getArray(line, "= ");
				if (arr != null && arr.length > 0) {
					if (arr.length == 1)
						map.put(arr[0], null);
					else {
						String val = "";
						for (int j = 1; j < arr.length; j++) {
							if (j > 1)
								val += "=";
							val += arr[j];
						}
						map.put(arr[0], val);
					}
				}
			}

		} finally {
			try {
				r.close();
			} catch (Exception e) {
			}
		}

		return map;
	}
}
