package org.sdf.lang;

import java.util.Hashtable;
import java.util.Vector;

/**
 * <PRE>
 * 프로젝트명    : Help Desk 확대 구축
 * 프로그램개요 : 
 * 관련 테이블   : 
 * 클래스명 	    : IData
 * 작성자 	        : 박성호
 * 작성일 	        : 2005. 5. 12.
 * 비고 		        : 
 * 개정이력  	    : 2005. 5. 12. 박성호, v1.0, 최초작성
 * 
 * 
 * </PRE>
 */

public interface IData {

	public abstract boolean valid(String key);

	public abstract String get(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract boolean getBoolean(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract double getDouble(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract float getFloat(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract int getInt(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract long getLong(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract String getString(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract Object getObject(String key);

	/**
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public abstract String[] getArray(String key);

	public abstract String fix(String name);

	public abstract String[] getKeys();
}
