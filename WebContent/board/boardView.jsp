<%@page import="java.io.PrintWriter"%>
<%@page import="dto.BoardDTO"%>
<%@page import="dao.BoardDAO"%>
<%@page import="dao.dao_test"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<% 

	request.setCharacterEncoding("UTF-8");

// 	if(request.getParameter("boardNo")==null) {
// 		out.println("<script>");
// 		out.println("location.href='"+request.getContextPath()+"/board/boardList.jsp';");
// 		out.println("</script>");
// 		return;
// 	}
	if(request.getParameter("boardNo")== null || request.getParameter("boardNo")== "") {
		PrintWriter pw = response.getWriter();
		pw.println("<script>");
		pw.println("location.href='"+request.getContextPath()+"/board/boardList.jsp';");		
		pw.println("</script>");
		return;
	}
	
	int boardNo = Integer.parseInt(request.getParameter("boardNo"));
	
	BoardDTO board = dao_test.getDAO().getboardView(boardNo);

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
<table>
	<tr>
		<td>
			<table border="1" style="border-spacing: 0px; border-collapse: collapse;">
				<tr>
					<td width="70px;">제목</td>
					<td width="240px;">
					<%=board.getSubject() %>
					</td>
				</tr>
				<tr>
					<td width="70px;">내용</td>
					<td>
					<%=board.getContent() %>
					</td>
				</tr>
				<tr>
					<td width="70px;">작성자</td>
					<td>
					<%=board.getWriter() %>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
			<input type="button" value="이전" onclick="javaScript:history.back();">
<%-- 			<a href="boardDeleteAction.jsp?boardNo=<%=boardNo %>">삭제</a> --%>
			<input type="button" value="삭제" onclick="boardDelete();">
			<input type="button" value="수정" onclick="boardUpdate();">
		</td>
	</tr>
</table>
<script>

boardDelete = function() {
	location.href = "boardDeleteAction.jsp?boardNo=<%=boardNo %>";
}

boardUpdate = function() {
	location.href = "boardUpdate.jsp?boardNo=<%=boardNo %>";
}

</script>
</body>
</html>