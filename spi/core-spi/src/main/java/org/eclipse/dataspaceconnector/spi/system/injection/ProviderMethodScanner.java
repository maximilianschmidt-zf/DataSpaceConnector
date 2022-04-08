/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.spi.system.injection;

import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.isPublic;

public class ProviderMethodScanner {
    private final ServiceExtension target;

    private ProviderMethodScanner(ServiceExtension target) {
        this.target = target;
    }

    public static ProviderMethodScanner scanProviders(ServiceExtension target) {
        return new ProviderMethodScanner(target);
    }

    public Set<ProviderMethod> all() {
        return getProviderMethods(target);
    }

    public Set<ProviderMethod> nonDefaultProviders() {
        return getProviderMethods(target).stream().filter(pm -> !pm.isDefault()).collect(Collectors.toSet());
    }

    public Set<ProviderMethod> defaultProviders() {
        return getProviderMethods(target).stream().filter(ProviderMethod::isDefault).collect(Collectors.toSet());
    }

    private Set<ProviderMethod> getProviderMethods(ServiceExtension extension) {
        var methods = Arrays.stream(extension.getClass().getDeclaredMethods())
                .filter(m -> m.getAnnotation(Provider.class) != null)
                .map(ProviderMethod::new)
                .collect(Collectors.toSet());

        if (methods.stream().anyMatch(m -> m.getReturnType().equals(Void.TYPE))) {
            throw new EdcInjectionException("Methods annotated with @Provider must have a non-void return type!");
        }
        if (methods.stream().anyMatch(m -> !isPublic(m.getMethod().getModifiers()))) {
            throw new EdcInjectionException("Methods annotated with @Provider must be public!");
        }
        return methods;
    }

}
