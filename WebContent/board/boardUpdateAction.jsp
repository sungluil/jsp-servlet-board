<%@page import="dto.BoardDTO"%>
<%@page import="dao.dao_test"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% 

	request.setCharacterEncoding("utf-8");

	int boardNo = Integer.parseInt(request.getParameter("boardNo"));
	String subject = request.getParameter("subject");
	String content = request.getParameter("content");
	String writer = request.getParameter("writer");
	
	BoardDTO board = new BoardDTO();
	board.setNum(boardNo);
	board.setSubject(subject);
	board.setContent(content);
	board.setWriter(writer);
	
	dao_test.getDAO().updateBoard(board);
	
	response.sendRedirect(request.getContextPath()+"/board/boardList.jsp");

%>