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
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;

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

				Deque<String> pathElements = splitPath(fileName);
				String variableFileName = StringUtils.join(pathElements, File.separator);

				while (pathElements.size() > 0) {
					for (Project project : foundFilesInAllProjects.keySet()) {
						for (VirtualFile directFile : foundFilesInAllProjects.get(project)) {
							if (directFile.getPath().endsWith(variableFileName)) {
								log.info("Found file " + directFile.getName());
								navigate(project, directFile, line);
								return;
							}
						}
					}
					pathElements.pop();
					variableFileName = StringUtils.join(pathElements, File.separator);
				}
			}
		});
	}

	private Deque<String> splitPath(String filePath) {
		File file = new File(filePath);
		Deque<String> pathParts = new ArrayDeque<String>();
		pathParts.push(file.getName());
		while ((file = file.getParentFile()) != null) {
			pathParts.push(file.getName());
		}

		return pathParts;
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
