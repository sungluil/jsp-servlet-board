package org.sdf.lang;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.steg.efc.User;

public class Label {
	public String en;
	public String jp;
	public String cn;
	public String kr;

	public Label(String prefix, IData d) {
		kr = d.get(prefix + "_label");
		en = d.get(prefix + "_label_en");
		jp = d.get(prefix + "_label_jp");
		cn = d.get(prefix + "_label_cn");
	}

	public Label(JSONObject d) {
		kr = d.getString("label");
		en = d.getString("label_en");
		jp = d.getString("label_jp");
		cn = d.getString("label_cn");
	}

	public String getLabel(String cs) {
		if ("en".equals(cs))
			return en;
		if ("jp".equals(cs))
			return jp;
		if ("cn".equals(cs))
			return cn;
		return kr;
	}

	public String getLabel(HttpSession session) {
		if (session == null)
			return getLabel();
		User user = (User) session.getAttribute("egene.user");
		if (user == null)
			return getLabel();
		return getLabel(user.charset);
	}

	public String getLabel() {
		return kr;
	}

	public String toString() {
		return "[" + kr + "][" + en + "][" + jp + "][" + cn + "]";
	}
}
