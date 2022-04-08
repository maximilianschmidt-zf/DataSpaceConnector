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

package org.eclipse.dataspaceconnector.boot.system;

import org.assertj.core.data.Index;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.injection.EdcInjectionException;
import org.eclipse.dataspaceconnector.spi.system.injection.InjectionContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DependencyGraphTest {

    private DependencyGraph sorter;

    @BeforeEach
    void setUp() {
        sorter = new DependencyGraph();
    }

    @Test
    void sortExtensions_withDefaultProvider() {
        var providerExtension = createProviderExtension(true);

        var dependentExtension = createDependentExtension(true);

        var list = sorter.of(of(dependentExtension, providerExtension));
        assertThat(list).extracting(InjectionContainer::getInjectionTarget)
                .contains(providerExtension, Index.atIndex(2))
                .contains(dependentExtension, Index.atIndex(3));

    }

    @Test
    void sortExtensions_withNoDefaultProvider() {
        var defaultProvider = createProviderExtension(false);
        var provider = createProviderExtension(true);
        var dependentExtension = createDependentExtension(true);

        var list = sorter.of(of(dependentExtension, provider, defaultProvider));
        assertThat(list).extracting(InjectionContainer::getInjectionTarget)
                .contains(provider, Index.atIndex(2))
                .contains(defaultProvider, Index.atIndex(3))
                .contains(dependentExtension, Index.atIndex(4));
    }

    @Test
    void sortExtensions_missingDependency() {

        var dependentExtension = createDependentExtension(true);
        assertThatThrownBy(() -> sorter.of(of(dependentExtension))).isInstanceOf(EdcInjectionException.class);
    }

    @Test
    void sortExtensions_missingOptionalDependency() {

        var dependentExtension = createDependentExtension(false);
        assertThat(sorter.of(of(dependentExtension))).hasSize(3)
                .extracting(InjectionContainer::getInjectionTarget)
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(dependentExtension);
    }

    private List<ServiceExtension> of(ServiceExtension... extensions) {
        var l = new ArrayList<>(List.of(extensions));
        l.add(new CoreExtension());
        l.add(new BaseExtension());
        return l;
    }

    private ServiceExtension createProviderExtension(boolean isDefault) {

        return isDefault ? new DefaultProviderExtension() : new ProviderExtension();
    }

    private ServiceExtension createDependentExtension(boolean isRequired) {

        return isRequired ? new RequiredDependentExtension() : new DependentExtension();
    }

    private static class TestObject {
        private final String description;

        private TestObject(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


    private static class ProviderExtension implements ServiceExtension {
        @Provider
        public TestObject testObject() {
            return new TestObject("foobar");
        }
    }

    private static class DefaultProviderExtension implements ServiceExtension {
        @Provider(isDefault = true)
        public TestObject testObject() {
            return new TestObject("barbaz");
        }
    }

    private static class RequiredDependentExtension implements ServiceExtension {
        @Inject
        private TestObject testObject;
    }

    private static class DependentExtension implements ServiceExtension {
        @Inject(required = false)
        private TestObject testObject;
    }

    @org.eclipse.dataspaceconnector.spi.system.CoreExtension
    private static class CoreExtension implements ServiceExtension {
    }

    @org.eclipse.dataspaceconnector.spi.system.BaseExtension
    private static class BaseExtension implements ServiceExtension {
    }
}