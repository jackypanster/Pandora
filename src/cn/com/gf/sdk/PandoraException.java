package cn.com.gf.sdk;

public class PandoraException extends Exception {
    PandoraException(String message) {
        super(message);
    }

    PandoraException(String message, Throwable cause) {
        super(message, cause);
    }
}
