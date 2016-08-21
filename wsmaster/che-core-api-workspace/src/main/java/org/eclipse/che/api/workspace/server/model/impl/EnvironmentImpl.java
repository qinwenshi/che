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
package org.eclipse.che.api.workspace.server.model.impl;

import org.eclipse.che.api.core.model.workspace.Environment;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Data object for {@link Environment}.
 *
 * @author Yevhenii Voevodin
 */
public class EnvironmentImpl implements Environment {
    private EnvironmentRecipeImpl            recipe;
    private Map<String, ExtendedMachineImpl> machines;

    public EnvironmentImpl(EnvironmentRecipeImpl recipe,
                           Map<String, ExtendedMachineImpl> machines) {
        this.recipe = recipe;
        this.machines = machines;
    }

    public EnvironmentImpl(Environment environment) {
        this.recipe = new EnvironmentRecipeImpl(environment.getRecipe());
        this.machines = environment.getMachines()
                                   .entrySet()
                                   .stream()
                                   .collect(Collectors.toMap(Map.Entry::getKey,
                                                             entry -> new ExtendedMachineImpl(entry.getValue())));
    }

    public EnvironmentRecipeImpl getRecipe() {
        return recipe;
    }

    public void setRecipe(EnvironmentRecipeImpl environmentRecipe) {
        this.recipe = environmentRecipe;
    }

    @Override
    public Map<String, ExtendedMachineImpl> getMachines() {
        return machines;
    }

    public void setMachines(Map<String, ExtendedMachineImpl> machines) {
        this.machines = machines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnvironmentImpl)) return false;
        EnvironmentImpl that = (EnvironmentImpl)o;
        return Objects.equals(recipe, that.recipe) &&
               Objects.equals(machines, that.machines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipe, machines);
    }
}
