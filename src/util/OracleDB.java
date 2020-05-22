package util;

public class OracleDB {
	
	//드라이버 클래스 이름
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	
	//DB연결 정보
	public static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
	public static final String USER = "scott";
	public static final String PW = "tiger";
}
