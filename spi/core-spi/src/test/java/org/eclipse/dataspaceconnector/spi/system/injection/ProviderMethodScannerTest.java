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
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProviderMethodScannerTest {


    @Test
    void all() {
        assertThat(ProviderMethodScanner
                .scanProviders(new TestExtension())
                .all())
                .hasSize(3);
    }

    @Test
    void providerMethods() {
        assertThat(ProviderMethodScanner
                .scanProviders(new TestExtension())
                .nonDefaultProviders())
                .hasSize(2);

    }

    @Test
    void defaultProviderMethods() throws NoSuchMethodException {
        assertThat(ProviderMethodScanner
                .scanProviders(new TestExtension())
                .defaultProviders())
                .hasSize(1)
                .extracting(ProviderMethod::getMethod)
                .containsOnly(TestExtension.class.getMethod("providerDefault"));
    }

    @Test
    void verifyInvalidReturnType() {
        assertThatThrownBy(() -> ProviderMethodScanner.scanProviders(new InvalidTestExtension()).nonDefaultProviders()).isInstanceOf(EdcInjectionException.class);
        assertThatThrownBy(() -> ProviderMethodScanner.scanProviders(new InvalidTestExtension()).defaultProviders()).isInstanceOf(EdcInjectionException.class);
    }

    @Test
    void verifyInvalidVisibility() {
        assertThatThrownBy(() -> ProviderMethodScanner.scanProviders(new InvalidTestExtension2()).nonDefaultProviders()).isInstanceOf(EdcInjectionException.class);
        assertThatThrownBy(() -> ProviderMethodScanner.scanProviders(new InvalidTestExtension2()).defaultProviders()).isInstanceOf(EdcInjectionException.class);
    }

    private static class TestExtension implements ServiceExtension {
        public void someMethod() {

        }

        @Provider
        public Object providerMethodWithArg(ServiceExtensionContext context) {
            return new Object();
        }

        @Provider
        public String provider() {
            return "";
        }

        @Provider(isDefault = true)
        public String providerDefault() {
            return "";
        }
    }

    private static class InvalidTestExtension extends TestExtension {
        @Provider
        public void invalidProvider() {

        }
    }

    private static class InvalidTestExtension2 extends TestExtension {
        @Provider
        Object invalidProvider() {
            return new Object();
        }
    }

}