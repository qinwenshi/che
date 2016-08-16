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

import org.eclipse.che.api.environment.server.compose.model.ComposeEnvironment;
import org.eclipse.che.api.environment.server.compose.model.ComposeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * author Alexander Garagatyi
 */
public class ComposeServicesStartStrategy {
    public List<String> order(ComposeEnvironment composeEnvironment) throws IllegalArgumentException {
        // TODO if dev machine has the same weight as other machines put it in the head of queue
//        final MachineConfigImpl devCfg = removeFirstMatching(startConfigs, MachineConfig::isDev);
//        startConfigs.add(0, devCfg);
//        configs = new ArrayList<>(configs);

        // move start of dependent machines after machines they depends on
        Map<String, Integer> weights = weightMachines(composeEnvironment.getServices());

        return sortByWeight(composeEnvironment.getServices(), weights);
    }

    private Map<String, Integer> weightMachines(Map<String, ComposeService> services) throws IllegalArgumentException {
        HashMap<String, Integer> weights = new HashMap<>();
        Set<String> machinesLeft = services.keySet();

        // create dependency graph
        Map<String, List<String>> dependencies = new HashMap<>(services.size());
        for (Map.Entry<String, ComposeService> serviceEntry : services.entrySet()) {
            ComposeService service = serviceEntry.getValue();

            ArrayList<String> machineDependencies = new ArrayList<>(service.getDependsOn().size() +
                                                                    service.getLinks().size());

            machineDependencies.addAll(service.getDependsOn());

            for (String link : service.getLinks()) {
                machineDependencies.add(getServiceFromMachineLink(link));
            }
            dependencies.put(serviceEntry.getKey(), machineDependencies);
        }

        boolean weightEvaluatedInCycleRun = true;
        while (weights.size() != dependencies.size() && weightEvaluatedInCycleRun) {
            weightEvaluatedInCycleRun = false;
            for (String service : dependencies.keySet()) {
                // process not yet processed machines only
                if (machinesLeft.contains(service)) {
                    if (dependencies.get(service).size() == 0) {
                        // no links - smallest weight 0
                        weights.put(service, 0);
                        machinesLeft.remove(service);
                        weightEvaluatedInCycleRun = true;
                    } else {
                        // machine has depends on entry - check if it has not weighted connection
                        Optional<String> nonWeightedLink = dependencies.get(service)
                                                                       .stream()
                                                                       .filter(machinesLeft::contains)
                                                                       .findAny();
                        if (!nonWeightedLink.isPresent()) {
                            // all connections are weighted - lets evaluate current machine
                            Optional<String> maxWeight = dependencies.get(service)
                                                                     .stream()
                                                                     .max((o1, o2) -> weights.get(o1).compareTo(weights.get(o2)));
                            // optional can't empty because size of the list is checked above
                            //noinspection OptionalGetWithoutIsPresent
                            weights.put(service, weights.get(maxWeight.get()) + 1);
                            machinesLeft.remove(service);
                            weightEvaluatedInCycleRun = true;
                        }
                    }
                }
            }
        }

        if (weights.size() != services.size()) {
            throw new IllegalArgumentException("Launch order of machines " + machinesLeft + " can't be evaluated");
        }

        return weights;
    }

    private String getServiceFromMachineLink(String link) {
        String service = link;
        if (link != null) {
            String[] split = service.split(":");
            if (split.length != 1 && split.length != 2) {
                throw new IllegalArgumentException(format("Service link %s is invalid", link));
            }
            service = split[0];
        }
        return service;
    }

    private List<String> sortByWeight(Map<String, ComposeService> services,
                                                    Map<String, Integer> weights) {
        TreeMap<String, ComposeService> sortedServices =
                new TreeMap<>((o1, o2) -> weights.get(o1).compareTo(weights.get(o2)));

        sortedServices.putAll(services);

        return new ArrayList<>(sortedServices.keySet());
    }

    private static <T> T removeFirstMatching(List<? extends T> elements, Predicate<T> predicate) {
        T element = null;
        for (final Iterator<? extends T> it = elements.iterator(); it.hasNext() && element == null; ) {
            final T next = it.next();
            if (predicate.test(next)) {
                element = next;
                it.remove();
            }
        }
        return element;
    }
}
