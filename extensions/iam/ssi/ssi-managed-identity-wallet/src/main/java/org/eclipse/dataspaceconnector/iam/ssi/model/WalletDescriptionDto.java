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

package org.eclipse.dataspaceconnector.iam.ssi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WalletDescriptionDto {
//"name": "CatenaX-Wallet",
//"bpn": "BPNL000000000000",
//"did": "did:indy:idunion:test:8E2bifh4jJaGwdp8Eiddy1",
//"createdAt": "2022-05-25T07:11:36.675670",
//"vcs": []
    private final String name;

    private final String bpn;

    private final String did;

    private final String createdAt;

    public WalletDescriptionDto(@JsonProperty("name") String name,
                                @JsonProperty("bpn") String bpn,
                                @JsonProperty("did") String did,
                                @JsonProperty("createdAt") String createdAt) {
        this.name = name;
        this.bpn = bpn;
        this.did = did;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public String getBpn() {
        return bpn;
    }

    public String getDid() {
        return did;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
