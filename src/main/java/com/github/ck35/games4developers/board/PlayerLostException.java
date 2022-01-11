package com.github.ck35.games4developers.board;

public class PlayerLostException extends Exception {

    public PlayerLostException(String message) {
        this(message, null);
    }
    public PlayerLostException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
