package com.oo.api.resource;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.oo.api.APIClient;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;

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
        String msg = String.format("Failed to get cloud with name %s due to %s", cloudName, response.getStatusLine());
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
        String msg = String.format("Failed to get list of clouds due to %s", response.getStatusLine());
        throw new OneOpsClientAPIException(msg);
      }
    }
    String msg = String.format("Failed to get list of clouds due to null response");
    throw new OneOpsClientAPIException(msg);
  }
}
