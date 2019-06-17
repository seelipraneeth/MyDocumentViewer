function displayFileList() {
	document.getElementById("div_fileList").style.display = "block";
	document.getElementById("div_upload").style.display = "none";
}
function displayUpload() {
	document.getElementById("div_fileList").style.display = "none";
	document.getElementById("div_upload").style.display = "block";
}
function getSubfolder(subfolderid) {
	document.getElementById('folderID').value = subfolderid;
	document.getElementById('folderID2').value = subfolderid;
	document.myForm1.submit();
}
