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
package com.oneops.boo;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneops.api.exception.OneOpsClientAPIException;
import com.oneops.boo.yaml.PlatformBean;
import com.oneops.boo.yaml.helper.PlatformBeanHelper;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPlatformBeanHelper extends BooTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestPlatformBeanHelper.class);

  @Test
  public void testGetPlatforms() throws OneOpsClientAPIException {
    List<PlatformBean> list = PlatformBeanHelper.getPlatforms(this.config.getYaml().getPlatforms());
    LOG.debug("Total {} platforms.", list.size());
  }

}
