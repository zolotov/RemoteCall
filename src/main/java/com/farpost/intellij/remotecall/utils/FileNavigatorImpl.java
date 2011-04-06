package com.farpost.intellij.remotecall.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

public class FileNavigatorImpl implements FileNavigator {

	private final Logger log = Logger.getInstance(getClass().getName());

	@Override
	public void findAndNavigate(final String fileName, final int line) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			public void run() {
				Project[] projects = ProjectManager.getInstance().getOpenProjects();
				for (Project project : projects) {
					PsiFile foundFiles[] = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
					if (foundFiles.length >= 1) {
						VirtualFile directFile = foundFiles[0].getVirtualFile();
						if (directFile == null) {
							continue;
						}

						log.info("Found file " + directFile.getName());
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

}
