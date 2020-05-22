boardWriteAction.jsp<%@page import="dao.dao_test"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% 

	int boardNo = Integer.parseInt(request.getParameter("boardNo"));

	dao_test.getDAO().getBoardDel(boardNo);
	
	//response.sendRedirect("location.href = '"+request.getContextPath()+"/board/boardList.jsp';");
	response.sendRedirect(request.getContextPath()+"/board/boardList.jsp");

%>