package com.farpost.intellij.remotecall.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileNavigatorImpl implements FileNavigator {

	private final Logger log = Logger.getInstance(getClass().getName());

	@Override
	public void findAndNavigate(final String fileName, final int line) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			public void run() {
				Map<Project, Collection<VirtualFile>> foundFilesInAllProjects = new HashMap<Project, Collection<VirtualFile>>();
				Project[] projects = ProjectManager.getInstance().getOpenProjects();

				for (Project project : projects) {
					foundFilesInAllProjects.put(project, FilenameIndex.getVirtualFilesByName(project, new File(fileName).getName(), GlobalSearchScope.allScope(project)));
				}

				String variableFileName = fileName;
				int pathElementsCount = variableFileName.split(File.separator).length;
				for (int i = 0; i <= pathElementsCount; i++) {
					for (Project project : foundFilesInAllProjects.keySet()) {
						for (VirtualFile directFile : foundFilesInAllProjects.get(project)) {
							if (directFile.getPath().endsWith(variableFileName)) {
								log.info("Found file " + directFile.getName());
								navigate(project, directFile, line);
								return;
							}
						}
					}
					String extractedParent[] = variableFileName.split(File.separator, 2);
					variableFileName = extractedParent.length > 1 ? extractedParent[1] : extractedParent[0];
				}

			}
		});
	}

	private void navigate(Project project, VirtualFile file, int line) {
		final OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, file, line, 0);
		if (openFileDescriptor.canNavigate()) {
			log.info("Trying to navigate to " + file.getPath() + ":" + line);
			openFileDescriptor.navigate(true);
			WindowManager.getInstance().suggestParentWindow(project).toFront();
		} else {
			log.info("Cannot navigate");
		}
	}

}
