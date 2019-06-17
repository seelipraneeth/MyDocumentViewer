package com.documentviewer.model;

public class FileBean {

	private String fileName;
	private String fileID;

	public FileBean(String fileName, String fileID) {
		super();
		this.fileName = fileName;
		this.fileID = fileID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
}
