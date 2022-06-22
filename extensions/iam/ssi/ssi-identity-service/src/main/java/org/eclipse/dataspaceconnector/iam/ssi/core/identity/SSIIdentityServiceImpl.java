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

package org.eclipse.dataspaceconnector.iam.ssi.core.identity;


import org.eclipse.dataspaceconnector.iam.ssi.core.claims.SSIClaims;
import org.eclipse.dataspaceconnector.iam.ssi.model.VerifiablePresentationDto;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.iam.TokenRepresentation;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;

public class SSIIdentityServiceImpl implements IdentityService {

  private final SSIClaims claims;

  public SSIIdentityServiceImpl() {
    claims = new SSIClaims();
  }

  @Override
  public Result<TokenRepresentation> obtainClientCredentials(String scope) {
    TokenRepresentation token;
    try {
      VerifiablePresentationDto vp = claims.GetVerifiedPresentation(scope);
      token = claims.MakeTokenFromVerifiablePresentation(vp);
      return Result.success(token);
    } catch (Exception e) {
      return Result.failure(e.getMessage());
    }
  }

  @Override
  public Result<ClaimToken> verifyJwtToken(TokenRepresentation tokenRepresentation) {
    return null;
  }

  @Override
  public Result<ClaimToken> verifyJwtToken(String token) {
    return IdentityService.super.verifyJwtToken(token);
  }
}