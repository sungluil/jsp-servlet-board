package org.sdf.util;

/**
 *
 * @(#) StopWatch.java Copyright 1999-2000 by LG-EDS Systems, Inc., Information
 *      Technology Group, Application Architecture Team, Application
 *      Intrastructure Part. 236-1, Hyosung-2dong, Kyeyang-gu, Inchun, 407-042,
 *      KOREA. All rights reserved.
 *
 *      NOTICE ! You can copy or redistribute this code freely, but you should
 *      not remove the information about the copyright notice and the author.
 *
 * @author WonYoung Lee, wyounglee@lgeds.lg.co.kr.
 */
public class StopWatch {
	long start = 0;
	long current = 0;

	/**
	 * StopWatch constructor comment.
	 */
	public StopWatch() {
		reset();
	}

	/**
	 *
	 * @return long
	 */
	public long getElapsed() {
		long now = System.currentTimeMillis();
		long elapsed = (now - current);
		current = now;
		return elapsed;
	}

	public String getStartDateString() {
		return DateUtil.getDateString(start / 1000);
	}

	public String getStartTimeString() {
		return DateUtil.getTimeString(start / 1000);
	}

	/**
	 *
	 * @return long
	 */
	public long getTotalElapsed() {
		current = System.currentTimeMillis();
		return (current - start);
	}

	/**
	 *
	 */
	public void reset() {
		start = System.currentTimeMillis();
		current = start;
	}
}