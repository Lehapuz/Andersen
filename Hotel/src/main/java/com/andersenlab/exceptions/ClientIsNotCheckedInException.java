package com.andersenlab.exceptions;

public class ClientIsNotCheckedInException extends HotelException{
    public ClientIsNotCheckedInException(String message) {
        super(message);
    }
}
