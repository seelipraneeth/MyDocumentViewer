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
		->
		<c:out value="${currentFolderID}" />
		<-
		<form:form name="myForm3" method="POST"
			action="/MyDocumentViewer/createFolder">
			<input type="hidden" id="folderName" name="folderName" value="0">
			<input type="hidden" id="parentID" name="parentID" value="0">
			<table>
				<tr>
					<td><input type="button" value="Create Folder"
						onclick="createFolder('${currentFolderID}')"></td>
				</tr>
			</table>
		</form:form>
	</div>
	<div align="center">

		<form:form name="myForm1" method="POST"
			action="/MyDocumentViewer/subFiles">
			<input type="hidden" id="folderID" name="folderID" value="0">
			<input type="hidden" id="fileID" name="fileID" value="0">
			<input type="hidden" id="fileParentID" name="fileParentID" value="0">
			<table>
				<tr>
					<td><c:if test="${null != folder}">
							<table border="1" cellpadding="5">
								<c:forEach var="subFile" items="${folder.getSubFiles()}">
									<tr>
										<td><a
											href="/MyDocumentViewer/downloadFile/${subFile.getFileID()}" >
												<c:out value="${subFile.getFileName()}" />
										</a></td>
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
		<form:form name="myForm2" method="POST"
			action="/MyDocumentViewer/uploadFile" enctype="multipart/form-data">
			<input type="hidden" id="folderID2" name="folderID2" value="0">
			<div align="center" id="div_upload">
				<table border="1">
					<tr>
						<td><input type="file" name="multipartFile"
							id="multipartFile"></td>
					</tr>
					<tr>
						<td><input type="button" value="Upload File"
							onclick="uploadFile('${currentFolderID}')"></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
</body>
</html>