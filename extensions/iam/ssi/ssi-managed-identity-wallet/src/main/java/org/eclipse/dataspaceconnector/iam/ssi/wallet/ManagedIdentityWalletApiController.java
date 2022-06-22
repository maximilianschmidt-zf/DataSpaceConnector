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
package org.eclipse.dataspaceconnector.iam.ssi.wallet;


import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.eclipse.dataspaceconnector.iam.ssi.model.AccessTokenDescriptionDto;
import org.eclipse.dataspaceconnector.iam.ssi.model.AccessTokenRequestDto;
import org.eclipse.dataspaceconnector.iam.ssi.model.WalletDescriptionDto;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import jakarta.ws.rs.InternalServerErrorException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.lang.String.format;

public class ManagedIdentityWalletApiController {

    private final Monitor monitor;
    private final String logPrefix;
    private final ManagedIdentityWalletConfig config;
    private final OkHttpClient httpClient;

    private final TypeManager typeManager;

    private final ObjectMapper objectMapper;

    private final AccessTokenRequestDto accessTokenRequestDto;

    public ManagedIdentityWalletApiController(Monitor monitor,
                                              String logPrefix,
                                              ManagedIdentityWalletConfig config,
                                              OkHttpClient httpClient,
                                              TypeManager typeManager) {
        this.monitor = monitor;
        this.logPrefix = logPrefix;
        this.config = config;
        this.httpClient = httpClient;
        this.typeManager = typeManager;
        this.objectMapper = typeManager.getMapper();
        AccessTokenRequestDto.Builder builder = AccessTokenRequestDto.Builder.newInstance();
        this.accessTokenRequestDto = builder.clientID(config.getKeycloakClientID())
                .clientSecret(config.getKeycloakClientSecret())
                .grandType(config.getKeycloakGrandType())
                .scope(config.getKeycloakScope())
                .build();
        monitor.info(format("%s :: Received a Initialize request with values: " + config.toString(), logPrefix));
    }

    public String fetchWalletDescription(){
        monitor.info(format("%s :: Received a wallet request", logPrefix));
        var url = config.getWalletURL() + "/api/wallets/" + config.getWalletDID();
        AccessTokenDescriptionDto accessToken = null;
        WalletDescriptionDto walletDescriptionDto = null;
        try{
            accessToken = getKeyCloakToken(this.accessTokenRequestDto);
            monitor.severe(format("Fetched AccessToken %s", accessToken.toString()));
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + accessToken.getAccessToken())
                    .build();
            try(var response = httpClient.newCall(request).execute()){
                var body = response.body();
                if (!response.isSuccessful() || body == null) {
                    throw new InternalServerErrorException(format("Keycloak responded with: %s %s", response.code(), body != null ? body.string() : ""));
                }
                walletDescriptionDto = objectMapper.readValue(body.string(), WalletDescriptionDto.class);
                monitor.info("Fetched Wallets: " + walletDescriptionDto);
            } catch (Exception e) {
                monitor.severe(format("Error by fetching wallets at %s", url), e);
            }
        } catch (Exception e){
            monitor.severe(format("Error in fetching AccessToken"), e);
        }
        return walletDescriptionDto != null ? walletDescriptionDto.getName() : "nothing found";
    }

    public AccessTokenDescriptionDto getKeyCloakToken(AccessTokenRequestDto accessTokenRequest) throws IOException {
        var url = config.getKeycloakURL();
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", accessTokenRequest.getGrantType())
                .add("client_id", accessTokenRequest.getCliendId())
                .add("client_secret", accessTokenRequest.getClient_secret())
                .add("scope", accessTokenRequest.getScope()).build();
        var request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody);

        try(var response = httpClient.newCall(request.build()).execute()){
            var body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new InternalServerErrorException(format("Keycloak responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }
            var accessTokenDescription = objectMapper.readValue(body.string(), AccessTokenDescriptionDto.class);
            monitor.info("Get new token with ID: " + accessTokenDescription.getTokenID());
            return accessTokenDescription;
        } catch (Exception e) {
            monitor.severe(format("Error in calling the keycloak server at %s", url), e);
            throw e;
        }
    }
}