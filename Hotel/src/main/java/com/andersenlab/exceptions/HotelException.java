package com.andersenlab.exceptions;

public class HotelException extends RuntimeException{
    public HotelException() {
    }

    public HotelException(String message) {
        super(message);
    }

    public HotelException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotelException(Throwable cause) {
        super(cause);
    }

    public HotelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
