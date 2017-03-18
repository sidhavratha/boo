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
package com.wm.bfd.oo.utils;

import com.wm.bfd.oo.LogUtils;

public class ProgressBar {
  private StringBuilder progress;

  /**
   * Initialize progress bar properties.
   */
  public ProgressBar() {
    init();
  }

  /**
   * Called whenever the progress bar needs to be updated.
   * 
   * @param done The work done so far.
   * @param total The total work.
   */
  public void update(int done, int total) {
    String format = "%3d%% %s \n";
    int percent = (done++ * 100) / total;
    int extrachars = (percent / 2) - this.progress.length();

    while (extrachars-- > 0) {
      progress.append('*');
    }

    LogUtils.info(format, percent, progress);

    if (done == total) {
      System.out.flush();
      System.out.println();
      init();
    }
  }

  private void init() {
    this.progress = new StringBuilder(60);
  }
}
