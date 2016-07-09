package com.wm.bfd.oo.exception;

public class BFDOOException extends Exception {

  private static final long serialVersionUID = 1L;

  public BFDOOException() {
    super();
  }

  public BFDOOException(String message) {
    super(message);
  }

  public BFDOOException(Throwable cause) {
    super(cause);
  }

  public BFDOOException(String message, Throwable cause) {
    super(message, cause);
  }

}
