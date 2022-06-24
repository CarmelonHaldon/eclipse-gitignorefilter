/*******************************************************************************
 * Copyright (c) 2010, 2015 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Mathias Kinzler (SAP AG) - initial implementation
 *******************************************************************************/
package es.personal.eclipse.gitignorefilter;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.egit.core.IteratorService;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;

/**
 * Utility class for handling Repositories in the UI.
 */
public class RepositoryUtil {

	/**
	 * Checks if existing resource with given path is to be ignored.
	 * <p>
	 * <b>Note:</b>The check makes sense only for files which exists in the working directory. This method returns false
	 * for paths to not existing files or directories.
	 *
	 * @param path Path to be checked, file or directory must exist on the disk
	 * @return true if the path is either not inside git repository or exists and matches an ignore rule
	 * @throws IOException
	 * @since 2.3
	 */
	public static boolean isIgnored(IPath path) throws IOException {
		RepositoryMapping mapping = RepositoryMapping.getMapping(path);
		if (mapping == null) {
//			return true; // Linked resources may not be mapped
			return false; // Linked resources may not be mapped
		}
		Repository repository = mapping.getRepository();
		WorkingTreeIterator treeIterator = IteratorService.createInitialIterator(repository);
		if (treeIterator == null) {
//			return true;
			return false;
		}
		String repoRelativePath = mapping.getRepoRelativePath(path);
		if (repoRelativePath == null || repoRelativePath.isEmpty()) {
//			return true;
			return false;
		}
		try (TreeWalk walk = new TreeWalk(repository)) {
			walk.addTree(treeIterator);
			walk.setFilter(PathFilterGroup.createFromStrings(repoRelativePath));
			while (walk.next()) {
				WorkingTreeIterator workingTreeIterator = walk.getTree(0, WorkingTreeIterator.class);
				if (walk.getPathString().equals(repoRelativePath)) {
					return workingTreeIterator.isEntryIgnored();
				}
				if (workingTreeIterator.getEntryFileMode().equals(FileMode.TREE)) {
					walk.enterSubtree();
				}
			}
		}
		return false;
	}
}
