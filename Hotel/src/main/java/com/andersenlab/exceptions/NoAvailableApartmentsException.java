package com.andersenlab.exceptions;

public class NoAvailableApartmentsException extends HotelException{
    public NoAvailableApartmentsException(String message) {
        super(message);
    }
}
