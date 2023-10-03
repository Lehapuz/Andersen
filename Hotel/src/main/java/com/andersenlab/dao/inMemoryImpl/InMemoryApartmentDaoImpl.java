package com.andersenlab.dao.inMemoryImpl;

import com.andersenlab.dao.ApartmentDao;
import com.andersenlab.entity.Apartment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryApartmentDaoImpl implements ApartmentDao {

    private final List<Apartment> apartments;

    public InMemoryApartmentDaoImpl() {
        this.apartments = new ArrayList<>();
    }

    @Override
    public Optional<Apartment> getById(long id) {
        return apartments.stream()
                .filter(apartment -> apartment.getId() == id)
                .findFirst();
    }

    @Override
    public List<Apartment> getAll() {
        return new ArrayList<>(apartments);
    }

    @Override
    public Apartment save(Apartment apartment) {
        apartments.add(apartment);
        return apartment;
    }

    @Override
    public Optional<Apartment> update(Apartment apartment) {
        Optional<Apartment> existingApartment = getById(apartment.getId());
        existingApartment.ifPresent(apt -> updateApartmentFields(apt, apartment));
        return existingApartment;
    }

    @Override
    public boolean remove(long id) {
        return apartments.removeIf(apartment -> apartment.getId() == id);
    }

    @Override
    public List<Apartment> getSortedBy(ApartmentSortType type) {
        return switch (type) {
            case ID -> sortBy(Apartment::getId);
            case PRICE -> sortBy(Apartment::getPrice);
            case CAPACITY -> sortBy(Apartment::getCapacity);
            case STATUS -> sortBy(Apartment::getStatus);
        };
    }

    private List<Apartment> sortBy(Function<Apartment, Comparable> extractor) {
        return getAll().stream()
                .sorted(Comparator.comparing(extractor))
                .toList();
    }

    private void updateApartmentFields(Apartment existingApartment, Apartment updatedApartment) {
        if (updatedApartment.getPrice() != 0.0) {
            existingApartment.setPrice(updatedApartment.getPrice());
        }
        if (updatedApartment.getCapacity() != 0) {
            existingApartment.setCapacity(updatedApartment.getCapacity());
        }
        if (updatedApartment.getStatus() != null) {
            existingApartment.setStatus(updatedApartment.getStatus());
        }
    }
}