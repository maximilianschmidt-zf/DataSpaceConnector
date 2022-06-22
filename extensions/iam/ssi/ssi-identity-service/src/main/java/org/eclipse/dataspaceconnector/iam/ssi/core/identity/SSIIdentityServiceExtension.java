/*
 * Copyright (c) 2022 ZF Friedrichshafen AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      ZF Friedrichshafen AG - Initial API and Implementation
 */

package org.eclipse.dataspaceconnector.iam.ssi.core.identity;

//import org.eclipse.dataspaceconnector.iam.ssi.config.ManagedIdentityWalletConfig;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.api.datamanagement.configuration.DataManagementApiConfiguration;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;


public class SSIIdentityServiceExtension implements ServiceExtension {

    @Override
    public String name() {
        return "SSI Identity Service";
    }

    @Inject
    WebService webService;
    @Inject
    private OkHttpClient okHttpClient;
    //@Inject
    //ManagedIdentityWalletConfig walletConfig;

    private static final String LOG_PREFIX_SETTING = "ssi.miw.logprefix";

    @Inject
    DataManagementApiConfiguration config;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var logPrefix = context.getSetting(LOG_PREFIX_SETTING, "MIW");
        var typeManager = context.getTypeManager();

        webService.registerResource(config.getContextAlias(),
                new SSIIdentityServiceImpl());
    }
}
