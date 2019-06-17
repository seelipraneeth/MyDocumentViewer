package com.documentviewer.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.documentviewer.model.FileBean;
import com.documentviewer.model.FolderBean;
import com.documentviewer.model.UploadFile;
import com.documentviewer.service.AuthorizationService;
import com.documentviewer.service.GoogleDriveService;
import com.google.api.services.drive.model.File;

@Controller
public class MainController {

	private Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	AuthorizationService authorizationService;

	@Autowired
	GoogleDriveService googleDriveService;

	private FolderBean files = null;

	/**
	 * Handles the root request. Checks if user is already authenticated via SSO.
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/")
	public String showHomePage() throws Exception {
		if (authorizationService.isUserAuthenticated()) {
			logger.debug("User is authenticated. Redirecting to home...");
			return "redirect:/files";
		} else {
			logger.debug("User is not authenticated. Redirecting to sso...");
			return "redirect:/login";
		}
	}

	/**
	 * Directs to login
	 * 
	 * @return
	 */
	@RequestMapping("/login")
	public String goToLogin() {
		return "index";
	}

	/**
	 * Calls the Google OAuth service to authorize the app
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/googleSignIn")
	public void doGoogleSignIn(HttpServletResponse response) throws Exception {
		logger.debug("SSO Called...");
		response.sendRedirect(authorizationService.authenticateUserViaGoogle());
	}

	/**
	 * Applications Callback URI for redirection from Google auth server after user
	 * approval/consent
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/oauth/callback")
	public String saveAuthorizationCode(HttpServletRequest request) throws Exception {
		logger.debug("SSO Callback invoked...");
		String code = request.getParameter("code");
		logger.debug("SSO Callback Code Value..., " + code);
		if (code != null) {
			authorizationService.exchangeCodeForTokens(code);
			return "home";
		}
		return "index";
	}

	/**
	 * Handles logout
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) throws Exception {
		logger.debug("Logout invoked...");
		authorizationService.removeUserSession(request);
		return "redirect:/login";
	}

	/**
	 * Handles the files being uploaded to GDrive
	 * 
	 * @param request
	 * @param uploadedFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ModelAndView uploadFile(@ModelAttribute UploadFile uploadedFile,
			@RequestParam(value = "folderID2") String folderID, ModelAndView modelAndView) throws Exception {
		logger.debug("Uploading the given file");
		MultipartFile multipartFile = uploadedFile.getMultipartFile();
		googleDriveService.uploadFile(multipartFile, folderID);
		modelAndView.setViewName("files");
		files = googleDriveService.getFiles();
		modelAndView.addObject("folder", files);
		if (null != files && !folderID.equals("")) {
			if (!folderID.equals(""))
				modelAndView.addObject("folder", getSubfolderById(files, folderID));
			return modelAndView;
		}
		return modelAndView;
	}

	@RequestMapping(value = "/createFolder", method = RequestMethod.POST)
	public ModelAndView createFolder(@RequestParam(value = "folderName") String folderName,
			@RequestParam(value = "parentID") String parentID, ModelAndView modelAndView) throws Exception {
		logger.debug("Creating a folder, name: " + folderName + ", Parent id: " + parentID);

		modelAndView.setViewName("files");
		modelAndView.addObject("folder", files);
		modelAndView.addObject("currentFolderID", parentID);
		if (googleDriveService.createFolder(folderName, parentID)) {
			files = googleDriveService.getFiles();
			if (!parentID.equals("")) {
				FolderBean tmpFiles = getSubfolderById(files, parentID);
				modelAndView.addObject("folder", tmpFiles);
				return modelAndView;
			} else {
				modelAndView.addObject("folder", files);
				return modelAndView;
			}
		}

		return modelAndView;
	}

	@RequestMapping(value = "/downloadFile/{fileID}", method = RequestMethod.GET)
	public ModelAndView downloadFile(@PathVariable(value = "fileID") String fileID, ModelAndView modelAndView, HttpServletResponse response)
			throws Exception {
		String parentID = "";
		modelAndView.setViewName("files");
		modelAndView.addObject("folder", files);
		modelAndView.addObject("currentFolderID", parentID);

		googleDriveService.downloadFiles(fileID, response);
		if (!parentID.equals("")) {
			FolderBean tmpFiles = getSubfolderById(files, parentID);
			modelAndView.addObject("folder", tmpFiles);
			return modelAndView;
		}
		return modelAndView;
	}


	/**
	 * Handles to collect the files from GDrive
	 * 
	 * @return a new model with folder
	 * @throws Exception
	 */
	@RequestMapping(value = "/files", method = RequestMethod.GET)
	public ModelAndView getFilesList(ModelAndView modelAndView) throws IOException, GeneralSecurityException {
		logger.debug("getting folders and files");
		files = googleDriveService.getFiles();
		modelAndView.setViewName("files");
		modelAndView.addObject("folder", files);
		return modelAndView;
	}

	/**
	 * Handles to collect the files from GDrive
	 * 
	 * @param folder       id to be search in all the folders
	 * @param uploadedFile
	 * @return a new model with folder
	 * @throws Exception
	 */
	@RequestMapping(value = "/subFiles", method = RequestMethod.POST)
	private ModelAndView getSubfolder(@RequestParam(value = "folderID") String searchId, ModelAndView modelAndView)
			throws IOException, GeneralSecurityException {
		logger.debug("Searching folders to get subfolder on search id.");
		modelAndView.setViewName("files");
		if (null != files) {
			modelAndView.addObject("folder", getSubfolderById(files, searchId));
			modelAndView.addObject("currentFolderID", searchId);
			return modelAndView;
		}
		modelAndView.addObject("folder", files);
		return modelAndView;
	}

	/**
	 * Handles to return a subfolder on searchId(Recursive method to search deeper)
	 * 
	 * @param Root   folder
	 * @param folder id to be searched
	 * @return subfolder
	 */
	private FolderBean getSubfolderById(FolderBean folder, String searchId) {
		FolderBean subFolder = null;
		if (folder != null) {
			for (FolderBean tmpSubFolder : folder.getSubFolders()) {
				if (searchId.equals(tmpSubFolder.getFolderID())) {
					return tmpSubFolder;
				} else {
					return getSubfolderById(tmpSubFolder, searchId);
				}
			}
		}
		return subFolder;
	}
	
}