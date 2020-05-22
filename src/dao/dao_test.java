package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


import dto.BoardDTO;
import dto.PageVo;

public class dao_test extends JdbcDAO {
	private static dao_test _dao;
	
	
	public dao_test() {
		// TODO Auto-generated constructor stub
	}
	
	static {
		_dao = new dao_test();
	}
	
	public static dao_test getDAO() {
		return _dao;
	}
	
	protected static Logger logger = Logger.getLogger(dao_test.class.getName());

	
	//총게시글 수
	public int getTotalCnt() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int rows = 0;
		
		try {
			con=getConnection();
			String sql = "select count(*) from bbs_board01";
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				rows=rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(con, pstmt, rs);
		}
		return rows;
	}
	//게시글 리스트
	public List<BoardDTO> getboardList(int pageNo) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		
		try {
			con=getConnection();
			String sql = "select * from(select num,id,writer,subject,reg_date,readcount,ref,re_step,re_level,content,ip,status, ROW_NUMBER() over(order by num desc)rn from bbs_board01) where rn between (?-1)*10 and (?)*10";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, pageNo);
			pstmt.setInt(2, pageNo);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				board.setNum(rs.getInt("num"));
				board.setId(rs.getString("id"));
				board.setWriter(rs.getString("writer"));
				board.setSubject(rs.getString("subject"));
				board.setRegDate(rs.getString("reg_date"));
				board.setReadCount(rs.getInt("readcount"));
				board.setRef(rs.getInt("ref"));
				board.setReStep(rs.getInt("re_step"));
				board.setReLevel(rs.getInt("re_level"));
				board.setContent(rs.getString("content"));
				board.setIp(rs.getString("ip"));
				board.setStatus(rs.getInt("status"));
				list.add(board);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, pstmt, rs);
		}
		return list;
	}
	//게시글 리스트 페이징
	public List<BoardDTO> getboardPasingList(int startRow, int endRow) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		
		try {
			con=getConnection();
			String sql = "select * from(select num,id,writer,subject,reg_date,readcount,ref,re_step,re_level,content,ip,status, ROW_NUMBER() over(order by num desc)rn from bbs_board01) where rn between ? and ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				board.setNum(rs.getInt("num"));
				board.setId(rs.getString("id"));
				board.setWriter(rs.getString("writer"));
				board.setSubject(rs.getString("subject"));
				board.setRegDate(rs.getString("reg_date"));
				board.setReadCount(rs.getInt("readcount"));
				board.setRef(rs.getInt("ref"));
				board.setReStep(rs.getInt("re_step"));
				board.setReLevel(rs.getInt("re_level"));
				board.setContent(rs.getString("content"));
				board.setIp(rs.getString("ip"));
				board.setStatus(rs.getInt("status"));
				list.add(board);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, pstmt, rs);
		}
		return list;
	}
	//게시글 리스트 페이징
	public List<BoardDTO> searchBoardList(int startRow, int endRow, String keyword, String search) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		//System.out.println("search = "+search);
		try {
			con=getConnection();
			if(keyword == "") {
				String sql = "select * from(select num,id,writer,subject,reg_date,readcount,ref,re_step,re_level,content,ip,status, ROW_NUMBER() over(order by num desc)rn from bbs_board01) where rn between ? and ?";				
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, startRow);
				pstmt.setInt(2, endRow);
			} else {
				String sql = "select * from(select num,id,writer,subject,reg_date,readcount,ref,re_step,re_level,content,ip,status, "
						+ "ROW_NUMBER() over(order by num desc) rn from bbs_board01 where "+search+" like '%'||?||'%') where rn between ? and ?";				
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, keyword);
				pstmt.setInt(2, startRow);
				pstmt.setInt(3, endRow);
			}
			rs=pstmt.executeQuery();
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				board.setNum(rs.getInt("num"));
				board.setId(rs.getString("id"));
				board.setWriter(rs.getString("writer"));
				board.setSubject(rs.getString("subject"));
				board.setRegDate(rs.getString("reg_date"));
				board.setReadCount(rs.getInt("readcount"));
				board.setRef(rs.getInt("ref"));
				board.setReStep(rs.getInt("re_step"));
				board.setReLevel(rs.getInt("re_level"));
				board.setContent(rs.getString("content"));
				board.setIp(rs.getString("ip"));
				board.setStatus(rs.getInt("status"));
				list.add(board);
			}
			
		} catch (SQLException e) {
			e.getMessage();
		} finally {
			close(con, pstmt, rs);
		}
		return list;
	}
	
	public BoardDTO getboardView(int boardNo) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDTO board = null;
		try {
			con=getConnection();
			String sql = "select * from bbs_board01 where num = ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				board = new BoardDTO();
				board.setNum(rs.getInt("num"));
				board.setId(rs.getString("id"));
				board.setWriter(rs.getString("writer"));
				board.setSubject(rs.getString("subject"));
				board.setRegDate(rs.getString("reg_date"));
				board.setReadCount(rs.getInt("readcount"));
				board.setRef(rs.getInt("ref"));
				board.setReStep(rs.getInt("re_step"));
				board.setReLevel(rs.getInt("re_level"));
				board.setContent(rs.getString("content"));
				board.setIp(rs.getString("ip"));
				board.setStatus(rs.getInt("status"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(con, pstmt, rs);
		}
		return board;
	}
	
	public int getboardWrite(BoardDTO board) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int rows = 0;
		
		try {
			con=getConnection();
			//String sql = "insert into bbs_board01 values (sequence_bbs.nextVal,?,?,?,sysdate,0,?,?,?,?,?,?)";
			String sql = "insert into bbs_board01 values ((select nvl(max(num)+1,1) from bbs_board01),?,?,?,sysdate,0,?,?,?,?,?,?)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, board.getId());
			pstmt.setString(2, board.getWriter());
			pstmt.setString(3, board.getSubject());
			pstmt.setInt(4, board.getRef());
			pstmt.setInt(5, board.getReStep());
			pstmt.setInt(6, board.getReLevel());
			pstmt.setString(7, board.getContent());
			pstmt.setString(8, board.getId());
			pstmt.setInt(9, board.getStatus());
			rows=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(con, pstmt);
		}
		
		return rows;
	}
	
	public int getBoardDel(int boardNo) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int rows=0;
		
		try {
			con=getConnection();
			String sql = "delete from bbs_board01 where num = ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			rows=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rows;
	}
	
	public int updateBoard(BoardDTO board) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int rows=0;
		
		try {
			con=getConnection();
			String sql="update bbs_board01 set subject=?,status=?,content=? where num=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, board.getSubject());
			pstmt.setInt(2, board.getStatus());
			pstmt.setString(3, board.getContent());
			pstmt.setInt(4, board.getNum());
			rows=pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(con, pstmt);
		}
		
		return rows;
	}
	public int getBoardNum() {
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		int num=0;
		try {
			con=getConnection();
			
			String sql="select nvl(max(num+1),1) num from bbs_board01";					
			pstmt=con.prepareStatement(sql);
			
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				num=rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(con, pstmt, rs);
		}
		return num;
	}
}
