package edu.illinois.phantom.parseengine;

public class DocParseException extends RuntimeException {
  public DocParseException(final String message) {
    super(message);
  }

  public DocParseException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
