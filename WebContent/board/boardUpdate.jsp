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
	
	BoardDTO boardlist = dao_test.getDAO().getboardView(boardNo);

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
<form action="boardUpdateAction.jsp" method="post">
<input type="hidden" name="boardNo" value="<%=boardNo%>">
<table>
	<tr>
		<td>
			<table border="1" style="border-spacing: 0px; border-collapse: collapse;">
				<tr>
					<td width="70px;">제목</td>
					<td width="240px;">
					<input type="text" required="required" name="subject" maxlength="20" style="width: 98%; height: 100%;" value="<%=boardlist.getSubject() %>">
					</td>
				</tr>
				<tr>
					<td width="70px;">내용</td>
					<td>
					<textarea rows="10" cols="33" required="required" name="content"><%=boardlist.getContent() %></textarea>
					</td>
				</tr>
				<tr>
					<td width="70px;">작성자</td>
					<td><input type="text" required="required" name="writer" maxlength="20" style="width: 98%; height: 100%;" value="<%=boardlist.getWriter() %>"></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
			<input type="button" value="이전" onclick="javaScript:history.back();">
<%-- 			<a href="boardDeleteAction.jsp?boardNo=<%=boardNo %>">삭제</a> --%>
			<input type="submit" value="수정">
		</td>
	</tr>
</table>
</form>
<script>
</script>
</body>
</html>