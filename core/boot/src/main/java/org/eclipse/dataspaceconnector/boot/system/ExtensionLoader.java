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
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 */

package org.eclipse.dataspaceconnector.boot.system;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import org.eclipse.dataspaceconnector.boot.system.injection.ExtensionLifecycleManager;
import org.eclipse.dataspaceconnector.boot.system.injection.InjectorImpl;
import org.eclipse.dataspaceconnector.spi.monitor.ConsoleMonitor;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.monitor.MultiplexingMonitor;
import org.eclipse.dataspaceconnector.spi.security.CertificateResolver;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.MonitorExtension;
import org.eclipse.dataspaceconnector.spi.system.NullVaultExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.VaultExtension;
import org.eclipse.dataspaceconnector.spi.system.injection.InjectionContainer;
import org.eclipse.dataspaceconnector.spi.telemetry.Telemetry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ExtensionLoader {


    private final ServiceLocator serviceLocator;

    public ExtensionLoader(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    /**
     * Convenience method for loading service extensions.
     */
    public static void bootServiceExtensions(List<InjectionContainer<ServiceExtension>> containers, ServiceExtensionContext context) {
        var injector = new InjectorImpl();


        var lifeCycles = containers.stream()
                .map(c -> new ExtensionLifecycleManager(c, context, injector))
                .peek(ExtensionLifecycleManager::injectDependencies)
                .peek(ExtensionLifecycleManager::initialize)
                .map(ExtensionLifecycleManager::registerProviders)
                .collect(Collectors.toList());

        lifeCycles.forEach(ExtensionLifecycleManager::start);
    }


    /**
     * Loads a vault extension.
     */
    public static void loadVault(ServiceExtensionContext context, ExtensionLoader loader) {
        VaultExtension vaultExtension = loader.loadSingletonExtension(VaultExtension.class, false);
        if (vaultExtension == null) {
            vaultExtension = new NullVaultExtension();
            context.getMonitor().info("Secrets vault not configured. Defaulting to null vault.");
        }
        vaultExtension.initialize(context.getMonitor());
        context.getMonitor().info("Initialized " + vaultExtension.name());
        vaultExtension.initializeVault(context);
        context.registerService(Vault.class, vaultExtension.getVault());
        context.registerService(PrivateKeyResolver.class, vaultExtension.getPrivateKeyResolver());
        context.registerService(CertificateResolver.class, vaultExtension.getCertificateResolver());
    }

    public static @NotNull Monitor loadMonitor() {
        var loader = ServiceLoader.load(MonitorExtension.class);
        return loadMonitor(loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList()));
    }

    public static @NotNull Telemetry loadTelemetry() {
        var loader = ServiceLoader.load(OpenTelemetry.class);
        var openTelemetries = loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
        return new Telemetry(selectOpenTelemetryImpl(openTelemetries));
    }

    static @NotNull Monitor loadMonitor(List<MonitorExtension> availableMonitors) {
        if (availableMonitors.isEmpty()) {
            return new ConsoleMonitor();
        }

        if (availableMonitors.size() > 1) {
            return new MultiplexingMonitor(availableMonitors.stream().map(MonitorExtension::getMonitor).collect(Collectors.toList()));
        }

        return availableMonitors.get(0).getMonitor();
    }

    static @NotNull OpenTelemetry selectOpenTelemetryImpl(List<OpenTelemetry> openTelemetries) {
        if (openTelemetries.size() > 1) {
            throw new IllegalStateException(String.format("Found %s OpenTelemetry implementations. Please provide only one OpenTelemetry service provider.", openTelemetries.size()));
        }
        return openTelemetries.isEmpty() ? GlobalOpenTelemetry.get() : openTelemetries.get(0);
    }

    /**
     * Loads and orders the service extensions.
     */
    public List<InjectionContainer<ServiceExtension>> loadServiceExtensions() {
        List<ServiceExtension> serviceExtensions = loadExtensions(ServiceExtension.class, true);

        var sips = new DependencyGraph().of(serviceExtensions);

        return Collections.unmodifiableList(sips);
    }

    /**
     * Loads multiple extensions, raising an exception if at least one is not found.
     */
    public <T> List<T> loadExtensions(Class<T> type, boolean required) {
        return serviceLocator.loadImplementors(type, required);
    }

    /**
     * Loads a single extension, raising an exception if one is not found.
     */
    @Nullable()
    @Contract("_, true -> !null")
    public <T> T loadSingletonExtension(Class<T> type, boolean required) {
        return serviceLocator.loadSingletonImplementor(type, required);
    }


}
