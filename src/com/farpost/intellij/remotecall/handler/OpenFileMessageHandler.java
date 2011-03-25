package com.farpost.intellij.remotecall.handler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiFile;

import java.io.File;

public class OpenFileMessageHandler implements MessageHandler {

	private final Logger log = Logger.getInstance(getClass().getName());

	public void handleMessage(final String message) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			public void run() {
				Project[] projects = ProjectManager.getInstance().getOpenProjects();
				String[] fileInfo = message.split(":", 2);
				String fileName = normalizeFileName(fileInfo[0]);
				int line = 0;
				if (fileInfo.length > 1) {
					try {
						line = Integer.parseInt(fileInfo[1]);
					} catch (NumberFormatException e) {
						log.info("Bad line number: " + fileInfo[1] + ". Open on the first line");
					}
				}
				for (Project project : projects) {
					PsiFile foundFiles[] = JavaPsiFacade.getInstance(project).getShortNamesCache().getFilesByName(fileName);
					if (foundFiles.length >= 1) {
						VirtualFile directFile = foundFiles[0].getVirtualFile();
						if (directFile == null) {
							continue;
						}

						log.info("Found file " + directFile.getName());
						if (line > 0) line--;
						final OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, directFile, line, 0);
						if (openFileDescriptor.canNavigate()) {
							log.info("Trying to navigate to " + directFile.getName() + ":" + line);
							openFileDescriptor.navigate(true);
							WindowManager.getInstance().suggestParentWindow(project).toFront();
						} else {
							log.info("Cannot navigate");
						}
					}
				}
			}
		});
	}

	private String normalizeFileName(String fileName) {
		if (fileName.contains(File.separator)) {
			File file = new File(fileName);
			fileName = file.getName();
		}

		return fileName;
	}
}
