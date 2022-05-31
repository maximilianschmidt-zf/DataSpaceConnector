/*
 *  Copyright (c) 2022 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       ZF Friefrichshafen AG - Initial implementation
 *
 */

package org.eclipse.dataspaceconnector.iam.ssi.wallet;

//import org.eclipse.dataspaceconnector.iam.ssi.config.ManagedIdentityWalletConfig;
import org.eclipse.dataspaceconnector.api.datamanagement.configuration.DataManagementApiConfiguration;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;


public class ManagedIdentityWalletExtension implements ServiceExtension {

    @Override
    public String name() {
        return "Managed Identity Wallets";
    }

    @Inject
    WebService webService;
    //@Inject
    //ManagedIdentityWalletConfig walletConfig;

    private static final String LOG_PREFIX_SETTING = "ssi.miw.logprefix";

    /**
     * Keycloak Settings
     */
    private final String KEYCLOAK_URL = "ssi.miw.keycloak.url";
    private final String KEYCLOAK_USERNAME = "ssi.miw.keycloak.username";
    private final String KEYCLOAK_CLIENT_ID = "ssi.miw.keycloak.client_id";
    private final String KEYCLOAK_CLIENT_SECRET = "ssi.miw.keycloak.client_secret";
    private final String KEYCLOAK_CLIENT_GRAND_TYPE = "ssi.miw.keycloak.grand_type";
    private final String KEYCLOAK_SCOPE = "ssi.miw.keycloak.scope";

    /**
     * Access Token Settings
     */
    private final String ACCESSTOKEN_URL = "ssi.miw.accesstoken.url";

    /**
     * Connection Settings
     */
    private final String WALLET_URL = "ssi.miw.url";
    private final String WALLET_JWKS_URL = "ssi.miw.url";
    private final String WALLET_ISSUER_URL = "ssi.miw.url";
    @Inject
    DataManagementApiConfiguration config;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var logPrefix = context.getSetting(LOG_PREFIX_SETTING, "MIW");

        ManagedIdentityWalletConfig walletConfig = new ManagedIdentityWalletConfig.Builder()
                .walletURL(context.getConfig(WALLET_URL).toString())
                .walletJwksURL(context.getConfig(WALLET_JWKS_URL).toString())
                .walletIssuerURL(context.getConfig(WALLET_ISSUER_URL).toString())
                .accessTokenURL(context.getConfig(ACCESSTOKEN_URL).toString())
                .keycloakURL(context.getConfig(KEYCLOAK_URL).toString())
                .keycloakClientID(context.getConfig(KEYCLOAK_CLIENT_ID).toString())
                .keycloakClientSecret(context.getConfig(KEYCLOAK_CLIENT_SECRET).toString())
                .keycloakGrandType(context.getConfig(KEYCLOAK_CLIENT_GRAND_TYPE).toString())
                .keycloakScope(context.getConfig(KEYCLOAK_SCOPE).toString())
                .build();

        webService.registerResource(config.getContextAlias(),new ManagedIdentityWalletApiController(context.getMonitor(), logPrefix, walletConfig));
    }
}
