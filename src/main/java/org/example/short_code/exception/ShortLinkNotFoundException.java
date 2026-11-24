package org.example.short_code.exception;

public class ShortLinkNotFoundException extends RuntimeException{
    public ShortLinkNotFoundException() {
        super();
    }

    public ShortLinkNotFoundException(String message) {
        super(message);
    }
    public ShortLinkNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ShortLinkNotFoundException(Throwable cause) {
        super(cause);
    }
}
