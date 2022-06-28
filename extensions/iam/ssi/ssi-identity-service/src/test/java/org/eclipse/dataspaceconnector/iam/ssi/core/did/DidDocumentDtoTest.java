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

package org.eclipse.dataspaceconnector.iam.ssi.core.did;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class DidDocumentDtoTest {

  @Test
  public void didDocumentVerifyMapping() throws IOException {
    //given
    String jsonDidDocumentFilePath = "diddocument.json";
    String jsonDidDocumentString = "";
    DidDocumentDto didDoc = null;
    //when
    var stream = getClass().getClassLoader().getResourceAsStream(jsonDidDocumentFilePath);
    jsonDidDocumentString = new String(stream.readAllBytes());
    didDoc = new ObjectMapper().readValue(jsonDidDocumentString, DidDocumentDto.class);
    //then
    assertNotNull(didDoc);
  }

}
