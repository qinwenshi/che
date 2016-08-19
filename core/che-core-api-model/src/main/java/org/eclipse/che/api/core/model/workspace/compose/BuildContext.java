package org.eclipse.che.api.core.model.workspace.compose;

/**
 * @author Alexander Garagatyi
 */
public interface BuildContext {
    String getContext();

    String getDockerfile();
}
