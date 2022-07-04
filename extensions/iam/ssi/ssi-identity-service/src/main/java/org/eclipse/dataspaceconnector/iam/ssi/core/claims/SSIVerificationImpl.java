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

import com.danubetech.keyformats.crypto.provider.impl.NaClSodiumEd25519Provider;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.identity.jsonld.JsonLDException;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;
import io.ipfs.multibase.Base58;
import org.eclipse.dataspaceconnector.iam.ssi.core.did.DidDocumentDto;
import org.eclipse.dataspaceconnector.iam.ssi.core.did.DidVerificationMethodDto;
import org.eclipse.dataspaceconnector.iam.ssi.core.did.SSIDidResolver;
import org.eclipse.dataspaceconnector.iam.ssi.core.did.SSIDidResolverImpl;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiableCredentialDto;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiablePresentationDto;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.ssi.spi.IdentityWalletApiService;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Verification of the signature validation from a given Verifiable Presentation
 */
public class SSIVerificationImpl implements SSIVerification {

  private final SSIDidResolver didResolver;

  public SSIVerificationImpl(IdentityWalletApiService walletApiService) {
    didResolver = new SSIDidResolverImpl(walletApiService);
  }


  /**
   * Verification of a given Presentation by consuming the validation
   * Service Endpoint of the Wallet Identity Service
   * @param vp
   * @return
   */
  @Override
  public boolean verifyPresentation(VerifiablePresentationDto vp) {
    boolean result = false;
    try {
      DidDocumentDto did = didResolver.resolveDid(vp.getHolder());
      DidVerificationMethodDto didVerifyMethod = did.getVerificationMethodDtos().get(0);
      byte[] publicKey = Base58.decode(didVerifyMethod.getPublicKeyBase58());
      result = verifyEd25519Signature(publicKey, vp);
    } catch (Exception e) {
      throw new EdcException(e.getMessage());
    }
    return result;
  }

  private boolean verifyEd25519Signature(byte[] publicKey, VerifiablePresentationDto vp) throws JsonLDException, GeneralSecurityException, IOException {
    //TODO Wait for validation Service and consume endpoint
/*    ObjectMapper mapper = new ObjectMapper();
    String jsonVP = mapper.writeValueAsString(vp);
    VerifiablePresentation danubPresentation = VerifiablePresentation.fromJson(jsonVP);
    Ed25519Signature2018LdVerifier verifier = new Ed25519Signature2018LdVerifier(publicKey);
    Boolean verified = verifier.verify(danubPresentation);*/
    return true;
  }

  @Override
  public VerifiableCredentialDto verifyCredential(VerifiableCredentialDto vc) {
    return null;
  }
}
