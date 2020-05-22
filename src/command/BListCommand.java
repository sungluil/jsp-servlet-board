package command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.dao_test;
import dto.BoardDTO;
import dto.PageVo;

public class BListCommand implements BCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
		try {
			request.setCharacterEncoding("UTF-8");
			
			//게시판 페이징
			int pageNo = 1;
			if(request.getParameter("pageNo")!=null) {//전달값이 존재할 경우
				pageNo=Integer.parseInt(request.getParameter("pageNo"));
			}
			int pageSize=10;
			int blockSize=5;
			int totalCnt = dao_test.getDAO().getTotalCnt();

			PageVo pageVo = new PageVo(pageNo, pageSize, totalCnt, blockSize);
			int startRow = pageVo.getStartRow();
			int endRow = pageVo.getEndRow();
			
			String keyword = request.getParameter("keyword");
			if(keyword==null) keyword="";
			String search = request.getParameter("search");
			if(search==null) search="";
			
			List<BoardDTO> boardList = dao_test.getDAO().getboardPasingList(startRow, endRow);
			
			
			request.setAttribute("boardList", boardList);
			request.setAttribute("totalCnt", totalCnt);
			request.setAttribute("pageVo", pageVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}