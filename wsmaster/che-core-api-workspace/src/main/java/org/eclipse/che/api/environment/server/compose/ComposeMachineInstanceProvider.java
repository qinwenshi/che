/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.environment.server.compose;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.util.LineConsumer;
import org.eclipse.che.api.environment.server.compose.model.ComposeServiceImpl;
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
                                 ComposeServiceImpl service,
                                 LineConsumer machineLogger)
            throws ServerException;

    void startNetwork(String networkName) throws ServerException;

    void stopNetwork(String networkName) throws ServerException;
}
