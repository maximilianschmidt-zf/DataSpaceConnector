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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.isPublic;

public class ProviderMethodProxy {
    private final ServiceExtension target;

    private ProviderMethodProxy(ServiceExtension target) {
        this.target = target;
    }

    public static ProviderMethodProxy scanProviders(ServiceExtension target) {
        return new ProviderMethodProxy(target);
    }

    public ProviderMethodInvoker thenInvoke() {
        return new ProviderMethodInvoker(getProviderMethods(target), target);
    }

    public Set<Method> providerMethods() {
        return getProviderMethods(target).stream().filter(m -> !isDefaultProvider(m)).collect(Collectors.toSet());
    }

    public Set<Method> defaultProviderMethods() {
        return getProviderMethods(target).stream().filter(this::isDefaultProvider).collect(Collectors.toSet());
    }

    private Set<Method> getProviderMethods(ServiceExtension extension) {
        var methods = Arrays.stream(extension.getClass().getMethods())
                .filter(m -> m.getAnnotation(Provider.class) != null)
                .filter(m -> isPublic(m.getModifiers()))
                .collect(Collectors.toSet());

        if (methods.stream().anyMatch(m -> m.getReturnType() == Void.class)) {
            throw new EdcInjectionException("Methods annotated with @Provider must have a non-void return type!");
        }
        return methods;
    }

    private boolean isDefaultProvider(Method m) {
        return m.getAnnotation(Provider.class).isDefault();
    }
}
