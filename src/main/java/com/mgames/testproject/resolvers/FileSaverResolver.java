/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mgames.testproject.resolvers;

import com.mgames.testproject.Variables;
import com.mgames.utils.filesaver.AmazonS3FileSaver;
import com.mgames.utils.filesaver.FileSaver;
import com.mgames.utils.filesaver.LocalFileSaver;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Constantine Tretyak
 */
@Component
public class FileSaverResolver {
	@Value("${amazonS3AccessKey}")
	private String amazonS3AccessKey;

	@Value(value = "${amazonS3Bucket}")
	private String amazonS3Bucket;

	@Value("${amazonS3RootFolder}")
	private String amazonS3RootFolder;

	@Value("${amazonS3RootURL}")
	private String amazonS3RootURL;

	@Value("${amazonS3SecretKey}")
	private String amazonS3SecretKey;

	private FileSaver fileSaver;

	@Autowired
	private Variables vars;

	public FileSaver get() {
		if (fileSaver == null) {
			if (vars.RESOURCES_USE_AMAZON_S3) {
				fileSaver = new AmazonS3FileSaver(() -> amazonS3RootFolder,
												  () -> amazonS3RootURL,
												  amazonS3AccessKey,
												  amazonS3SecretKey,
												  amazonS3Bucket
				);
			} else {
				fileSaver = new LocalFileSaver(() -> vars.RESOURCES_FOLDER, () -> vars.RESOURCES_HTTP_PATH);
			}
		}

		return fileSaver;
	}

	@PostConstruct
	private void postConstruct() {
		vars.addChangeVariableListener((String name, Object oldValue, Object newValue) -> {
			switch (name) {
				case "RESOURCES_USE_AMAZON_S3":
					fileSaver = null;
					break;
			}
		});
	}

}
