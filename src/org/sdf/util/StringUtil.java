/*****************************************************************
 * Class : StringUtil.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park / jeros@epitomie.com
 *****************************************************************/

package org.sdf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.sdf.servlet.HttpUtility;
import org.sdf.slim.config.GlobalResource;

public class StringUtil {

	final public synchronized static String getLine(BufferedReader r)
			throws java.io.IOException {
		while (r.ready()) {
			String line = r.readLine();
			if (line == null)
				break;
			if (line.trim().length() == 0 || line.trim().charAt(0) == '#') {
				continue;
			}
			return line;
		}
		return null;
	}

	public synchronized static String[] getArrays(String str, String delim) {
		return StringUtil.getArrays(str, delim, false);
	}

	public synchronized static String[] getArrays(String str, String delim,
			boolean null_skip) {
		List l = StringUtil.splits(str, delim, null_skip);

		String[] arr = new String[l.size()];
		l.toArray(arr);
		return arr;
	}

	public synchronized static List splits(String str, String delim) {
		return StringUtil.splits(str, delim, false);
	}

	public synchronized static List splits(String str, String delim,
			boolean null_skip) {
		if (StringUtil.invalid(str))
			return new ArrayList();
		if (StringUtil.invalid(delim)) {
			List l = new ArrayList();
			l.add(str);
			return l;
		}

		List l = new ArrayList();
		int len = delim.length();
		int offset = 0;

		for (;;) {
			int idx = str.indexOf(delim, offset);
			if (idx == -1)
				break;
			String s = str.substring(offset, idx);
			s = s.trim();
			offset = idx + len;
			if (null_skip && s.equals(""))
				continue;
			l.add(s);
		}
		if (offset < str.length()) {
			String s = str.substring(offset);
			s = s.trim();
			if (!null_skip || !s.equals("")) {
				l.add(s);
			}
		}
		return l;
	}

	public synchronized static String[] getArray(String line, String delim) {
		Vector v = getVector(line, delim);
		int size = v.size();
		if (size == 0)
			return new String[0];

		String[] array = new String[v.size()];
		v.copyInto(array);
		return array;
	}

	public synchronized static Vector getVector(String line, String delim) {
		StringTokenizer st = new StringTokenizer(line, delim);
		Vector v = new Vector();
		while (st.hasMoreTokens()) {
			v.addElement(st.nextToken().trim());
		}
		return v;
	}

	public synchronized static String[] getArray(String line, char delim) {
		return getArray(line, delim, true);
	}

	public synchronized static String[] getArray(String line, char delim,
			boolean trim) {
		Vector v = getVector(line, delim, trim);
		int size = v.size();
		if (size == 0)
			return new String[0];

		String[] array = new String[v.size()];
		v.copyInto(array);
		return array;
	}

	public synchronized static Vector getVector(String line, char delim) {

		return getVector(line, delim, true);
	}

	public synchronized static Vector getVector(String line, char delim,
			boolean trim) {
		int len = line.length();
		Vector v = new Vector();
		if (len == 0)
			return v;

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < len; i++) {
			char c = line.charAt(i);
			if (c == delim) {
				v.addElement(buf.toString().trim());
				// if( i == len -1 ) break;
				buf = new StringBuffer();
			} else
				buf.append(c);
		}

		if (!trim)
			v.addElement(buf.toString());
		else
			v.addElement(buf.toString().trim());

		return v;
	}

	public synchronized static String fillZero(String val, int size) {
		int s = size - val.length();

		for (int i = 0; i < s; i++)
			val = "0" + val;

		return val;
	}

	public synchronized static String fillZero(long val, int size) {
		return fillZero(String.valueOf(val), size);
	}

	public synchronized static String fillZeroRight(String val, int size) {

		int s = size - val.length();

		for (int i = 0; i < s; i++)
			val = val + "0";

		return val;
	}

	public synchronized static String round(double val, int size) {
		double d = Math.pow(10, size);
		return String.valueOf(((long) (val * d)) / d);
	}

	public synchronized static boolean valid(String s) {
		return s != null && !s.trim().equals("");
	}

	public synchronized static boolean invalid(String s) {
		return s == null || s.trim().equals("");
	}

	public synchronized static String str2html(String s) {
		if (s == null)
			return "";
		return replace(s, "\n", "<br>");
	}

	public synchronized static String replace(String str, String oldstr,
			String newstr) {
		if (!StringUtil.valid(str)) return "";

		if( !StringUtil.valid(oldstr)  || newstr == null  )
			return str;
		StringBuffer buf = new StringBuffer();
		int savedpos = 0;
		while (true) {
			int pos = str.indexOf(oldstr, savedpos);
			if (pos != -1) {
				buf.append(str.substring(savedpos, pos));
				buf.append(newstr);

				savedpos = pos + oldstr.length();
				if (savedpos >= str.length())
					break;
			} else
				break;
		}
		buf.append(str.substring(savedpos, str.length()));
		return buf.toString();
	}

	public synchronized static long hex2rgb(String hex) {
		hex = hex.toUpperCase();
		// System.out.print( hex);
		String r = hex.substring(0, 2);
		String g = hex.substring(2, 4);
		String b = hex.substring(4);

		long rgb = hex2dec(r.charAt(0)) * 16 + hex2dec(r.charAt(1));
		// System.out.print( "> rgb : " + rgb);
		rgb += (hex2dec(g.charAt(0)) * 16 + hex2dec(g.charAt(1))) * 16 * 16;
		// System.out.print( ", rgb : " + rgb);

		rgb += (hex2dec(b.charAt(0)) * 16 + hex2dec(b.charAt(1))) * 16 * 16
				* 16 * 16;
		// System.out.println( ", rgb : " + rgb);

		return rgb;
	}

	public synchronized static int hex2dec(char c) {
		int n;
		if (c >= 'A')
			n = 10 + (c - 'A');
		else
			n = c - '0';
		// System.out.println( c + " : " + n);
		return n;
	}

	public synchronized static String cut(String s, int size) {
		return StringUtil.cut(s, size, "..");
	}

	public synchronized static String cut2(String s, int size) {
		return StringUtil.cut(s, size, "..");
	}

	public synchronized static String cut(String s, int size, String att) {
		if (s == null)
			return "";
		if (s.length() > size)
			return s.substring(0, size) + att;
		return s;
	}

	public synchronized static String getFileContents(String file) {
		return getFileContents(new File(file));
	}

	public synchronized static String getFileContents(File file) {
		BufferedReader r = null;
		StringBuffer buf = new StringBuffer();
		try {
			r = new BufferedReader(new FileReader(file));
			while (r.ready()) {
				String line = r.readLine();
				if (line == null)
					break;
				buf.append(line).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				r.close();
			} catch (Exception e) {
			}
		}
		return buf.toString();
	}

	public static String han2eng(String passStr) {
		if (passStr == null)
			return "";

		try {
			return new String(passStr.getBytes("KSC5601"), "8859_1");
		} catch (java.io.UnsupportedEncodingException e) {
			System.err.println(e.toString());
			return e.toString();
		}
	}

	public static String iso(String s) {
		return StringUtil.han2eng(s);
	}

	public static String eng2han(String passStr) {
		if (passStr == null)
			return "";
		try {
			return new String(passStr.getBytes("8859_1"), "KSC5601");
		} catch (java.io.UnsupportedEncodingException e) {
			return e.toString();
		}
	}

	/*
	 * public static void main( String[] args){ System.out.println(
	 * StringUtil.hex2rgb( "ff0000")); System.out.println( StringUtil.hex2rgb(
	 * "808000")); System.out.println( StringUtil.hex2rgb( "ffff00"));
	 * System.out.println( StringUtil.hex2rgb( "87ceeb")); System.out.println(
	 * StringUtil.hex2rgb( "008000")); System.out.println( StringUtil.hex2rgb(
	 * "0000ff")); }
	 */

	public static String toHtml(String s) {
		if (s == null)
			return null;
		StringBuffer stringbuffer = new StringBuffer();
		char ac[] = s.toCharArray();
		int i = ac.length;
		for (int j = 0; j < i; j++)
			if (ac[j] == '&')
				stringbuffer.append("&amp;");
			else if (ac[j] == '<')
				stringbuffer.append("&lt;");
			else if (ac[j] == '>')
				stringbuffer.append("&gt;");
			else if (ac[j] == '"')
				stringbuffer.append("&quot;");
			else if (ac[j] == '\'')
				stringbuffer.append("&#039;");
			else
				stringbuffer.append(ac[j]);

		return stringbuffer.toString();
	}

	/**
	 * Method cropByte. 문자열 바이트수만큼 끊어주고, 생략표시하기
	 *
	 * @param str
	 *            문자열
	 * @param i
	 *            바이트수
	 * @param trail
	 *            생략 문자열. 예) "..."
	 * @return String
	 */
	public static String cropByte(String str, int i, String trail) {
		if (str == null)
			return "";
		String tmp = str;
		int slen = 0, blen = 0;
		char c;
		try {
			if (tmp.getBytes("MS949").length > i) {
				while (blen + 1 < i) {
					c = tmp.charAt(slen);
					blen++;
					slen++;
					if (c > 127)
						blen++; // 2-byte character..
				}
				tmp = tmp.substring(0, slen) + trail;
			}
		} catch (java.io.UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding:" + str);
		}
		return tmp;
	}

	public static String fix(String v) {
		if (v == null)
			return v;
		return HttpUtility.translate(v);
	}

	public static String currency(long n) {
		String s = String.valueOf(n);
		String cs = "";
		int len = s.length();
		for (int i = 0; i < len; i++) {
			// System.out.println( len + ":" + i + " = " + (len-i));
			char c = s.charAt(len - i - 1);
			if (i != 0 && i % 3 == 0)
				cs = ',' + cs;
			cs = c + cs;
		}
		return cs;
	}

	public static String nvl(String v, String r) {
		if (!StringUtil.valid(v))
			return r;
		return v;
	}

	/**
	 * @param tobetested
	 *            검사할 문자열
	 */
	public final static boolean isEmpty(final String tobetested) {
		if ((tobetested != null) && (!tobetested.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/**
	 * 문자열이 null이거나 빈문자열인지를 검사하여 , 만약 비었다면 기본 문자열을 리턴
	 *
	 * @param tobetested
	 *            원래 문자열
	 */
	public final static String getString(final Object tobetested,
			final String emptyString) {
		if (tobetested != null) {
			return tobetested.toString().intern();
		}
		return emptyString;
	}

	/**
	 * 문자열이 null이거나 빈문자열인지를 검사하여 , 만약 비었다면 기본 문자열을 리턴
	 *
	 * @param tobetested
	 *            원래 문자열
	 */
	public final static String getString(final String tobetested,
			final String emptyString) {
		if ((tobetested != null) && (!tobetested.trim().equals(""))) {
			return tobetested.intern();
		}
		return emptyString;
	}

	/**
	 * 문자열이 null이거나 빈문자열인지를 검사하여 , 만약 비었다면 기본 값을 리턴
	 *
	 * @param tobetested
	 *            원래 문자열
	 */
	public final static int parseInt(final String tobetested, final int defValue) {
		if ((tobetested != null) && (!tobetested.trim().equals(""))) {
			try {
				return Integer.parseInt(tobetested);
			} catch (Exception e) {
				// DO NOTHING return defValue
			}
		}
		return defValue;
	}

	/**
	 * true True TRUE yes Yes YES 모두 true return 문자열이 null이거나 빈문자열인지를 검사하여 , 만약
	 * 비었다면 기본 값을 리턴
	 *
	 * @param tobetested
	 *            원래 문자열
	 */
	public final static boolean parseBoolean(final String tobetested,
			final boolean defValue) {
		if ((tobetested != null) && (!tobetested.trim().equals(""))) {
			String str = tobetested.trim().toLowerCase();
			if (str.equals("true") || str.equals("yes")) {
				return true;
			} else {
				return false;
			}
		}

		return defValue;
	}

	/**
	 * 문자열이 null이거나 빈문자열인지를 검사하여 , 만약 비었다면 기본 값을 리턴
	 *
	 * @param tobetested
	 *            원래 문자열
	 */
	public final static long parseLong(final String tobetested,
			final long defValue) {
		if ((tobetested != null) && (!tobetested.trim().equals(""))) {
			try {
				return Long.parseLong(tobetested);
			} catch (Exception e) {
				// DO NOTHING return defValue
			}
		}
		return defValue;
	}

	/**
	 * 문자열이 null이거나 빈문자열인지를 검사하여 , 만약 비었다면 기본 값을 리턴
	 *
	 * @param tobetested
	 *            원래 문자열
	 */
	public final static float parseFloat(final String tobetested,
			final float defValue) {
		if ((tobetested != null) && (!tobetested.trim().equals(""))) {
			try {
				return Float.parseFloat(tobetested);
			} catch (Exception e) {
				// DO NOTHING return defValue
			}
		}
		return defValue;
	}

	/**
	 * 문자열이 null이거나 빈문자열인지를 검사하여 , 만약 비었다면 기본 값을 리턴
	 *
	 * @param tobetested
	 *            원래 문자열
	 */
	public final static short parseShort(final String tobetested,
			final short defValue) {
		if ((tobetested != null) && (!tobetested.trim().equals(""))) {
			try {
				return Short.parseShort(tobetested);
			} catch (Exception e) {
				// DO NOTHING return defValue
			}
		}
		return defValue;
	}

	/**
	 * JEUS와 TOMCAT모두 사용하기 위해 생성함
	 *
	 * @param src
	 * @param sc
	 * @return
	 * @throws Exception
	 */
	public static String getExcelFileName(String src, ServletContext sc)
			throws Exception {
		GlobalResource gr = GlobalResource.getInstance();
		String was = gr.get("was.filename");
		if (was != null && was.toLowerCase().equals("other")) {

		} else {
			src = new String(src.getBytes("KSC5601"), "8859_1");
		}
		/*
		 * if(sc.getServerInfo().indexOf("Tomcat") > -1) { src = new
		 * String(src.getBytes("KSC5601"),"8859_1"); }
		 */
		return src;
	}

	public static String strCut(String szText, int nLength, int nPrev,
			boolean isNotag) { // 문자열 자르기

		String r_val = szText;
		int oF = 0, oL = 0, rF = 0, rL = 0;
		int nLengthPrev = 0;

		Pattern p = Pattern.compile(
				"<(/?)([^<>]*)?>", Pattern.CASE_INSENSITIVE); // 태그제거
																				// 패턴

		if (isNotag) {
			r_val = p.matcher(r_val).replaceAll("");
		} // 태그 제거
		r_val = r_val.replaceAll("&amp;", "&");
		r_val = r_val.replaceAll("(!/|\r|\n|&nbsp;)", ""); // 공백제거

		try {
			byte[] bytes = r_val.getBytes("UTF-8"); // 바이트로 보관

			// x부터 y길이만큼 잘라낸다. 한글안깨지게.
			nLengthPrev = nPrev;
			int j = 0;

			if (nLengthPrev > 0)
				while (j < bytes.length) {
					if ((bytes[j] & 0x80) != 0) {
						oF += 2;
						rF += 3;
						if (oF + 2 > nLengthPrev) {
							break;
						}
						j += 3;
					} else {
						if (oF + 1 > nLengthPrev) {
							break;
						}
						++oF;
						++rF;
						++j;
					}
				}

			j = rF;

			while (j < bytes.length) {
				if ((bytes[j] & 0x80) != 0) {
					if (oL + 2 > nLength) {
						break;
					}
					oL += 2;
					rL += 3;
					j += 3;
				} else {
					if (oL + 1 > nLength) {
						break;
					}
					++oL;
					++rL;
					++j;
				}
			}

			r_val = new String(bytes, rF, rL, "UTF-8"); // charset 옵션

		} catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return r_val;
	}

	public static String getArgsInput(String str) {
		StringBuffer buf = new StringBuffer();
		String[] arr = StringUtil.getArray(str, '&');
		for (int i = 0; i < arr.length; i++) {
			String[] s = StringUtil.getArray(arr[i], '=');
			if (s.length != 2)
				continue;
			buf.append("<input type=hidden name=" + s[0] + " value=\""
					+ HttpUtility.translate(s[1]) + "\">");
		}
		return buf.toString();
	}

	public static String[] getArray(List l) {
		if (l == null)
			return new String[0];
		String[] arr = new String[l.size()];
		l.toArray(arr);
		return arr;
	}

	public static String stripScript(String content) {
		if (!StringUtil.valid(content))
			return "";
		Pattern SCRIPTS = Pattern.compile(
				"<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);

		Matcher m;

		m = SCRIPTS.matcher(content);
		content = m.replaceAll("");

		return content;
	}

	public static String stripTag(String content) {
		if (!StringUtil.valid(content))
			return "";

		Pattern SCRIPTS = Pattern.compile(
				"<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
		Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>",
				Pattern.DOTALL);
		Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		Pattern nTAGS = Pattern.compile("<\\w+\\s+[^<]*\\s*>");
		Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
		Pattern WHITESPACE = Pattern.compile("\\s\\s+");

		Matcher m;

		m = SCRIPTS.matcher(content);
		content = m.replaceAll("");
		m = STYLE.matcher(content);
		content = m.replaceAll("");
		m = TAGS.matcher(content);
		content = m.replaceAll("");
		m = ENTITY_REFS.matcher(content);
		content = m.replaceAll("");
		m = WHITESPACE.matcher(content);
		content = m.replaceAll(" ");

		return content;
	}

	public static void main(String[] args) {
		String req_content = "123<p><img width=\"100\" src=\"adsadsdasddasdsadsadasdsadasd\"></p>456";
		req_content += "<p>안녕하세요</p><p>&nbsp;</p><p>P2017-00113: IN 건에 특허대리인이 번역문 업로드를 하려 하는데</p>"
				+ "<div><span style=\"font-family: Gulim;\">AA</span> </div><p>&nbsp;</p><div>시스템에 번역문 업로드를 위한 클릭 버튼이 활성화 되지 않아 업로드를 하지 못하고 있습니다. </div><p>&nbsp;</p><div><span style=\"font-family: Gulim;\"></span> </div><p>&nbsp;</p><div>활성화 요청 드립니다.</div><div><span style=\"font-family: Gulim;\">?</span></div><div><span style=\"font-family: Gulim;\"><font face=\"돋움\">감사합니다. </font>?</span></div>";


	    Pattern SCRIPTS =Pattern.compile("<img(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
	    Pattern STYLE =Pattern.compile("<span(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		//Pattern STYLE = Pattern.compile("<(no)?span[^>]*>.*?</(no)?>", Pattern.DOTALL);
	    //Pattern.compile("<(no)?img[^>]*>.*?</(no)?>", Pattern.DOTALL);
		Matcher m;

		m = SCRIPTS.matcher(req_content);
		req_content = m.replaceAll("");

		m = STYLE.matcher(req_content);
		req_content = m.replaceAll("");
		try {
			System.out.println("m : "+m.start());
		} catch (Exception e) {
			// TODO: handle exception
		}



		System.out.println("req_content2 : "+req_content);

	}

	public static String fixValue(String v) {
		StringBuffer buf = new StringBuffer();
		if (v == null)
			return "";
		int len = v.length();
		for (int i = 0; i < len; i++) {
			char c = v.charAt(i);
			if (c == '\"') {
				buf.append('\\').append('\"');
				continue;
			}
			if (c == '\n') {
				buf.append('\\').append('n');
				continue;
			}
			if (c == '\r') {
				continue;
			}
			buf.append(c);
		}

		return buf.toString();
	}
}