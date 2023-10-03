package com.andersenlab.service.impl;

import com.andersenlab.dao.ApartmentDao;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.ApartmentStatus;
import com.andersenlab.exceptions.ConfigurationRestrictionException;
import com.andersenlab.exceptions.IdDoesNotExistException;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.service.ApartmentService;
import com.andersenlab.util.EntityValidityCheck;
import com.andersenlab.util.IdGenerator;

import java.util.List;

public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentDao apartmentDao;
    private final HotelFactory hotelFactory;

    public ApartmentServiceImpl(ApartmentDao apartmentDao, HotelFactory hotelFactory) {
        this.apartmentDao = apartmentDao;
        this.hotelFactory = hotelFactory;
    }

    @Override
    public Apartment getById(long id) {
        return apartmentDao.getById(id)
                .orElseThrow(() -> new IdDoesNotExistException("Apartment with this id doesn't exist. Id: " + id));
    }

    @Override
    public List<Apartment> getAll() {
        return apartmentDao.getAll();
    }

    @Override
    public Apartment save(int capacity, double price) {
        EntityValidityCheck.apartmentCapacityCheck(capacity);
        EntityValidityCheck.apartmentPriceCheck(price);
        Apartment apartment = new Apartment(IdGenerator.generateApartmentId(),
                capacity, price, ApartmentStatus.AVAILABLE);
        return apartmentDao.save(apartment);
    }

    @Override
    public Apartment update(Apartment apartment) {
        return apartmentDao.update(apartment)
                .orElseThrow(() -> new IdDoesNotExistException("Apartment with this id doesn't exist. Id: "
                        + apartment.getId()));
    }

    @Override
    public Apartment changePrice(long id, double price) {
        EntityValidityCheck.apartmentPriceCheck(price);
        return update(new Apartment(id, price));
    }

    @Override
    public Apartment changeStatus(long id) {
        boolean allowStatusChange = hotelFactory.getConfig().getConfigData().getApartment().isAllowApartmentStatusChange();
        if (!allowStatusChange) {
            throw new ConfigurationRestrictionException("Configuration does not allow change of status");
        }
        ApartmentStatus newStatus = getById(id).getStatus() == ApartmentStatus.AVAILABLE ?
                ApartmentStatus.UNAVAILABLE : ApartmentStatus.AVAILABLE;
        return update(new Apartment(id, newStatus));
    }

    @Override
    public List<Apartment> getSorted(String type) {
        return apartmentDao.getSortedBy(
                switch (type.toLowerCase()) {
                    case "price" -> ApartmentDao.ApartmentSortType.PRICE;
                    case "capacity" -> ApartmentDao.ApartmentSortType.CAPACITY;
                    case "status" -> ApartmentDao.ApartmentSortType.STATUS;
                    default -> ApartmentDao.ApartmentSortType.ID;
                });
    }
}