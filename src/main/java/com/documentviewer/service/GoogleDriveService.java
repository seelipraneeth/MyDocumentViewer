package com.documentviewer.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.documentviewer.model.FolderBean;

public interface GoogleDriveService {

	public FolderBean getFiles() throws IOException, GeneralSecurityException;

	public void uploadFile(MultipartFile multipartFile, String folderID) throws IllegalStateException, IOException;

	public boolean createFolder(String folderName, String parentID) throws IOException;

	public void downloadFiles(String file, HttpServletResponse response) throws IOException;
}
