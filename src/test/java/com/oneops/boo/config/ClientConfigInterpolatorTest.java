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
package com.oneops.boo.config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.oneops.boo.ClientConfigInterpolator;

import java.io.File;
import java.util.HashMap;

public class ClientConfigInterpolatorTest {

  private String basedir;

  @Before
  public void beforeTests() {
    basedir = System.getProperty("basedir", new File("").getAbsolutePath());
  }

  @Test
  public void validateInliningFiles() throws Exception {
    ClientConfigInterpolator interpolator = new ClientConfigInterpolator();
    File f0 = resource("f0.txt");
    assertEquals("f0", interpolator.interpolate(String.format("{{file(%s)}}", f0.getAbsolutePath()), new HashMap<String, String>()));    
  }
  
  protected File resource(String name) {
    return new File(basedir, String.format("src/test/files/%s", name));
  }
}
