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

package org.eclipse.dataspaceconnector.boot.system.injection;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.injection.InjectionContainer;
import org.eclipse.dataspaceconnector.spi.system.injection.Injector;
import org.eclipse.dataspaceconnector.spi.system.injection.ProviderMethod;

import static java.lang.String.format;
import static org.eclipse.dataspaceconnector.spi.system.injection.ProviderMethodScanner.scanProviders;

public class ExtensionLifecycleManager {
    private final InjectionContainer<ServiceExtension> container;
    private final ServiceExtensionContext context;
    private final Injector injector;
    private final Monitor monitor;

    public ExtensionLifecycleManager(InjectionContainer<ServiceExtension> container, ServiceExtensionContext context, Injector injector) {
        monitor = context.getMonitor();
        this.container = container;
        this.context = context;
        this.injector = injector;
    }

    public static void start(ExtensionStarter starter) {
        starter.start();
    }

    /**
     * Injects all dependencies into a {@link ServiceExtension}: those dependencies must be class members annotated with @Inject.
     */
    public void injectDependencies() {
        // satisfy injection points
        injector.inject(container, context);
    }

    /**
     * Invokes the {@link ServiceExtensionContext#initialize()} method and validates, that every type provided with @Provides
     * is actually provided, logs a warning otherwise
     */
    public void initialize() {

        // call initialize
        var target = container.getInjectionTarget();
        target.initialize(context);
        var result = container.validate(context);

        // wrap failure message in a more descriptive string
        if (result.failed()) {
            monitor.warning(String.join(", ", format("There were missing service registrations in extension %s: %s", target.getClass(), String.join(", ", result.getFailureMessages()))));
        }
        monitor.info("Initialized " + container.getInjectionTarget().name());
    }

    /**
     * Scans the {@linkplain ServiceExtension} for methods annotated with {@linkplain org.eclipse.dataspaceconnector.spi.system.Provider},
     * invokes them and registers the bean into the {@link ServiceExtensionContext} if necessary.
     */
    public ExtensionStarter registerProviders() {
        var target = container.getInjectionTarget();
        // invoke provider methods, register the service they return
        scanProviders(target)
                .all()
                .forEach(pm -> invokeAndRegister(pm, target, context));
        return new ExtensionStarter(container.getInjectionTarget(), monitor);
    }

    private void invokeAndRegister(ProviderMethod m, ServiceExtension target, ServiceExtensionContext context) {
        var type = m.getReturnType();

        if (!m.isDefault() || (m.isDefault() && !context.hasService(type))) {
            var res = m.invoke(target, context);
            context.registerServiceRaw(type, res);
        }
    }

    public static class ExtensionStarter {
        private final ServiceExtension injectionTarget;
        private final Monitor monitor;


        public ExtensionStarter(ServiceExtension injectionTarget, Monitor monitor) {
            this.injectionTarget = injectionTarget;
            this.monitor = monitor;
        }

        /**
         * Starts a {@link ServiceExtension}. This should only be done <em>after</em> the initialization phase is complete
         */
        private void start() {
            injectionTarget.start();
            monitor.info("Started " + injectionTarget.name());
        }

    }
}
