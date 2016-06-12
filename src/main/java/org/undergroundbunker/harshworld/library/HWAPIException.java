package org.undergroundbunker.harshworld.library;

public class HWAPIException extends RuntimeException {

    public HWAPIException() {

    }

    public HWAPIException(String message) {
        super("[Harsh World] " + message);
    }

    public HWAPIException(String message, Throwable cause) {
        super("[Harsh World] " + message, cause);
    }

    public HWAPIException(Throwable cause) {
        super(cause);
    }

    public HWAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("[Harsh World] " + message, cause, enableSuppression, writableStackTrace);
    }
}
