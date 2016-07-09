package com.wm.bfd.oo.utils;

import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.exception.BFDOOException;
import com.wm.bfd.oo.yaml.Constants;

public class BFDUtils {

  public boolean verifyTemplate(ClientConfig config) throws BFDOOException {
    if (config == null || config.getYaml() == null || config.getYaml().getAssembly() == null
        || config.getYaml().getPlatforms() == null) {
      throw new BFDOOException(Constants.YAML_ERROR);
    }
    return false;
  }
}
