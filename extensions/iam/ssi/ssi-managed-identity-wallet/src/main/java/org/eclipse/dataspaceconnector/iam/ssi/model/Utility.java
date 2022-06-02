 /*
  *  Copyright (c) 2022 ZF Friedrichshafen AG
  *
  *  This program and the accompanying materials are made available under the
  *  terms of the Apache License, Version 2.0 which is available at
  *  https://www.apache.org/licenses/LICENSE-2.0
  *
  *  SPDX-License-Identifier: Apache-2.0
  *
  *  Contributors:
  *       ZF Friedrichshafen AG - Initial API and Implementation
  *
  */

package org.eclipse.dataspaceconnector.iam.ssi.model;

import okhttp3.MediaType;

public class Utility {
    public static final MediaType JSON = MediaType.get("application/json");
    public static final MediaType WWW = MediaType.get("application/x-www-form-urlencoded");

    private Utility() {}
}
