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
	document.myForm1.submit();
}
function uploadFile(subfolderid) {
	document.getElementById('folderID2').value = subfolderid;
	document.myForm2.submit();
}
function createFolder(currentFolderID) {
	var folderName = prompt("Please enter new folder name", "New Folder");
	if (folderName != null) {

		document.getElementById("folderName").value = folderName;
		document.getElementById("parentID").value = currentFolderID;
		document.myForm3.submit();
	}
}