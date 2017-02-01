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
