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
import org.eclipse.dataspaceconnector.boot.system.injection.InjectorImpl;
import org.eclipse.dataspaceconnector.boot.util.TopologicalSort;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.monitor.ConsoleMonitor;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.monitor.MultiplexingMonitor;
import org.eclipse.dataspaceconnector.spi.security.CertificateResolver;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.BaseExtension;
import org.eclipse.dataspaceconnector.spi.system.CoreExtension;
import org.eclipse.dataspaceconnector.spi.system.MonitorExtension;
import org.eclipse.dataspaceconnector.spi.system.NullVaultExtension;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.VaultExtension;
import org.eclipse.dataspaceconnector.spi.system.injection.EdcInjectionException;
import org.eclipse.dataspaceconnector.spi.system.injection.InjectionContainer;
import org.eclipse.dataspaceconnector.spi.system.injection.InjectionPoint;
import org.eclipse.dataspaceconnector.spi.system.injection.InjectionPointScanner;
import org.eclipse.dataspaceconnector.spi.system.injection.ProviderMethod;
import org.eclipse.dataspaceconnector.spi.telemetry.Telemetry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.eclipse.dataspaceconnector.spi.system.injection.ProviderMethodScanner.scanProviders;

public class ExtensionLoader {

    private final InjectionPointScanner injectionPointScanner = new InjectionPointScanner();
    private final ServiceLocator serviceLocator;

    public ExtensionLoader(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    /**
     * Convenience method for loading service extensions.
     */
    public static void bootServiceExtensions(List<InjectionContainer<ServiceExtension>> containers, ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        var injector = new InjectorImpl();

        containers.forEach(container -> {
            // satisfy injection points
            injector.inject(container, context);

            // call initialize
            ServiceExtension target = container.getInjectionTarget();
            target.initialize(context);
            //todo: add verification here, that every @Provides corresponds to a .registerService call
            var result = container.validate(context);
            if (!result.succeeded()) {
                monitor.warning(format("There were missing service registrations in extension %s: %s", target.getClass(), String.join(", ", result.getFailureMessages())));
            }
            monitor.info("Initialized " + target.name());

            // invoke provider methods, register the service they return
            scanProviders(target)
                    .scan()
                    .forEach(pm -> invokeProviderMethod(pm, target, context));
        });

        containers.forEach(extension -> {
            extension.getInjectionTarget().start();
            monitor.info("Started " + extension.getInjectionTarget().name());
        });
    }

    private static void invokeProviderMethod(ProviderMethod m, ServiceExtension target, ServiceExtensionContext context) {
        var type = m.getReturnType();

        if (!m.isDefault() || (m.isDefault() && !context.hasService(type))) {
            var res = m.invoke(target, context);
            context.registerServiceRaw(type, res);
        }
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

        //the first sort is only to verify that there are no "upward" dependencies from PRIMORDIAL -> DEFAULT
        var sips = sortExtensions(serviceExtensions);

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

    private List<InjectionContainer<ServiceExtension>> sortExtensions(List<ServiceExtension> loadedExtensions) {
        Map<String, List<ServiceExtension>> dependencyMap = new HashMap<>();
        addDefaultExtensions(loadedExtensions);

        // add all provided features to the dependency map
        loadedExtensions.forEach(ext -> getDefaultProvidedFeatures(ext).forEach(feature -> dependencyMap.computeIfAbsent(feature, k -> new ArrayList<>()).add(ext)));
        loadedExtensions.forEach(ext -> getProvidedFeatures(ext).forEach(feature -> dependencyMap.computeIfAbsent(feature, k -> new ArrayList<>()).add(ext)));
        var sort = new TopologicalSort<ServiceExtension>();

        // check if all injected fields are satisfied, collect missing ones and throw exception otherwise
        var unsatisfiedInjectionPoints = new ArrayList<InjectionPoint<ServiceExtension>>();
        var injectionPoints = loadedExtensions.stream().flatMap(ext -> getInjectedFields(ext).stream().peek(injectionPoint -> {
            List<ServiceExtension> dependencies = dependencyMap.get(injectionPoint.getFeatureName());
            if (dependencies == null) {
                if (injectionPoint.isRequired()) {
                    unsatisfiedInjectionPoints.add(injectionPoint);
                }
            } else {
                dependencies.stream()
                        .filter(d -> !Objects.equals(d, ext)) // remove dependencies onto oneself
                        .forEach(dependency -> sort.addDependency(ext, dependency));
            }
        })).collect(Collectors.toList());

        //throw an exception if still unsatisfied links
        if (!unsatisfiedInjectionPoints.isEmpty()) {
            var string = "The following injected fields were not provided:\n";
            string += unsatisfiedInjectionPoints.stream().map(InjectionPoint::toString).collect(Collectors.joining("\n"));
            throw new EdcInjectionException(string);
        }

        //check that all the @Required features are there
        var unsatisfiedRequirements = new ArrayList<String>();
        loadedExtensions.forEach(ext -> {
            var features = getRequiredFeatures(ext.getClass());
            features.forEach(feature -> {
                var dependencies = dependencyMap.get(feature);
                if (dependencies == null) {
                    unsatisfiedRequirements.add(feature);
                } else {
                    dependencies.forEach(dependency -> sort.addDependency(ext, dependency));
                }
            });
        });

        if (!unsatisfiedRequirements.isEmpty()) {
            var string = String.format("The following @Require'd features were not provided: [%s]", String.join(", ", unsatisfiedRequirements));
            throw new EdcException(string);
        }

        sort.sort(loadedExtensions);

        // todo: should the list of InjectionContainers be generated directly by the flatmap?
        // convert the sorted list of extensions into an equally sorted list of InjectionContainers
        return loadedExtensions.stream().map(se -> new InjectionContainer<>(se, injectionPoints.stream().filter(ip -> ip.getInstance() == se).collect(Collectors.toSet()))).collect(Collectors.toList());
    }

    private Set<String> getRequiredFeatures(Class<?> clazz) {
        var requiresAnnotation = clazz.getAnnotation(Requires.class);
        if (requiresAnnotation != null) {
            var features = requiresAnnotation.value();
            return Stream.of(features).map(Class::getName).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * Obtains all features a specific extension requires as strings
     */
    private Set<String> getProvidedFeatures(ServiceExtension ext) {
        var allProvides = new HashSet<String>();

        // check all @Provides
        var providesAnnotation = ext.getClass().getAnnotation(Provides.class);
        if (providesAnnotation != null) {
            var featureStrings = Arrays.stream(providesAnnotation.value()).map(Class::getName).collect(Collectors.toSet());
            allProvides.addAll(featureStrings);
        }
        // check all @Provider methods
        allProvides.addAll(scanProviders(ext).providerMethods().stream().map(ProviderMethod::getReturnType).map(Class::getName).collect(Collectors.toSet()));
        return allProvides;
    }

    private Set<String> getDefaultProvidedFeatures(ServiceExtension ext) {
        return scanProviders(ext).defaultProviderMethods().stream()
                .map(ProviderMethod::getReturnType)
                .map(Class::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Obtains all features a specific extension provides as strings
     */
    private Set<InjectionPoint<ServiceExtension>> getInjectedFields(ServiceExtension ext) {
        // initialize with legacy list
        return injectionPointScanner.getInjectionPoints(ext);
    }

    /**
     * Handles core-, transfer- and contract-extensions and inserts them at the beginning of the list so that
     * explicit @Requires annotations are not necessary
     */
    private void addDefaultExtensions(List<ServiceExtension> loadedExtensions) {
        var baseDependencies = loadedExtensions.stream().filter(e -> e.getClass().getAnnotation(BaseExtension.class) != null).collect(Collectors.toList());
        if (baseDependencies.isEmpty()) {
            throw new EdcException("No base dependencies were found on the classpath. Please add the \"core:base\" module to your classpath!");
        }
        var coreDependencies = loadedExtensions.stream().filter(e -> e.getClass().getAnnotation(CoreExtension.class) != null).collect(Collectors.toList());

        //make sure the core- and transfer-dependencies are ALWAYS ordered first
        loadedExtensions.removeAll(baseDependencies);
        loadedExtensions.removeAll(coreDependencies);
        coreDependencies.forEach(se -> loadedExtensions.add(0, se));
        baseDependencies.forEach(se -> loadedExtensions.add(0, se));
    }
}
