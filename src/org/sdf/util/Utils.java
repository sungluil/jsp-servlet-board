package org.sdf.util;

import java.net.URLEncoder;

import javax.servlet.http.HttpSession;

import org.sdf.lang.Data;
import org.sdf.lang.IData;
import org.sdf.log.Log;
import org.sdf.servlet.Box;

public class Utils {
	public static String transVars(String str, Box box, HttpSession session,
			Data row) {
		if (str == null || str.equals(""))
			return "";
		try {
			String nstr = str;
			int cidx = 0;
			for (int i = 0;; i++) {
				int idx = str.indexOf("#{", cidx);
				if (idx < 0)
					break;
				int midx = str.indexOf(".", idx + 2);
				cidx = str.indexOf("}", midx);
				String fstr = str.substring(idx, cidx + 1);
				String type = str.substring(idx + 2, midx);
				String key = str.substring(midx + 1, cidx);
				if (type.equals("box")) {
					String v = getKeyData(box, key);
					// Log.act.info("box_v : "+v+" - key : "+key);
					nstr = StringUtil.replace(nstr, fstr, v);
				} else if (type.equals("row")) {
					String v = getKeyData(row, key);
					// Log.act.info("row_v : "+v);
					nstr = StringUtil.replace(nstr, fstr, v);
				} else if (type.equals("session")) {
					if (key.startsWith("user")) {
						Data user = (Data) session.getAttribute("egene.user");
						if (user == null)
							continue;
						int kidx = key.indexOf('.');
						key = key.substring(kidx + 1);
						String v = user.get(key);
						nstr = StringUtil.replace(nstr, fstr, v);
					} else {
						nstr = StringUtil.replace(nstr, fstr,
								(String) session.getAttribute(key));
					}
				}
				// Log.act.info(nstr + ":" + fstr +":"+ box.get(key));
			}
			return nstr;
		} catch (Exception e) {
			Log.biz.err("TransVars() : [" + str + "]", e);
		}
		return str;
	}

	public static String transVars(String str, Box box, HttpSession session,
			Data row, String cset) {
		if (str == null || str.equals(""))
			return "";
		try {
			String nstr = str;
			int cidx = 0;
			for (int i = 0;; i++) {
				int idx = str.indexOf("#{", cidx);
				if (idx < 0)
					break;
				int midx = str.indexOf(".", idx + 2);
				cidx = str.indexOf("}", midx);
				String fstr = str.substring(idx, cidx + 1);
				String type = str.substring(idx + 2, midx);
				String key = str.substring(midx + 1, cidx);
				if (type.equals("box")) {
					if (box == null)
						continue;
					String v = getKeyData(box, key);

					nstr = StringUtil.replace(nstr, fstr,
							URLEncoder.encode(v, cset));
				} else if (type.equals("row")) {
					if (row == null)
						continue;
					String v = getKeyData(row, key);
					nstr = StringUtil.replace(nstr, fstr,
							URLEncoder.encode(v, cset));
				} else if (type.equals("session")) {
					if (key.startsWith("user")) {
						Data user = (Data) session.getAttribute("egene.user");
						if (user == null)
							continue;
						int kidx = key.indexOf('.');
						key = key.substring(kidx + 1);
						String v = user.get(key);
						nstr = StringUtil.replace(nstr, fstr,
								URLEncoder.encode(v, cset));
					} else {
						nstr = StringUtil.replace(nstr, fstr,
								URLEncoder.encode((String) session
										.getAttribute(key), cset));
					}
				}
				// System.out.println(nstr + ":" + fstr +":"+ box.get(key));
			}
			return nstr;
		} catch (Exception e) {
			Log.biz.err("TransVars() : [" + str + "]", e);
		}
		return str;
	}

	public static String transVars(String str, Box box, HttpSession session) {
		if (str == null || str.equals(""))
			return "";
		try {
			String nstr = str;
			int cidx = 0;
			for (int i = 0;; i++) {
				int idx = str.indexOf("#{", cidx);
				if (idx < 0)
					break;
				int midx = str.indexOf(".", idx + 2);
				cidx = str.indexOf("}", midx);
				String fstr = str.substring(idx, cidx + 1);
				String type = str.substring(idx + 2, midx);
				String key = str.substring(midx + 1, cidx);
				if (type.equals("box")) {
					if (box == null)
						continue;
					String v = getKeyData(box, key);
					nstr = StringUtil.replace(nstr, fstr, v);

				} else if (type.equals("session")) {
					if (key.startsWith("user")) {
						Data user = (Data) session.getAttribute("egene.user");
						if (user == null)
							continue;
						int kidx = key.indexOf('.');
						key = key.substring(kidx + 1);
						String v = user.get(key);
						nstr = StringUtil.replace(nstr, fstr, v);
					} else if (type.equals("session")) {
						if (key.startsWith("user")) {
							Data user = (Data) session
									.getAttribute("egene.user");
							if (user == null)
								continue;
							int kidx = key.indexOf('.');
							key = key.substring(kidx + 1);
							String v = user.get(key);
							nstr = StringUtil.replace(nstr, fstr,
									v);
						} else {
							nstr = StringUtil.replace(nstr, fstr,
									(String) session.getAttribute(key));
						}
					}
				}
				// System.out.println(nstr + ":" + fstr +":"+ box.get(key));
			}
			return nstr;
		} catch (Exception e) {
			Log.biz.err("TransVars() : [" + str + "]", e);
		}
		return str;
	}

	public static String transVars(String str, IData data) {
		if (str == null || str.equals(""))
			return "";
		if (data == null)
			return "";
		try {
			String nstr = str;
			int cidx = 0;
			for (int i = 0;; i++) {
				int idx = str.indexOf("#{", cidx);
				if (idx < 0)
					break;

				cidx = str.indexOf("}", idx);
				String fstr = str.substring(idx, cidx + 1);
				String key = str.substring(idx + 2, cidx);

				String v = data.get(key);
				if (!StringUtil.valid(v)) {
					int n = key.indexOf('/');
					if (n > 0)
						key = key.substring(n + 1);
					v = data.get(key);
				}
				nstr = StringUtil.replace(nstr, fstr, v);

			}
			return nstr;
		} catch (Exception e) {
			Log.biz.err("TransVars() : [" + str + "]", e);
		}
		return str;
	}

	public static String getKeyData(IData data, String key) {
		if (data == null)
			return "";
		String v = data.get(key);
		if (!StringUtil.valid(v)) {
			int n = key.indexOf('/');
			if (n > 0)
				key = key.substring(n + 1);
			key = key.toLowerCase();
			v = data.get(key);
		}
		return v;
	}

	public static Data getDatafromQuery(String s) {
		Data data = new Data();
		String[] params = StringUtil.getArray(s, '&');
		for (int i = 0; i < params.length; i++) {
			String param = params[i];
			int idx = param.indexOf('=');
			if (idx == -1)
				continue;
			String key = param.substring(0, idx - 1);
			String val = param.substring(idx + 1);
			data.put(key, val);
		}
		return data;
	}

	public static String encode(String s, String charset) {
		if (charset == null || charset.equals(""))
			return s;
		try {
			StringBuffer buf = new StringBuffer();
			String[] arr = StringUtil.getArray(s, '&');
			for (int i = 0; i < arr.length; i++) {
				if (i > 0)
					buf.append('&');
				String[] t = StringUtil.getArray(arr[i], '=');
				if (t.length > 0)
					buf.append(t[0]).append('=');
				if (t.length > 1) {
					StringBuffer b = new StringBuffer();
					for (int j = 1; j < t.length; j++) {
						if (j > 1)
							b.append('=');
						b.append(t[j]);
					}
					buf.append(URLEncoder.encode(b.toString(), charset));
				}

			}
			return buf.toString();
		} catch (Exception e) {
		}
		return s;
	}

	public static void main(String[] args) {
		//String param = "aaa=123&con=한글 테스트&ccc=1234";
		double d = 127.564;

		System.out.println(round(d, 1000));
	}

	public synchronized static String round(double val, int size) {
		if (val < 0)
			return "-";
		double d = (double) Math.round(val * size) / size;
		//System.out.println(d);
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.####");
		return df.format(d);
	}
}
