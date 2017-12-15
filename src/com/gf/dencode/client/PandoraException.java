package com.gf.dencode.client;

public class PandoraException extends Exception {
    PandoraException(String message) {
        super(message);
    }

    PandoraException(String message, Throwable cause) {
        super(message, cause);
    }

    PandoraException(Throwable cause) {
        super(cause);
    }
}
