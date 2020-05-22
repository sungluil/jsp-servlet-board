package org.sdf.lang;

import java.text.SimpleDateFormat;
import java.util.*;

public class Time {
	public int yy;
	public int mm;
	public int dd;
	public int hh;
	public int mi;
	public int ss;
	public int wday;

	public String dt;
	public String tm;

	public int hhmiss;
	public int yymmdd;

	public Calendar cal;

	public long duration; // 해당일자 00:00:00 ~ 해당시간까지의 총 mili-Second
	public long time;

	public Time() {
		cal = Calendar.getInstance();
		setCalendar(cal);
	}

	public Time(long time) {
		setTime(time);
	}

	public Time(Calendar cal) {
		setCalendar(cal);
	}

	public Time(String dttm) {
		cal = Calendar.getInstance();
		set(dttm);

	}

	protected void validate() {

		wday = cal.get(Calendar.DAY_OF_WEEK);
		time = cal.getTime().getTime();
		Calendar tcal = Calendar.getInstance();

		tcal.set(yy, mm - 1, dd, 0, 0, 0);

		duration = (time - tcal.getTime().getTime());

	}

	public void setDate(String time) {

		try {
			yy = Integer.parseInt(time.substring(0, 4));
		} catch (Exception e) {
		}
		try {
			mm = Integer.parseInt(time.substring(4, 6));
		} catch (Exception e) {
		}
		try {
			dd = Integer.parseInt(time.substring(6, 8));
		} catch (Exception e) {
		}
		yymmdd = yy * 10000 + mm * 100 + dd;
		dt = yy + (mm < 10 ? "0" + mm : "" + mm)
				+ (dd < 10 ? "0" + dd : "" + dd);
		cal.set(yy, mm - 1, dd, hh, mi, ss);
		validate();
	}

	public void setTime(String time) {
		try {
			hh = Integer.parseInt(time.substring(0, 2));
		} catch (Exception e) {
		}
		try {
			mi = Integer.parseInt(time.substring(2, 4));
		} catch (Exception e) {
		}
		try {
			ss = Integer.parseInt(time.substring(4, 6));
		} catch (Exception e) {
		}

		hhmiss = hh * 10000 + mi * 100 + ss;
		tm = (hh < 10 ? "0" + hh : "" + hh) + (mi < 10 ? "0" + mi : "" + mi)
				+ (ss < 10 ? "0" + ss : "" + ss);
		cal.set(yy, mm - 1, dd, hh, mi, ss);
		validate();
	}

	public void setTime(long time) {
		Calendar tmp = Calendar.getInstance();
		tmp.setTimeInMillis(time);
		setCalendar(tmp);
	}

	public void set(String dttm) {
		try {
			setDate(dttm.substring(0, 8));
		} catch (Exception e) {
		}
		try {
			setTime(dttm.substring(8));
		} catch (Exception e) {
		}
	}

	public long getTime() {
		return cal.getTimeInMillis();
	}

	public void setCalendar(Calendar c) {

		this.cal = c;

		yy = cal.get(Calendar.YEAR);
		mm = cal.get(Calendar.MONTH) + 1;
		dd = cal.get(Calendar.DAY_OF_MONTH);
		hh = cal.get(Calendar.HOUR_OF_DAY);
		mi = cal.get(Calendar.MINUTE);
		ss = cal.get(Calendar.SECOND);

		yymmdd = yy * 10000 + mm * 100 + dd;
		dt = yy + (mm < 10 ? "0" + mm : "" + mm)
				+ (dd < 10 ? "0" + dd : "" + dd);

		hhmiss = hh * 10000 + mi * 100 + ss;
		tm = (hh < 10 ? "0" + hh : "" + hh) + (mi < 10 ? "0" + mi : "" + mi)
				+ (ss < 10 ? "0" + ss : "" + ss);

		validate();
	}

	public void addDay(int n) {
		cal.add(Calendar.DAY_OF_MONTH, n);
		setCalendar(cal);
	}

	public void addMonth(int n) {
		cal.add(Calendar.MONTH, n);
		setCalendar(cal);
	}

	public void addYear(int n) {
		cal.add(Calendar.YEAR, n);
		setCalendar(cal);
	}

	public void addHour(int n) {
		cal.add(Calendar.HOUR, n);
		setCalendar(cal);
	}

	public void addHour(double n) {
		int hour = (int) n;
		double d = n - hour;
		int min = (int) (d * 100);
		cal.add(Calendar.HOUR, hour);
		if (min > 0)
			cal.add(Calendar.MINUTE, min);
		setCalendar(cal);
	}

	public void addMinute(int n) {
		cal.add(Calendar.MINUTE, n);
		setCalendar(cal);
	}

	public void addSecond(int n) {
		cal.add(Calendar.SECOND, n);
		setCalendar(cal);
	}

	public void add(int type, int n) {
		cal.add(type, n);
		setCalendar(cal);
	}

	public static SimpleDateFormat df_m = new SimpleDateFormat("yyyyMM");
	public static SimpleDateFormat df_d = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat df_t = new SimpleDateFormat("HHmmss");
	public static SimpleDateFormat df_dt = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public static SimpleDateFormat ldf_d = new SimpleDateFormat("yyyy.MM.dd");
	public static SimpleDateFormat ldf_t = new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat ldf_dt = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss");

	public static SimpleDateFormat df_m(){
		if(df_m == null) df_m = new SimpleDateFormat("yyyyMM");
		return df_m;
	}

	public static SimpleDateFormat df_d(){
		if(df_d == null) df_d = new SimpleDateFormat("yyyyMMdd");
		return df_d;
	}

	public static SimpleDateFormat df_t(){
		if(df_t == null) df_t = new SimpleDateFormat("HHmmss");
		return df_t;
	}

	public static SimpleDateFormat df_dt(){
		if(df_dt == null) df_dt = new SimpleDateFormat("yyyyMMddHHmmss");
		return df_dt;
	}


	public static SimpleDateFormat ldf_d(){
		if(ldf_d == null) ldf_d = new SimpleDateFormat("yyyy.MM.dd");
		return ldf_d;
	}

	public static SimpleDateFormat ldf_t(){
		if(ldf_t == null) ldf_t = new SimpleDateFormat("HH:mm:ss");
		return ldf_t;
	}

	public static SimpleDateFormat ldf_dt(){
		if(ldf_dt == null) ldf_dt = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return ldf_dt;
	}

	public static String lastday() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return Time.df_d.format(cal.getTime());
	}

	public static String firstday() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		return Time.df_d.format(cal.getTime());
	}

	public void setLastTimeOfDay() {
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
				.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
		cal.add(Calendar.SECOND, -1);
	}

	public void setFirstTimeOfDay() {
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
				.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
	}

	public static String cur_ym() {
		Calendar cal = Calendar.getInstance();
		return Time.df_m().format(cal.getTime());
	}

	public static String cur_dt() {
		Calendar cal = Calendar.getInstance();
		return Time.df_d().format(cal.getTime());
	}

	public static String cur_dttm() {
		Calendar cal = Calendar.getInstance();
		return Time.df_dt().format(cal.getTime());
	}

	public static String cur_tm() {
		Calendar cal = Calendar.getInstance();
		return Time.df_t().format(cal.getTime());
	}

	public static String cur_ldt() {
		Calendar cal = Calendar.getInstance();
		return Time.ldf_d().format(cal.getTime());
	}

	public static String cur_ldttm() {
		Calendar cal = Calendar.getInstance();
		return Time.ldf_dt().format(cal.getTime());
	}

	public static String cur_ltm() {
		Calendar cal = Calendar.getInstance();
		return Time.ldf_t().format(cal.getTime());
	}

	public static String fm_dt(Calendar cal) {
		return Time.df_d.format(cal.getTime());
	}

	public static String fm_dttm(Calendar cal) {
		return Time.df_dt().format(cal.getTime());
	}

	public static String fm_tm(Calendar cal) {
		return Time.df_t().format(cal.getTime());
	}

	public static String fm_ldt(Calendar cal) {
		return Time.ldf_d().format(cal.getTime());
	}

	public static String fm_ldttm(Calendar cal) {
		return Time.ldf_dt().format(cal.getTime());
	}

	public static String fm_ltm(Calendar cal) {
		return Time.ldf_t().format(cal.getTime());
	}

	public static String fm_ldttm(String dt) {
		try {
			Date date = df_dt.parse(dt);
			return ldf_dt.format(date);
		} catch (Exception e) {

		}
		return dt;
	}

	public static String fm_ldt(String dt) {
		try {
			Date date = df_d.parse(dt);
			return ldf_d.format(date);
		} catch (Exception e) {

		}
		return dt;
	}

	public String fm_dt() {
		return Time.df_d().format(cal.getTime());
	}

	public String fm_dttm() {
		return Time.df_dt().format(cal.getTime());
	}

	public String fm_tm() {
		return Time.df_t().format(cal.getTime());
	}

	public String fm_ldt() {
		return Time.ldf_d().format(cal.getTime());
	}

	public String fm_ldttm() {
		return Time.ldf_dt().format(cal.getTime());
	}

	public String fm_ltm() {
		return Time.ldf_t().format(cal.getTime());
	}

	public static void main(String[] args) {
		Time tm = new Time();
		Time tm1 = new Time();
		tm.addHour(1.30);
		String t = tm.fm_ldttm();
		System.out.println(t.replace(".", "-"));
		System.out.println(tm.hhmiss);
	}
}
