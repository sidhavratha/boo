/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneops.boo;

import static org.junit.Assert.assertEquals;

import com.oneops.boo.yaml.PlatformBean;
import com.oneops.client.api.exception.OneOpsClientAPIException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPlatformBeanBuilder extends BooTest {

  @Test
  public void testGetAssembly() throws OneOpsClientAPIException {
    PlatformBean platform = new PlatformBean.PlatformBeanBuilder("oneops/hadoop-yarn-v1", "1").build();
    assertEquals(platform.getPackSource(), "oneops");
    assertEquals(platform.getPackVersion(), "1");
    assertEquals(platform.getPack(), "hadoop-yarn-v1");
  }

}
