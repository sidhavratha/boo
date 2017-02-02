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
   * Called whenever the progress bar needs to be updated. that is whenever progress was made.
   * 
   * @param done an integer representing the work done so far
   * @param total an integer representing the total work
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
