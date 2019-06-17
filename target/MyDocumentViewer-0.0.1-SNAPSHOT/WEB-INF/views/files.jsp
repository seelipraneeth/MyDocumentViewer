<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="ISO-8859-1">
<link
	href="${pageContext.request.contextPath}/resources/css/appStyle.css"
	rel="stylesheet">
<script
	src="${pageContext.request.contextPath}/resources/js/appScript.js"></script>
<title>My Document Viewer</title>
</head>

<script type="text/javascript">
	
</script>
<body>
	<div align="center">
		<form:form name="myForm" method="POST"
			action="/MyDocumentViewer/subFiles">
			<input type="hidden" id="folderID" name="folderID" value="0">
			<table>
				<tr>
					<td><c:if test="${null != folder}">
							<table border="1" cellpadding="5">
								<c:forEach var="subFiles" items="${folder.getSubFiles()}">
									<tr>
										<td><c:out value="${subFiles.getFileName()}" /></td>
									</tr>
								</c:forEach>
								<c:forEach var="subFolder" items="${folder.getSubFolders()}">
									<tr>
										<td><a
											href="javascript:getSubfolder('${subFolder.getFolderID()}')">
												<c:out value="${subFolder.getFolderName()}" />
										</a></td>
									</tr>
								</c:forEach>
							</table>
						</c:if></td>
				</tr>
			</table>

		</form:form>
		<form action="/MyDocumentViewer/uploadFile" method="post" enctype="multipart/form-data">
		<input type="hidden" id="folderID2" name="folderID" value="0">
			<div align="center" id="div_upload">
				<table>
					<tr>
						<td><input type="file" name="multipartFile"
							id="multipartFile"><br></br></td>
					</tr>
					<tr>
						<td><input type="submit" value="Upload File" name="submit">
						</td>
					</tr>
				</table>
			</div>
		</form>
	</div>
</body>
</html>