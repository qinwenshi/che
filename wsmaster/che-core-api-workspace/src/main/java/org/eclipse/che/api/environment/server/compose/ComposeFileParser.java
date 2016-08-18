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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.core.model.workspace.EnvironmentRecipe;
import org.eclipse.che.api.environment.server.compose.model.ComposeEnvironment;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentImpl;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.lang.IoUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Alexander Garagatyi
 */
public class ComposeFileParser {
    private static final Logger       LOG         = getLogger(ComposeFileParser.class);
    private static final ObjectMapper YAML_PARSER = new ObjectMapper(new YAMLFactory());

    private final URI apiEndpoint;

    @Inject
    public ComposeFileParser(@Named("api.endpoint") URI apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public ComposeEnvironment parse(Environment environment) throws ServerException {
        // assert env
        // assert recipe
        // assert content type
        // assert content || location
        // assert type
        EnvironmentImpl envImpl = new EnvironmentImpl(environment);
        String recipeContent = getContentOfRecipe(environment.getRecipe());
        return parseEnvironmentRecipeContent(recipeContent,
                                             environment.getRecipe().getContentType());
    }

    private String getContentOfRecipe(EnvironmentRecipe environmentRecipe) throws ServerException {
        if (environmentRecipe.getContent() != null) {
            return environmentRecipe.getContent();
        } else {
            return getRecipe(environmentRecipe.getLocation());
        }
    }

    private ComposeEnvironment parseEnvironmentRecipeContent(String recipeContent, String contentType) {
        ComposeEnvironment composeEnvironment;
        switch (contentType) {
            case "application/x-yaml":
            case "text/yaml":
            case "text/x-yaml":
                try {
                    composeEnvironment = YAML_PARSER.readValue(recipeContent, ComposeEnvironment.class);
                } catch (IOException e) {
                    throw new IllegalArgumentException(
                            "Parsing of environment configuration failed. " + e.getLocalizedMessage());
                }
                break;
            default:
                throw new IllegalArgumentException("Provided environment recipe content type '" +
                                                   contentType +
                                                   "' is unsupported. Supported values are: application/x-yaml");
        }
        return composeEnvironment;
    }

    private String getRecipe(String location) throws ServerException {
        URL recipeUrl;
        File file = null;
        try {
            UriBuilder targetUriBuilder = UriBuilder.fromUri(location);
            // add user token to be able to download user's private recipe
            final String apiEndPointHost = apiEndpoint.getHost();
            final String host = targetUriBuilder.build().getHost();
            if (apiEndPointHost.equals(host)) {
                if (EnvironmentContext.getCurrent().getSubject() != null
                    && EnvironmentContext.getCurrent().getSubject().getToken() != null) {
                    targetUriBuilder.queryParam("token", EnvironmentContext.getCurrent().getSubject().getToken());
                }
            }
            recipeUrl = targetUriBuilder.build().toURL();
            file = IoUtil.downloadFileWithRedirect(null, "recipe", null, recipeUrl);

            return IoUtil.readAndCloseQuietly(new FileInputStream(file));
        } catch (IOException | IllegalArgumentException e) {
            throw new MachineException(format("Recipe downloading failed. Recipe url %s. Error: %s",
                                              location,
                                              e.getLocalizedMessage()));
        } finally {
            if (file != null && !file.delete()) {
                LOG.error(String.format("Removal of recipe file %s failed.", file.getAbsolutePath()));
            }
        }
    }
}
