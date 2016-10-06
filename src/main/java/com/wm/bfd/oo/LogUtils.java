package com.wm.bfd.oo;


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
