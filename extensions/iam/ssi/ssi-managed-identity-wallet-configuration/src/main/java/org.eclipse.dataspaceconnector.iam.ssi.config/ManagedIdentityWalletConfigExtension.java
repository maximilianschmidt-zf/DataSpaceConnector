package org.eclipse.dataspaceconnector.iam.ssi.config;

import org.eclipse.dataspaceconnector.spi.WebServer;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

@Provides(ManagedIdentityWalletConfigExtension.class)
public class ManagedIdentityWalletConfigExtension implements ServiceExtension {

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
    private WebServer webServer;

    public String name() {
        return "MIW API Configuration";
    }

    public void initialize(ServiceExtensionContext context){
        var monitor = context.getMonitor();
        ManagedIdentityWalletConfig config = new ManagedIdentityWalletConfig.Builder()
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

        context.registerService(ManagedIdentityWalletConfig.class, config);
    }

}