package com.andersenlab.util;

import com.andersenlab.exceptions.InappropriateValueException;

public class EntityValidityCheck {
    public static void perkPriceCheck(double price) {
        if (price < 0.0) {
            throw new InappropriateValueException("Perk price cannot be less than 0.0");
        }
    }
    public static void apartmentPriceCheck(double price) {
        if (price < 0.0) {
            throw new InappropriateValueException("Apartment price cannot be less than 0.0");
        }
    }
    public static void apartmentCapacityCheck(int capacity) {
        if (capacity < 1) {
            throw new InappropriateValueException("Apartment capacity must be greater than 0");
        }
    }
    public static void clientQuantityOfPeopleCheck(int quantityOfPeople) {
        if (quantityOfPeople < 1) {
            throw new InappropriateValueException("Client must include at least 1 person");
        }
    }
    public static void clientStayDurationCheck(int stayDuration) {
        if (stayDuration < 1) {
            throw new InappropriateValueException("Client must stay for at least 1 day");
        }
    }
}
