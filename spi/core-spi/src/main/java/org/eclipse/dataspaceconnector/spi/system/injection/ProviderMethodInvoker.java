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

import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class ProviderMethodInvoker {
    private final Set<Method> factoryMethods;
    private final Object target;

    public ProviderMethodInvoker(Set<Method> factoryMethods, ServiceExtension target) {
        this.factoryMethods = factoryMethods;
        this.target = target;
    }

    public void andRegister(ServiceExtensionContext context) {
        factoryMethods.forEach(m -> {
            var type = m.getReturnType();
            var obj = invokeProviderMethod(m, target, context);
            if (!context.hasService(type)) {
                context.registerServiceRaw(type, obj);
            }
        });
    }

    private Object invokeProviderMethod(Method method, Object target, ServiceExtensionContext context) {
        try {
            if (method.getParameterTypes().length == 0) {
                return method.invoke(target);
            }

            if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == ServiceExtensionContext.class) {
                return method.invoke(target, context);
            } else {
                throw new IllegalArgumentException("Factory methods can only have 0..1 arguments, and only accept a ServiceExtensionContext!");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new EdcInjectionException(e);
        }
    }
}
