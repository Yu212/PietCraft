package com.yu212.pietcraft.piet;

public class PietException extends Exception {
    public PietException() {
        super();
    }

    public PietException(String message) {
        super(message);
    }

    public PietException(String message, Throwable cause) {
        super(message, cause);
    }

    public PietException(Throwable cause) {
        super(cause);
    }
}
