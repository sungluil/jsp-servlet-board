package org.sdf.util;

public class Key {
	long time;
	int index;

	public Key() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		time = cal.getTime().getTime();
		time = (time - 1077113533045L) * 5;

		cal.set(2004, 2, 2, 0, 0, 0);
	}

	// 107362598956
	// (12??��E��cE�ˢ�EcE��E��cE�ˢ�Ec�ˢ碮��I��E��c����I��E��cE�ˢ�EcEc Key)
	public final String getKey() {
		return String.valueOf((time + index++));
	}

	// f9f91881c0 ( 10??��E��cE�ˢ�EcE��E��cE�ˢ�Ec�ˢ碮��I��E��c����I��E��cE�ˢ�EcEc
	// Key)
	public final String getHexKey() {
		return Long.toHexString((time + index++));
	}

	public static void main(String[] arg) {
		System.out.println(new Key().getHexKey());
	}
}