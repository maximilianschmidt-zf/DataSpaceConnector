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


import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiableCredentialDto;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiableCredentialRegistry;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiableCredentialRegistryImpl;
import org.eclipse.dataspaceconnector.iam.ssi.wallet.ManagedIdentityWalletApiServiceImpl;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.ssi.spi.IdentityWalletApiService;

public class SSIVerifiableCredentialsImpl implements SSIVerifiableCredentials{

  private IdentityWalletApiService walletApiService;
  private VerifiableCredentialRegistry verifiableCredentialRegistry;

  public SSIVerifiableCredentialsImpl(IdentityWalletApiService walletApiService, VerifiableCredentialRegistry verifiableCredentialRegistry) {
    this.walletApiService = walletApiService;
    this.verifiableCredentialRegistry = verifiableCredentialRegistry;
  }

  @Override
  public VerifiableCredentialDto findByScope(String scope) {
    // Update VC Registry
    walletApiService.fetchWalletDescription();

    try{
      VerifiableCredentialDto vc =  verifiableCredentialRegistry.getVerifiableCredential(scope);
      return vc;
    } catch (Exception e){
      throw new EdcException(e.getMessage());
    }
  }
}
