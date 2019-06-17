package com.documentviewer.config;

import org.springframework.core.io.Resource;

public class ApplicationConfig {

	private String CALLBACK_URI;
	private Resource driveSecretKeys;
	private Resource credentialsFolder;
	private String temporaryFolder;

	public String getCALLBACK_URI() {
		return CALLBACK_URI;
	}

	public void setCALLBACK_URI(String CALLBACK_URI_p) {
		CALLBACK_URI = CALLBACK_URI_p;
	}

	public Resource getDriveSecretKeys() {
		return driveSecretKeys;
	}

	public void setDriveSecretKeys(Resource driveSecretKeys) {
		this.driveSecretKeys = driveSecretKeys;
	}

	public Resource getCredentialsFolder() {
		return credentialsFolder;
	}

	public void setCredentialsFolder(Resource credentialsFolder) {
		this.credentialsFolder = credentialsFolder;
	}

	public String getTemporaryFolder() {
		return temporaryFolder;
	}

	public void setTemporaryFolder(String temporaryFolder) {
		this.temporaryFolder = temporaryFolder;
	}
}
