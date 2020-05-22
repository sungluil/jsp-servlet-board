/*****************************************************************
 * Class : DateUtil.java
 * Class Summary :
 * Written Date : 2001.07.18
 * Modified Date : 2001.07.18
 *
 * @author  SeongHo, Park / jeros@epitomie.com
 *****************************************************************/

package org.sdf.util;

import java.util.Calendar;
import java.util.Date;
import java.text.*;

public class DateUtil {
	long time;

	private static String[][] days = { { "일", "월", "화", "수", "목", "금", "토" },
			{ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" } };

	private static String[][] months = { { "Jan", "Feb", "Mar", "Apr", "May",
			"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" } };

	public static int locale = DateUtil.KR;
	public final static int KR = 0;
	public final static int EN = 1;
	public final static int JP = 2;

	public DateUtil() {
		time = DateUtil.curTime();
	}

	public DateUtil(long time) {
		this.time = time;
	}

	public DateUtil(String date) {
		time = DateUtil.getTime(date);
	}

	/**
	 * 주어진 시간보다 이전의 시간이면 true를 리턴.
	 *
	 * @return boolean
	 */
	public boolean before(DateUtil date) {
		if (time < date.getTime())
			return true;
		return false;
	}

	/**
	 * 주어진 시간과 같은 시간이면 true를 리턴.
	 *
	 * @return boolean
	 */
	public boolean equals(DateUtil date) {
		if (time == date.getTime())
			return true;
		return false;
	}

	/**
	 * 주어진 시간보다 이후의 시간이면 true를 리턴.
	 *
	 * @return boolean
	 */
	public boolean after(DateUtil date) {
		if (time > date.getTime())
			return true;
		return false;
	}

	/**
	 * 설정된 시간에 해당하는 날짜의 최초의 Second를 리턴한다.
	 *
	 * @return long
	 */
	public long firstTime() {
		return getTime(getDateString(time));
	}

	/**
	 * 설정된 시간에 해당하는 날짜의 마지막 Second를 리턴한다.
	 *
	 * @return long
	 */
	public long lastTime() {

		return getTime(getDateString(time)) + 60 * 60 * 24 - 1;
	}

	/**
	 * 설정된 시간의 970/01/01 GMT 이후의 총 Second를 리턴한다.
	 *
	 * @return long
	 */
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public static synchronized DateUtil getDate(String time) {
		long t = DateUtil.getTime(time);
		if (t == -1)
			return null;
		return new DateUtil(t);
	}

	public static synchronized long getTime(String time) {
		Calendar cal = DateUtil.getCalendar(time);
		if (cal == null)
			return -1;
		return cal.getTime().getTime() / 1000;
	}

	/**
	 * 지정된 시간의 Calendar를 리턴한다.
	 *
	 * @param date
	 *            String
	 *
	 *            <pre>
	 * time format :
	 * 						yyyy
	 * 						yyyymm
	 * 						yyyymmdd
	 * 						yyyymmddhh
	 * 						yyyymmddhhmi
	 * 						yyyymmddhhmiss
	 * </pre>
	 * @return Calendar 포맷이 적합하지 않으면 null을 리턴한다
	 */
	public static synchronized Calendar getCalendar(String date) {
		int yy = 0;
		int mm = 0;
		int dd = 0;
		int hh = 0;
		int mi = 0;
		int ss = 0;

		int len = date.length();

		switch (len) {
		case 4: // "yyyy"
			yy = Integer.parseInt(date);
			mm = 0;
			dd = 1;
			break;
		case 6: // "yyyymm"
			yy = Integer.parseInt(date.substring(0, 4));
			mm = Integer.parseInt(date.substring(4)) - 1;
			dd = 1;
			break;
		case 8: // "yyyymmdd"
			yy = Integer.parseInt(date.substring(0, 4));
			mm = Integer.parseInt(date.substring(4, 6)) - 1;
			dd = Integer.parseInt(date.substring(6));
			break;
		case 10: // "yyyymmddhh"
			yy = Integer.parseInt(date.substring(0, 4));
			mm = Integer.parseInt(date.substring(4, 6)) - 1;
			dd = Integer.parseInt(date.substring(6, 8));
			hh = Integer.parseInt(date.substring(8, 10));
			break;
		case 12: // "yyyymmddhhmi"
			yy = Integer.parseInt(date.substring(0, 4));
			mm = Integer.parseInt(date.substring(4, 6)) - 1;
			dd = Integer.parseInt(date.substring(6, 8));
			hh = Integer.parseInt(date.substring(8, 10));
			mi = Integer.parseInt(date.substring(10, 12));
			break;
		case 14: // "yyyymmddhhmiss"
			yy = Integer.parseInt(date.substring(0, 4));
			mm = Integer.parseInt(date.substring(4, 6)) - 1;
			dd = Integer.parseInt(date.substring(6, 8));
			hh = Integer.parseInt(date.substring(8, 10));
			mi = Integer.parseInt(date.substring(10, 12));
			ss = Integer.parseInt(date.substring(12));
			break;
		default:
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.set(yy, mm, dd, hh, mi, ss);
		return cal;
	}

	public Calendar getCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time * 1000));
		return cal;
	}

	/**
	 * 현재시간이 표현되어지는 1970/01/01 GMT 이후의 총 second를 리턴한다
	 *
	 * @return long
	 */

	public static synchronized DateUtil curDate() {
		return new DateUtil(DateUtil.curTime());
	}

	public static synchronized long curTime() {
		long time = (long) (DateUtil.curCalendar().getTime().getTime() / 1000);
		return time;
	}

	public static synchronized Calendar curCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * 설정된 시간에서 주어진 hour를 더한 후 970/01/01 GMT 이후의 총 Second를 리턴한다
	 *
	 * @param hour
	 *            int
	 * @return long
	 */
	public long add(int seconds) {
		time += seconds;
		return time;
	}

	public long addHour(int hour) {
		time += hour * 60 * 60;
		return time;
	}

	/**
	 * 설정된 시간에서 주어진 day를 더한 후 970/01/01 GMT 이후의 총 Second를 리턴한다
	 *
	 * @param day
	 *            int
	 * @return long
	 */
	public long addDay(int day) {
		time += day * 60 * 60 * 24;
		return time;
	}

	/**
	 * 설정된 시간에서 주어진 week를 더한 후 970/01/01 GMT 이후의 총 Second를 리턴한다
	 *
	 * @param week
	 *            int
	 * @return long
	 */
	public long addWeek(int week) {
		time += 7 * week * 60 * 60 * 24;
		return time;
	}

	/**
	 * 설정된 시간에서 주어진 month를 더한 후 970/01/01 GMT 이후의 총 Second를 리턴한다
	 *
	 * @param hour
	 *            int
	 * @return long
	 */
	public long addMonth(int month) {
		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST") );
		cal.setTime(new Date(time * 1000));
		cal.add(Calendar.MONTH, month);
		time = cal.getTime().getTime() / 1000;
		return time;
	}

	/**
	 * 설정된 시간에서 주어진 year를 더한 후 970/01/01 GMT 이후의 총 Second를 리턴한다
	 *
	 * @param year
	 *            int
	 * @return long
	 */
	public long addYear(int year) {
		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST") );
		cal.setTime(new Date(time * 1000));
		cal.add(Calendar.YEAR, year);
		time = cal.getTime().getTime() / 1000;
		return time;
	}

	public String getDateString() {
		return DateUtil.getDateString(time);
	}

	public String getHourString() {
		return DateUtil.getHourString(time);
	}

	public String getTimeString() {
		return DateUtil.getTimeString(time);
	}

	public synchronized static String getDayOfWeek(String date) {
		Calendar cal = getCalendar(date);
		return days[locale][cal.get(Calendar.DAY_OF_WEEK) - 1];
	}

	public synchronized final static String curDateString() {
		Calendar cal = Calendar.getInstance();
		return getDateString(cal.getTime().getTime() / 1000);
	}

	public synchronized static String getDateString(long time) {
		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST") );
		cal.setTime(new Date(time * 1000));

		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);

		String month = String.valueOf(m);
		String day = String.valueOf(d);

		if (m < 10)
			month = "0" + month;
		if (d < 10)
			day = "0" + day;

		return new StringBuffer().append(y).append(month).append(day)
				.toString();
	}

	public synchronized static String getTimeString(long time) {
		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST") );
		cal.setTime(new Date(time * 1000));

		int h = cal.get(Calendar.HOUR_OF_DAY);
		int mi = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);

		String hour = String.valueOf(h);
		String minute = String.valueOf(mi);
		String second = String.valueOf(s);

		if (h < 10)
			hour = "0" + hour;
		if (mi < 10)
			minute = "0" + minute;
		if (s < 10)
			second = "0" + second;
		return new StringBuffer().append(hour).append(minute).append(second)
				.toString();
	}

	public synchronized final static String getSimpleDateString() {
		Calendar cal = Calendar.getInstance();
		return getSimpleDateString(cal.getTime().getTime() / 1000);
	}

	public synchronized final static String getSimpleDateString(long time) {
		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST") );
		cal.setTime(new Date(time * 1000));
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);

		String month = String.valueOf(m);
		String day = String.valueOf(d);

		if (m < 10)
			month = "0" + month;
		if (d < 10)
			day = "0" + day;

		int h = cal.get(Calendar.HOUR_OF_DAY);
		int mi = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);

		String hour = String.valueOf(h);
		String minute = String.valueOf(mi);
		String second = String.valueOf(s);

		if (h < 10)
			hour = "0" + hour;
		if (mi < 10)
			minute = "0" + minute;
		if (s < 10)
			second = "0" + second;
		return new StringBuffer().append(y).append('/').append(month).append(
				'/').append(day).append(' ').append(hour).append(':').append(
				minute).append(':').append(second).toString();
	}

	public static String getHourString(long time) {
		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST") );
		cal.setTime(new Date(time * 1000));

		int h = cal.get(Calendar.HOUR_OF_DAY);
		if (h < 10)
			return "0" + h;

		return String.valueOf(h);
	}

	public static void main(String[] args) {
		DateUtil dt = new DateUtil("20010830");

		System.out.println(dt.firstTime() + ":" + dt.lastTime());
	}

	public static SimpleDateFormat df_d = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat df_t = new SimpleDateFormat("HHmmss");
	public static SimpleDateFormat df_dt = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public static SimpleDateFormat ldf_d = new SimpleDateFormat("yyyy/MM/dd");
	public static SimpleDateFormat ldf_t = new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat ldf_dt = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public final static int DT = 1;
	public final static int TM = 2;
	public final static int DT_TM = 3;

	public final static int LDT = 11;
	public final static int LTM = 12;
	public final static int LDT_TM = 13;

	public static String format(Date dt, int type) {
		switch (type) {
		case DT:
			return df_d.format(dt);
		case TM:
			return df_t.format(dt);
		case DT_TM:
			return df_dt.format(dt);
		case LDT:
			return ldf_d.format(dt);
		case LTM:
			return ldf_t.format(dt);
		case LDT_TM:
			return ldf_dt.format(dt);
		}
		return df_d.format(dt);
	}

	public static Date parse(String dt, int type) {
		try {
			switch (type) {
			case DT:
				return df_d.parse(dt);
			case TM:
				return df_t.parse(dt);
			case DT_TM:
				return df_dt.parse(dt);
			case LDT:
				return ldf_d.parse(dt);
			case LTM:
				return ldf_t.parse(dt);
			case LDT_TM:
				return ldf_dt.parse(dt);
			}

			return df_d.parse(dt);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * String 형식의 날짜를 포맷에 맞게 변환하여 반환
	 *
	 * @param date
	 *            String 날짜
	 * @param format
	 *            String 포맷
	 * @return
	 * @throws Exception
	 */
	public static String getFormatDate(String date, String format)
			throws Exception {
		if (date == null || format == null)
			return "";

		String result = "";
		int year = 0, month = 0, day = 0, hour = 0, min = 0, sec = 0, length = date
				.length();
		try {
			if (length >= 8 && length <= 14) { // 날짜
				year = Integer.parseInt(date.substring(0, 4));
				month = Integer.parseInt(date.substring(4, 6));
				day = Integer.parseInt(date.substring(6, 8));
				if (length == 10) { // 날짜+시간
					hour = Integer.parseInt(date.substring(8, 10));
				}

				if (length == 12) { // 날짜+시간
					hour = Integer.parseInt(date.substring(8, 10));
					min = Integer.parseInt(date.substring(10, 12));
				}

				if (length == 14) { // 날짜+시간
					hour = Integer.parseInt(date.substring(8, 10));
					min = Integer.parseInt(date.substring(10, 12));
					sec = Integer.parseInt(date.substring(12, 14));
				}
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month - 1, day, hour, min, sec);
				result = (new SimpleDateFormat(format)).format(calendar
						.getTime());
			}
		} catch (Exception ex) {
			throw new Exception("getFormatDate.getFormatDate(\"" + date
					+ "\",\"" + format + "\")\r\n" + ex.getMessage());
		}
		return result;
	}

	public synchronized static String getDateString(String str_time,
			long l_time, String delim) {

		long getTime = DateUtil.getTime(str_time) + l_time;
		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("JST") );
		cal.setTime(new Date(getTime * 1000));

		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);

		String month = String.valueOf(m);
		String day = String.valueOf(d);
		String hour = String.valueOf(h);
		String minute = String.valueOf(mm);
		String second = String.valueOf(s);

		if (m < 10)
			month = "0" + month;
		if (d < 10)
			day = "0" + day;
		if (h < 10)
			hour = "0" + hour;
		if (mm < 10)
			minute = "0" + minute;
		if (s < 10)
			second = "0" + second;

		return new StringBuffer().append(y).append(delim).append(month).append(
				delim).append(day).append(" ").append(hour).append(":").append(
				minute).toString();
	}
}