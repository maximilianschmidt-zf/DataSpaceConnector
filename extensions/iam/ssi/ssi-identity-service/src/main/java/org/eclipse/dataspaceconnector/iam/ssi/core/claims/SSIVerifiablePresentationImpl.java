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

package org.eclipse.dataspaceconnector.iam.ssi.core.claims;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiableCredentialDto;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiablePresentationDto;
import org.eclipse.dataspaceconnector.ssi.spi.IdentityWalletApiService;

public class SSIVerifiablePresentationImpl implements SSIVerifiablePresentation {

  IdentityWalletApiService walletApiService;

  public SSIVerifiablePresentationImpl(IdentityWalletApiService walletApiService) {
    this.walletApiService = walletApiService;
  }

  @Override
  public VerifiablePresentationDto getPresentation(VerifiableCredentialDto vc) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String vcJsonString = mapper.writeValueAsString(vc);

    String vpAsString = walletApiService.issueVerifiablePresentation(vcJsonString);

    VerifiablePresentationDto vp = mapper.readValue(vpAsString, VerifiablePresentationDto.class);

    return vp;
  }
}
