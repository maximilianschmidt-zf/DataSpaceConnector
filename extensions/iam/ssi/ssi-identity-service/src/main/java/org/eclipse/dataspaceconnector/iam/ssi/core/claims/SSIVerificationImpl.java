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
import org.eclipse.dataspaceconnector.iam.ssi.core.did.DidDocumentDto;
import org.eclipse.dataspaceconnector.iam.ssi.core.did.SSIDidResolver;
import org.eclipse.dataspaceconnector.iam.ssi.core.did.SSIDidResolverImpl;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiableCredentialDto;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiablePresentationDto;
import org.eclipse.dataspaceconnector.ssi.spi.IdentityWalletApiService;

public class SSIVerificationImpl implements SSIVerification {

  private final SSIDidResolver didResolver;

  public SSIVerificationImpl(IdentityWalletApiService walletApiService) {
    didResolver = new SSIDidResolverImpl(walletApiService);
  }


  @Override
  public VerifiablePresentationDto verifyPresentation(VerifiablePresentationDto vp) {
    try {
      DidDocumentDto did = didResolver.resolveDid(vp.getId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  @Override
  public VerifiableCredentialDto verifyCredential(VerifiableCredentialDto vc) {
    return null;
  }
}
