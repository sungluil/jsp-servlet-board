package org.sdf.log;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public interface ILogPolicy {
	public static SimpleDateFormat df_d = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat df_t = new SimpleDateFormat("HHmmss");
	public static SimpleDateFormat df_dt = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	public static SimpleDateFormat df_m = new SimpleDateFormat("yyyyMM");

	public static SimpleDateFormat ldf_d = new SimpleDateFormat("yyyy/MM/dd");
	public static SimpleDateFormat ldf_t = new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat ldf_dt = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public PrintWriter getPrintWriter();

	public String suffix();

	public String prefix();

	public String getName();

	public void backup();

	public boolean isInfo();

	public boolean isWarn();

	public boolean isDebug();

	public void setInfoEnable(boolean b);

	public void setWarnEnable(boolean b);

	public void setDebugEnable(boolean b);
	
	public void close();
		
}
