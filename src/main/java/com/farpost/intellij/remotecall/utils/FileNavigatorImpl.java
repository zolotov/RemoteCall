package com.farpost.intellij.remotecall.utils;

import com.google.common.base.Joiner;
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
import java.util.*;

public class FileNavigatorImpl implements FileNavigator {

	private static final Logger log = Logger.getInstance(FileNavigatorImpl.class);
	private static final Joiner pathJoiner = Joiner.on("/");

	@Override
	public void findAndNavigate(final String fileName, final int line, final int column) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			public void run() {
				Map<Project, Collection<VirtualFile>> foundFilesInAllProjects = new HashMap<Project, Collection<VirtualFile>>();
				Project[] projects = ProjectManager.getInstance().getOpenProjects();

				for (Project project : projects) {
					foundFilesInAllProjects.put(project, FilenameIndex.getVirtualFilesByName(project, new File(fileName).getName(), GlobalSearchScope.allScope(project)));
				}

				Deque<String> pathElements = splitPath(fileName);
				String variableFileName = pathJoiner.join(pathElements);

				while (pathElements.size() > 0) {
					for (Project project : foundFilesInAllProjects.keySet()) {
						for (VirtualFile directFile : foundFilesInAllProjects.get(project)) {
							if (directFile.getPath().endsWith(variableFileName)) {
								log.info("Found file " + directFile.getName());
								navigate(project, directFile, line, column);
								return;
							}
						}
					}
					pathElements.pop();
					variableFileName = pathJoiner.join(pathElements);
				}
			}
		});
	}

	private Deque<String> splitPath(String filePath) {
		File file = new File(filePath);
		Deque<String> pathParts = new ArrayDeque<String>();
		pathParts.push(file.getName());
		while ((file = file.getParentFile()) != null && !file.getName().isEmpty()) {
			pathParts.push(file.getName());
		}

		return pathParts;
	}

	private void navigate(Project project, VirtualFile file, int line, int column) {
		final OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, file, line, column);
		if (openFileDescriptor.canNavigate()) {
			log.info("Trying to navigate to " + file.getPath() + ":" + line);
			openFileDescriptor.navigate(true);
			WindowManager.getInstance().suggestParentWindow(project).toFront();
		} else {
			log.info("Cannot navigate");
		}
	}

}
