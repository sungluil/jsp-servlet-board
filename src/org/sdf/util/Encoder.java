package org.sdf.util;

public class Encoder {
	static Encoder enc = new Encoder();
	static Encoder encko = new KoEncoder();
	static Encoder encen = new EnEncoder();

	public String encode(String v) {
		return v;
	}

	public static Encoder getEncoder(String locale) {
		if (locale.equals("ko"))
			return Encoder.encko;
		else if (locale.equals("en"))
			return Encoder.encen;
		return Encoder.enc;
	}
}

class KoEncoder extends Encoder {
	public String encode(String v) {
		String s = null;
		try {
			s = new String(v.getBytes("KSC5601"), "utf-8");
		} catch (Exception e) {
		}
		System.out.println(v + ":" + s);
		return s;
	}
}

class EnEncoder extends Encoder {
	public String encode(String v) {
		try {
			v = new String(v.getBytes("8859_1"), "utf-8");
		} catch (Exception e) {
		}
		return v;
	}
}
