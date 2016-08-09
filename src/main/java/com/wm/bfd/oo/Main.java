package com.wm.bfd.oo;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.oo.api.exception.OneOpsClientAPIException;
import com.wm.bfd.oo.exception.BFDOOException;

public class Main {

  static {
    Logger.getRootLogger().setLevel(Level.OFF);
  }

  public static void main(String[] args) throws BFDOOException, OneOpsClientAPIException,
      ParseException {
    BooCli cli = new BooCli(args);
    cli.parse();
  }
}
