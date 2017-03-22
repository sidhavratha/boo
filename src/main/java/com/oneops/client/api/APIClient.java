/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.oneops.client.api;

import org.apache.commons.codec.binary.Base64;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.DecoderConfig;
import com.jayway.restassured.specification.RequestSpecification;
import com.oneops.client.api.exception.OneOpsClientAPIException;

public abstract class APIClient {

  private OOInstance instance;

  public APIClient(OOInstance instance) throws OneOpsClientAPIException {
    this.instance = instance;
    if (instance == null) {
      throw new OneOpsClientAPIException(
          "Missing OneOps instance information to perform API invocation");
    }
    if (instance.getAuthtoken() == null) {
      throw new OneOpsClientAPIException(
          "Missing OneOps authentication API key to perform API invocation");
    }
    if (instance.getEndpoint() == null) {
      throw new OneOpsClientAPIException("Missing OneOps endpoint to perform API invocation");
    }
  }

  protected RequestSpecification createRequest() {
    RequestSpecification rs = RestAssured.given();
    if (!instance.isGzipEnabled()) {
      rs.config(
          RestAssured.config().decoderConfig(DecoderConfig.decoderConfig().noContentDecoders()));
    }
    String basicAuth =
        "Basic " + new String(Base64.encodeBase64(instance.getAuthtoken().getBytes()));
    rs.header("Authorization", basicAuth);
    rs.header("User-Agent", "OneOpsAPIClient");
    rs.header("Accept", "application/json");
    rs.header("Content-Type", "application/json");
    String baseUri = instance.getEndpoint();
    if (instance.getOrgname() != null) {
      baseUri += instance.getOrgname();
    }
    rs.baseUri(baseUri);

    return rs;
  }


}
