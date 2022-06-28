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

import org.eclipse.dataspaceconnector.iam.ssi.core.SSIIdentityServiceImpl;
import org.junit.jupiter.api.Test;

public class SSIIdentityServiceExtensionTest {


  private static final String VC_SCOPE = "Membership";
  private SSIIdentityServiceImpl identityService;

  //TestServiceImplObtainClientCredentials Failed
  @Test
  void testNoConfigObtainClientCredentials(){
    identityService.obtainClientCredentials(VC_SCOPE);
  }
  //TestServiceImplObtainClientCredentials Success Happy


}
