package org.sdf.rgb;

class MethodInfo {
	int level = 3;

	public MethodInfo() {
	}

	public MethodInfo(int level) {
		this.level = level;
	}

	String getInfo() {
		Exception ex = new Exception();
		String info = "";
		java.io.PrintWriter w = null;
		java.io.BufferedReader r = null;
		java.io.StringWriter s = null;
		try {
			s = new java.io.StringWriter();
			w = new java.io.PrintWriter(s);
			ex.printStackTrace(w);
			r = new java.io.BufferedReader(new java.io.StringReader(s
					.toString()));
			for (int i = 0; i < level; i++)
				r.readLine();
			String str = r.readLine().trim();
			info = str.substring(3);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				r.close();
			} catch (Exception e) {
			}
			try {
				w.close();
			} catch (Exception e) {
			}
		}
		return info;
	}

}