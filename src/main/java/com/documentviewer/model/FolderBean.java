package com.documentviewer.model;

import java.util.ArrayList;
import java.util.List;

public class FolderBean {

	private String folderName;
	private String folderID;

	List<FolderBean> subFolders = new ArrayList<>();
	List<FileBean> subFiles = new ArrayList<FileBean>();

	public FolderBean(String folderName, String folderID) {
		super();
		this.folderName = folderName;
		this.folderID = folderID;
	}

	public FolderBean() {
		// TODO Auto-generated constructor stub
	}

	public void addSubfolders(FolderBean folderObj) {
		subFolders.add(folderObj);
	}

	public void addFiles(FileBean fileObj) {
		subFiles.add(fileObj);
	}

	public List<FolderBean> getSubFolders() {
		return subFolders;
	}

	public void setSubFolders(List<FolderBean> subFolders) {
		this.subFolders = subFolders;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public List<FileBean> getSubFiles() {
		return subFiles;
	}

	public void setSubFiles(List<FileBean> subFiles) {
		this.subFiles = subFiles;
	}

	public String getFolderID() {
		return folderID;
	}

	public void setFolderID(String folderID) {
		this.folderID = folderID;
	}

}
