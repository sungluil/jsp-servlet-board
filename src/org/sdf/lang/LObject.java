package org.sdf.lang;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

public class LObject {
	public Label labels;

	public void initLabel(String prefix, IData d) {
		labels = new Label(prefix, d);
	}

	public void initLabel(JSONObject d) {
		labels = new Label(d);
	}

	public String getLabel() {
		if (labels != null)
			return labels.getLabel();
		return "";
	}

	public String getLabel(HttpSession session) {
		if (labels != null)
			return labels.getLabel(session);
		return "";
	}

	public String getLabel(String cs) {
		if (labels != null)
			return labels.getLabel(cs);
		return "";
	}

}
