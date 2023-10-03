package com.andersenlab.exceptions;

public class IdDoesNotExistException extends HotelException{
    public IdDoesNotExistException(String message) {
        super(message);
    }
}
