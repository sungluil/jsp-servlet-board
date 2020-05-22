<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
tr {
	text-align: center;
}
</style>
</head>
<body>
<table>
	<tr>
		<td>
			<table border="1" style="border-spacing: 0px; border-collapse: collapse;">
				<tr>
					<td width="70px;">제목</td>
					<td width="240px;"><input type="text" required="required" name="subject" maxlength="20" style="width: 98%; height: 100%;"></td>
				</tr>
				<tr>
					<td width="70px;">내용</td>
					<td><textarea rows="10" cols="33" required="required" name="content" ></textarea></td>
				</tr>
				<tr>
					<td width="70px;">작성자</td>
					<td><input type="text" required="required" name="writer" maxlength="20" style="width: 98%; height: 100%;"></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
			<input type="button" value="이전" onclick="javaScript:history.back();">
			<input type="submit" value="작성">
		</td>
	</tr>
</table>
</form>
</body>
</html>