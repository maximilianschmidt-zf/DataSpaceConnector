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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VerifiableCredentialRegistryImpl implements VerifiableCredentialRegistry{

  private static final VerifiableCredentialRegistry instance = new VerifiableCredentialRegistryImpl();

  public static VerifiableCredentialRegistry getInstance() { return instance; }

  private final Map<String, VerifiableCredential> verifiableCredentialMap;

  public VerifiableCredentialRegistryImpl() {
    verifiableCredentialMap = new ConcurrentHashMap<>();
  }

  @Override
  public void addVerifableCredential(VerifiableCredential vc) {

  }

  @Override
  public VerifiableCredential getVerifiableCredential(String name) {
    return null;
  }
}
