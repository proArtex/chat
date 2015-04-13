package me.proartex.test.vitamin.chat.exceptions;


public class ServerException extends RuntimeException {
    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
    }
}
