
package org.eclipse.dataspaceconnector.iam.ssi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@JsonDeserialize(builder = ManagedIdentityWalletConfig.Builder.class)
public class ManagedIdentityWalletConfig {

    @NotNull
    private final String walletURL;
    @NotNull
    private final String walletJwksURL;
    @NotNull
    private final String walletIssuerURL;
    @NotNull
    private final String keycloakURL;
    @NotNull
    private final String keycloakClientID;
    @NotNull
    private final String keycloakClientSecret;
    @NotNull
    private final String keycloakGrandType;
    @NotNull
    private final String keycloakScope;
    @NotNull
    private final String accessTokenURL;
    @NotNull
    private final String logprefig;

    @Override
    public String toString() {
        return "ManagedIdentityWalletConfig{" +
                "walletURL='" + walletURL + '\'' +
                ", walletJwksURL='" + walletJwksURL + '\'' +
                ", walletIssuerURL='" + walletIssuerURL + '\'' +
                ", keycloakURL='" + keycloakURL + '\'' +
                ", keycloakClientID='" + keycloakClientID + '\'' +
                ", keycloakClientSecret='" + keycloakClientSecret + '\'' +
                ", keycloakGrandType='" + keycloakGrandType + '\'' +
                ", keycloakScope='" + keycloakScope + '\'' +
                ", accessTokenURL='" + accessTokenURL + '\'' +
                ", logprefig='" + logprefig + '\'' +
                '}';
    }

    private ManagedIdentityWalletConfig(Builder builder){
        this.walletURL = builder.walletURL;
        this.keycloakURL = builder.keycloakURL;
        this.keycloakClientID = builder.keycloakClientID;
        this.keycloakClientSecret = builder.keycloakClientSecret;
        this.keycloakGrandType = builder.keycloakGrandType;
        this.keycloakScope = builder.keycloakScope;
        this.accessTokenURL = builder.accessTokenURL;
        this.logprefig = builder.logprefig;
        this.walletJwksURL = builder.walletJwksURL;
        this.walletIssuerURL = builder.walletIssuerURL;
    }

    public String getWalletURL() {
        return walletURL;
    }

    public String getWalletJwksURL() {
        return walletJwksURL;
    }

    public String getWalletIssuerURL() {
        return walletIssuerURL;
    }

    public String getKeycloakURL() {
        return keycloakURL;
    }

    public String getKeycloakClientID() {
        return keycloakClientID;
    }

    public String getKeycloakClientSecret() {
        return keycloakClientSecret;
    }

    public String getKeycloakGrandType() {
        return keycloakGrandType;
    }

    public String getKeycloakScope() {
        return keycloakScope;
    }

    public String getAccessTokenURL() {
        return accessTokenURL;
    }

    public String getLogprefig() {
        return logprefig;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder{
        @NotNull
        private String walletURL;
        @NotNull
        private String keycloakURL;
        @NotNull
        private String keycloakClientID;
        @NotNull
        private String keycloakClientSecret;
        @NotNull
        private String keycloakGrandType;
        @NotNull
        private String keycloakScope;
        @NotNull
        private String accessTokenURL;
        @NotNull
        private String logprefig;
        @NotNull
        private String walletJwksURL;
        @NotNull
        private String walletIssuerURL;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder(){
        }

        public Builder accessTokenURL(String accessTokenURL){
            this.accessTokenURL = accessTokenURL;
            return this;
        }

        public Builder keycloakURL(String keycloakURL){
            this.keycloakURL = keycloakURL;
            return this;
        }

        public Builder walletURL(String walletURL){
            this.walletURL = walletURL;
            return this;
        }

        public Builder walletJwksURL(String walletJwksURL){
            this.walletJwksURL = walletJwksURL;
            return this;
        }

        public Builder walletIssuerURL(String walletIssuerURL){
            this.walletIssuerURL = walletIssuerURL;
            return this;
        }

        public Builder keycloakClientID(String keycloakClientID){
            this.keycloakClientID = keycloakClientID;
            return this;
        }

        public Builder keycloakClientSecret(String keycloakClientSecret){
            this.keycloakClientSecret = keycloakClientSecret;
            return this;
        }

        public Builder keycloakGrandType(String keycloakGrandType){
            this.keycloakGrandType = keycloakGrandType;
            return this;
        }

        public Builder keycloakScope(String keycloakScope){
            this.keycloakScope = keycloakScope;
            return this;
        }

        public ManagedIdentityWalletConfig build(){
            ManagedIdentityWalletConfig config = new ManagedIdentityWalletConfig(this);
            return config;
        }
    }


}