package me.proartex.test.vitamin.chat.client.exceptions;


public class ClientException extends RuntimeException {
    public ClientException() {
        super();
    }

    public ClientException(String message) {
        super(message);
    }
}
