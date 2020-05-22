<%@page import="java.io.PrintWriter"%>
<%@page import="dto.PageVo"%>
<%@page import="dao.dao_test"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="dto.BoardDTO"%>
<%@page import="java.util.List"%>
<%@page import="dao.BoardDAO"%>
<%@page import="org.apache.log4j.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
table {
	width: 720px;
	
}
.boardTable tr {
	text-align: center;
}
</style>
</head>
<body>
<table>
	<tr>
		<td align="right">total : ${totalCnt }</td>
	</tr>
	<tr>
		<td>
			<table class="boardTable" border="1" style="border-spacing: 0px; border-collapse: collapse;">
				<tr>
					<td width="50px;">번호</td>
					<td width="400px;">제목</td>
					<td>작성자</td>
					<td>날짜</td>
				</tr>
				<c:forEach items="${boardList}" var="list">
				<tr>
					<td>${list.num }</td>
					<td><a href="">${list.subject }</a></td>
					<td>${list.writer }</td>
					<td>${list.regDate.substring(0,10) }</td>
				</tr>
				</c:forEach>
			</table>			
		</td>
	</tr>
	<tr>
		<td>
		<form action="" method="post" id="searchForm" style="display: contents;">
			<select name="search">
				<option value="writer" selected="selected">&nbsp;작성자&nbsp;</option>
				<option value="subject">&nbsp;제목&nbsp;</option>
				<option value="content">&nbsp;내용&nbsp;</option>
			</select>
			<input type="text" name="keyword" id="keyword">
			<button type="submit">검색</button>
		</form>
		<a href="boardWrite.jsp" style="margin-left: 350px;">글쓰기</a>
		</td>
	</tr>
	<tr>
	</tr>
	<tr>
		<td>
		<%-- 페이지 번호 출력 및 하이퍼 링크 --%>
		<div>
		<c:choose>
		<c:when test="${pageVo.startPage>pageVo.blockSize }">
		<a href="<%=request.getContextPath()%>/servletBoard/boardList.do?pageNo=1">[처음]</a>
		<a href="<%=request.getContextPath()%>/servletBoard/boardList.do?pageNo=${pageVo.startPage-pageVo.blockSize}">[이전]</a>
		</c:when>
		<c:otherwise>
		[처음][이전]
		</c:otherwise>
		</c:choose>
		<c:forEach begin="${pageVo.startPage }" end="${pageVo.endPage }" var="i">
		<c:choose>
		<c:when test="${pageVo.pageNo!=i }">
		<a href="<%=request.getContextPath()%>/servletBoard/boardList.do?pageNo=${i }">[${i }]</a>		
		</c:when>
		<c:otherwise>
		<span style="font-weight: bold; color: red;">[${i }]</span>		
		</c:otherwise>
		</c:choose>
		</c:forEach>
		<c:choose>
		<c:when test="${pageVo.endPage!= pageVo.totalPage}">
		<a href="<%=request.getContextPath()%>/servletBoard/boardList.do?pageNo=${pageVo.startPage+pageVo.blockSize}">[다음]</a>		
		<a href="<%=request.getContextPath()%>/servletBoard/boardList.do?pageNo=${pageVo.totalPage }">[마지막]</a>		
		</c:when>
		<c:otherwise>
		[다음][마지막]
		</c:otherwise>
		</c:choose>
		</div>
		</td>
	</tr>
</table>
</body>
</html>