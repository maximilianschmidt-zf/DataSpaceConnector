package org.eclipse.dataspaceconnector.iam.ssi.wallet;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
//import org.eclipse.dataspaceconnector.iam.ssi.config.ManagedIdentityWalletConfig;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import static java.lang.String.format;

@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("ssi")
public class ManagedIdentityWalletApiController {

    private final Monitor monitor;
    private final String logPrefix;

    private final ManagedIdentityWalletConfig config;

    public ManagedIdentityWalletApiController(Monitor monitor, String logPrefix, ManagedIdentityWalletConfig config) {
        this.monitor = monitor;
        this.logPrefix = logPrefix;
        this.config = config;
        monitor.info(format("%s :: Received a Initialize request with values: " + config.toString(), logPrefix));
    }

    @GET
    @Path("health")
    public String checkHealth() {
        monitor.info(format("%s :: Received a health request with values: " + config.toString(), logPrefix));
        return "{\"response\":\"I'm alive!\"}";
    }

    @GET
    @Path("wallets")
    public String getWallet(){
        monitor.info(format("%s :: Received a wallet request", logPrefix));

        return "";
    }
}