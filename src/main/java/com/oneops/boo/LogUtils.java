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


/**
 * Control the logs.
 */
public class LogUtils {

  /**
   * Info.
   *
   * @param msg the msg
   * @param arg the arg
   */
  public static void info(String msg, Object... arg) {
    if (!BooCli.isQuiet()) {
      System.out.printf(msg, arg);
      System.out.println();
    }
  }

  /**
   * Error.
   *
   * @param msg the msg
   * @param arg the arg
   */
  public static void error(String msg, Object... arg) {
    if (!BooCli.isQuiet()) {
      System.err.printf(msg, arg);
      System.err.println();
    }
  }

}
