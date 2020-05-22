package org.sdf.lang;

import org.sdf.rdb.RecordSet;

public class LocaleText {
	public String id;
	public String text;
	public String text_en;
	public String text_jp;
	public String text_cn;
	public String tag;
	public String name;
	public String type;

	public LocaleText() {
	}
	
	public LocaleText(RecordSet rs) {
		id = rs.get("txt_id");
		text = rs.get("txt_text");
		text_en = rs.get("txt_text_en");
		text_jp = rs.get("txt_text_jp");
		text_cn = rs.get("txt_text_cn");
		tag = rs.get("txt_tag");
	}

	public String getText(String cs) {
		if ("en".equals(cs)) {
			if (text_en.equals(""))
				return getText();
			return text_en;
		} else if ("jp".equals(cs)) {
			if (text_jp.equals(""))
				return getText();
			return text_jp;
		} else if ("cn".equals(cs)) {
			if (text_cn.equals(""))
				return getText();
			return text_cn;
		}

		return getText();
	}

	public String getTag(String cs) {
		if ("en".equals(cs))
			return tag + "_en";
		if ("jp".equals(cs))
			return tag + "_jp";
		if ("cn".equals(cs))
			return tag + "_cn";
		return tag;
	}

	public String getText() {
		if (text == null || text.equals(""))
			return id;
		return text;
	}

	public String getTag() {
		return tag;
	}

	public String toString() {
		return "ID:" + id + "\nKorea:" + text + "\nEnglish:" + text_en
				+ "\nChina:" + text_cn + "\nJapan:" + text_jp + "\nTag:" + tag;
	}
}
