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
package com.oneops.client.api.resource;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.oneops.client.api.APIClient;
import com.oneops.client.api.OOInstance;
import com.oneops.client.api.exception.OneOpsClientAPIException;

public class Cloud extends APIClient {

  private static final String CLOUD_URI = "/clouds/";

  public Cloud(OOInstance instance) throws OneOpsClientAPIException {
    super(instance);
  }

  /**
   * Fetches specific cloud details
   * 
   * @param cloudName
   * @return
   * @throws OneOpsClientAPIException
   */
  public JsonPath getCloud(String cloudName) throws OneOpsClientAPIException {
    if (cloudName == null || cloudName.length() == 0) {
      String msg = String.format("Missing cloud name to fetch details");
      throw new OneOpsClientAPIException(msg);
    }

    RequestSpecification request = createRequest();
    Response response = request.get(CLOUD_URI + cloudName);
    if (response != null) {
      if (response.getStatusCode() == 200 || response.getStatusCode() == 302) {
        return response.getBody().jsonPath();
      } else {
        String msg = String.format("Failed to get cloud with name %s due to %s", cloudName,
            response.getStatusLine());
        throw new OneOpsClientAPIException(msg);
      }
    }
    String msg = String.format("Failed to get cloud with name %s due to null response", cloudName);
    throw new OneOpsClientAPIException(msg);
  }

  /**
   * Lists all the clouds
   * 
   * @return
   * @throws OneOpsClientAPIException
   */
  public JsonPath listClouds() throws OneOpsClientAPIException {
    RequestSpecification request = createRequest();
    Response response = request.get(CLOUD_URI);
    if (response != null) {
      if (response.getStatusCode() == 200 || response.getStatusCode() == 302) {
        return response.getBody().jsonPath();
      } else {
        String msg =
            String.format("Failed to get list of clouds due to %s", response.getStatusLine());
        throw new OneOpsClientAPIException(msg);
      }
    }
    String msg = String.format("Failed to get list of clouds due to null response");
    throw new OneOpsClientAPIException(msg);
  }
}
