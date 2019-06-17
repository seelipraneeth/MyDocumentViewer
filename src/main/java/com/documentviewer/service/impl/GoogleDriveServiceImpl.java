package com.documentviewer.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.documentviewer.constants.ApplicationConstant;
import com.documentviewer.model.FileBean;
import com.documentviewer.model.FolderBean;
import com.documentviewer.service.AuthorizationService;
import com.documentviewer.service.GoogleDriveService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

@Service(value = "googleDriveService")
public class GoogleDriveServiceImpl implements GoogleDriveService {

	private Logger logger = LoggerFactory.getLogger(GoogleDriveServiceImpl.class);
	private Drive driveService;

	@Autowired
	private AuthorizationService authorizationService;

	@PostConstruct
	public void init() throws Exception {
		Credential credential = authorizationService.getCredentials();
		driveService = new Drive.Builder(ApplicationConstant.HTTP_TRANSPORT, ApplicationConstant.JSON_FACTORY,
				credential).setApplicationName(ApplicationConstant.APPLICATION_NAME).build();
	}

	@Override
	public void uploadFile(MultipartFile multipartFile, String folderID) throws IllegalStateException, IOException {
		logger.debug("Inside Upload Service...");

		String fileName = multipartFile.getOriginalFilename();
		String contentType = multipartFile.getContentType();
		java.io.File transferedFile = new java.io.File(fileName);
		multipartFile.transferTo(transferedFile);

		File fileMetadata = new File();
		fileMetadata.setName(fileName);
		FileContent mediaContent = new FileContent(contentType, transferedFile);
		File file = null;
		if (!folderID.equals("")) {
			fileMetadata.setParents(Collections.singletonList(folderID));
			file = driveService.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
		} else {
			file = driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
		}
		logger.debug("File ID: " + file.getName() + ", " + file.getId());
	}

	@Override
	public void downloadFiles(String fileID, HttpServletResponse response) throws IOException {
		File file=getRequiredFile(fileID);
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		driveService.files().get(file.getId()).executeMediaAndDownloadTo(byteOutStream);

		java.io.File hardfile = new java.io.File(file.getName());
		FileOutputStream fileOutputStream = new FileOutputStream(hardfile);
		byteOutStream.writeTo(fileOutputStream);

		response.setContentType(file.getMimeType());
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) hardfile.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(hardfile));
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	}

	private File getRequiredFile(String fileId) throws IOException {
		return driveService.files().get(fileId).execute();
	}

	@Override
	public boolean createFolder(String folderName, String parentID) throws IOException {

		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setMimeType(ApplicationConstant.FOLDER_MIME_TYPE);

		File file = null;
		if (!parentID.equals("")) {
			fileMetadata.setParents(Collections.singletonList(parentID));
			file = driveService.files().create(fileMetadata).setFields("id, parents").execute();
		} else {
			file = driveService.files().create(fileMetadata).setFields("id").execute();
		}
		if (null != file && null != file.getId())
			return true;
		return false;
	}

	public FolderBean getFiles() throws IOException, GeneralSecurityException {
		logger.debug("Inside getfiles Service...");
		List<File> files = driveService.files().list().setQ("'root' in parents").execute().getFiles();
		FolderBean tmp = buildFolderBean(files, "root", "");

		if (!files.isEmpty())
			return tmp;
		else
			return null;
	}

	private FolderBean buildFolderBean(List<File> files, String folderName, String folderID) throws IOException {
		FolderBean folderObj = new FolderBean(folderName, folderID);
		if (null != files && !files.isEmpty()) {
			for (File file : files) {
				if (ApplicationConstant.FOLDER_MIME_TYPE.equals(file.getMimeType())) {
					List<File> innerFiles = driveService.files().list().setQ("'" + file.getId() + "' in parents")
							.execute().getFiles();
					if (null != files && !files.isEmpty()) {
						folderObj.addSubfolders(buildFolderBean(innerFiles, file.getName(), file.getId()));
					} else {
						FolderBean subFolderObj = new FolderBean(folderName, folderID);
						folderObj.addSubfolders(subFolderObj);
					}
				} else {
					folderObj.addFiles(new FileBean(file.getName(), file.getId()));
				}
			}
		}
		return folderObj;
	}

}