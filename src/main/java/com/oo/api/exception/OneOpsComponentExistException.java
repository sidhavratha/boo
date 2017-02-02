package com.oo.api.exception;

public class OneOpsComponentExistException extends Exception {

  private static final long serialVersionUID = 1L;

  public OneOpsComponentExistException() {
    super();
  }

  public OneOpsComponentExistException(String message) {
    super(message);
  }

  public OneOpsComponentExistException(Throwable cause) {
    super(cause);
  }

  public OneOpsComponentExistException(String message, Throwable cause) {
    super(message, cause);
  }

}
