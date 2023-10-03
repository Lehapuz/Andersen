package com.andersenlab.exceptions;

public class ClientAlreadyCheckedInException extends HotelException{
    public ClientAlreadyCheckedInException(String message) {
        super(message);
    }
}
