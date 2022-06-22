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

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;

public class SSIVerificationImpl implements SSIVerification{
  @Override
  public VerifiablePresentation verifyPresentation(VerifiablePresentation vp) {
    return null;
  }

  @Override
  public VerifiableCredential verifiyCredential(VerifiableCredential vc) {
    return null;
  }
}
