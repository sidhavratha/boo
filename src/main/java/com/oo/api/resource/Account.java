package com.oo.api.resource;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.oo.api.APIClient;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;

public class Account extends APIClient {

  private static final String ACCOUNT_URI = "/organization/";

  public Account(OOInstance instance) throws OneOpsClientAPIException {
    super(instance);
  }


  /**
   * Lists all the Environment Profiles
   * 
   * @return
   * @throws OneOpsClientAPIException
   */
  public JsonPath listEnvironmentProfiles() throws OneOpsClientAPIException {
    RequestSpecification request = createRequest();
    Response response = request.get(ACCOUNT_URI + "environments");
    if (response != null) {
      if (response.getStatusCode() == 200 || response.getStatusCode() == 302) {
        return response.getBody().jsonPath();
      } else {
        String msg = String.format("Failed to get list of Environment Profiles due to %s", response.getStatusLine());
        throw new OneOpsClientAPIException(msg);
      }
    }
    String msg = String.format("Failed to get list of Environment Profiles due to null response");
    throw new OneOpsClientAPIException(msg);
  }

}
