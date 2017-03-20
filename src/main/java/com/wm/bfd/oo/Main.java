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
package com.wm.bfd.oo;

import com.wm.bfd.oo.exception.BfdOoException;
import com.wm.bfd.oo.yaml.Constants;

import com.oo.api.exception.OneOpsClientAPIException;

import org.apache.commons.cli.ParseException;

public class Main {

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    BooCli cli = new BooCli();
    int exit = 0;
    try {
      exit = cli.parse(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      exit = Constants.EXIT_PARSE_ERROR;
    } catch (BfdOoException e) {
      System.err.println(e.getMessage());
      exit = Constants.EXIT_BOO;
    } catch (OneOpsClientAPIException e) {
      System.err.println(e.getMessage());
      exit = Constants.EXIT_CLIENT;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      exit = Constants.EXIT_UNKOWN;
    } finally {
      System.exit(exit);
    }

  }
}
