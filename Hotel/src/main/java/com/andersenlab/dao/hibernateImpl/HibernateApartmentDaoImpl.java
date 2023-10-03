package com.andersenlab.dao.hibernateImpl;

import com.andersenlab.dao.ApartmentDao;
import com.andersenlab.entity.Apartment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class HibernateApartmentDaoImpl implements ApartmentDao {
    private final EntityManagerFactory entityManagerFactory;

    public HibernateApartmentDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Apartment> getById(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Apartment apartment = entityManager.find(Apartment.class, id);
            entityManager.getTransaction().commit();
            return Optional.ofNullable(apartment);
        }
    }

    @Override
    public List<Apartment> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            List<Apartment> apartments = entityManager.createQuery("FROM Apartment ORDER BY id").getResultList();
            entityManager.getTransaction().commit();
            return apartments;
        }
    }

    @Override
    public Apartment save(Apartment apartment) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            apartment.setId(0);
            entityManager.persist(apartment);
            entityManager.getTransaction().commit();
            return apartment;
        }
    }

    @Override
    public Optional<Apartment> update(Apartment apartment) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Optional<Apartment> existingApartment = Optional.ofNullable(entityManager.find(Apartment.class, apartment.getId()));
            existingApartment.ifPresent(apt -> updateApartmentFields(apt, apartment));
            entityManager.getTransaction().commit();
            return existingApartment;
        }
    }

    @Override
    public boolean remove(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Optional<Apartment> existingApartment = Optional.ofNullable(entityManager.find(Apartment.class, id));
            existingApartment.ifPresent(apartment -> deleteApartment(apartment, entityManager));
            return existingApartment.isPresent();
        }
    }

    @Override
    public List<Apartment> getSortedBy(ApartmentSortType type) {
        return switch (type) {
            case ID -> getAll();
            case PRICE -> sortBy("price");
            case CAPACITY -> sortBy("capacity");
            case STATUS -> sortBy("status");
        };
    }

    private List<Apartment> sortBy(String parameter) {
        String getAllQuery = "FROM Apartment ORDER BY " + parameter;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            List<Apartment> apartments = entityManager.createQuery(getAllQuery).getResultList();
            entityManager.getTransaction().commit();
            return apartments;
        }
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

    private void deleteApartment(Apartment apartment, EntityManager entityManager) {
        entityManager.remove(apartment);
        entityManager.getTransaction().commit();
    }
}
