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
package org.eclipse.che.api.environment.server.compose.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexander Garagatyi
 */
public class ComposeEnvironment {
    private String                      version;
    private Map<String, ComposeService> services;

    public ComposeEnvironment() {}

    public ComposeEnvironment(ComposeEnvironment recipeContent) {
        version = recipeContent.getVersion();
        services = recipeContent.getServices()
                                .entrySet()
                                .stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                                          entry -> new ComposeService(entry.getValue())));
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, ComposeService> getServices() {
        if (services == null) {
            services = new HashMap<>();
        }
        return services;
    }

    public void setServices(Map<String, ComposeService> services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComposeEnvironment)) return false;
        ComposeEnvironment that = (ComposeEnvironment)o;
        return Objects.equals(version, that.version) &&
               Objects.equals(services, that.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, services);
    }

    @Override
    public String toString() {
        return "ComposeEnvironment{" +
               "version='" + version + '\'' +
               ", services=" + services +
               '}';
    }
}
