package es.personal.eclipse.gitignorefilter;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class GitignoreFilter extends ViewerFilter {

	public boolean select(Viewer arg0, Object arg1, Object element) {

		// if (element instanceof ICompilationUnit) {
		// try {
		// ICompilationUnit cu = (ICompilationUnit) element;
		// if (cu.getUnderlyingResource() != null) {
		// return !ROO_AJ_PATTERN.matcher(cu.getUnderlyingResource().getName()).matches();
		// }
		// }
		// catch (JavaModelException e) {
		// // just ignore this here
		// }
		// }
		// return true;

		if (!(element instanceof IResource)) {
			return true;
		}

		IResource resource = (IResource) element;

		try {
			return !RepositoryUtil.isIgnored(resource.getLocation());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
