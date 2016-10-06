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
