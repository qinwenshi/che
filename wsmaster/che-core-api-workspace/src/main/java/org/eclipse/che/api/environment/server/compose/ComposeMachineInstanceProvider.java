package org.eclipse.che.api.environment.server.compose;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.util.LineConsumer;
import org.eclipse.che.api.environment.server.compose.model.ComposeService;
import org.eclipse.che.api.machine.server.spi.Instance;

/**
 * @author Alexander Garagatyi
 */
public interface ComposeMachineInstanceProvider {
    Instance startService(String namespace,
                                 String workspaceId,
                                 String envName,
                                 String machineId,
                                 String machineName,
                                 boolean isDev,
                                 String networkName,
                                 ComposeService service,
                                 LineConsumer machineLogger)
            throws ServerException;

    void startNetwork(String networkName) throws ServerException;

    void stopNetwork(String networkName) throws ServerException;
}
