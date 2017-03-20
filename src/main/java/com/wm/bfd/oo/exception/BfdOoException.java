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
package com.wm.bfd.oo.exception;

public class BfdOoException extends Exception {

  private static final long serialVersionUID = 1L;

  public BfdOoException() {
    super();
  }

  public BfdOoException(String message) {
    super(message);
  }

  public BfdOoException(Throwable cause) {
    super(cause);
  }

  public BfdOoException(String message, Throwable cause) {
    super(message, cause);
  }

}
