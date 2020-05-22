package org.sdf.util;

import java.io.*;
import java.util.*;

public class FileInfo {
	public String dir;
	public String name;
	public String ext;
	public String key;
	int idx = -1;

	public FileInfo(String name, String key, String ext, String dir) {
		this.name = name;
		this.ext = ext;
		this.dir = dir;
		this.key = key;
	}

	PrintWriter w;

	public PrintWriter getWriter() throws Exception {
		if (w == null) {

			Filename fn = new Filename();

			if (idx == 0) {
				fn.org_name = name;
				fn.real_name = key;
			} else {
				fn.org_name = name + "[" + idx + "]";
				fn.real_name = key + "[" + idx + "]";
			}
			fn.org_name += "." + ext;
			fn.real_name += "." + ext;
			File f = new File(dir, fn.real_name);
			fn.f = f;

			w = new PrintWriter(new BufferedWriter(new FileWriter(f)));

			fnames.add(fn);
		}
		return w;
	}

	public Filename[] getFilenames() {
		Filename[] fn = new Filename[fnames.size()];
		fnames.toArray(fn);
		return fn;
	}

	public void inc() {
		idx++;
		try {
			w.close();
		} catch (Exception e) {
		}
		;
		w = null;
	}

	List fnames = new ArrayList();

	public class Filename {
		public File f;
		public String org_name;
		public String real_name;

	}
}
