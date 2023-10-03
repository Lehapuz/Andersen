package com.andersenlab.dao;

import com.andersenlab.dao.inMemoryImpl.InMemoryApartmentDaoImpl;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.ApartmentStatus;
import com.andersenlab.util.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;


public class ApartmentDaoTest {
    private Apartment apartment;
    private InMemoryApartmentDaoImpl apartmentDao;

    @BeforeEach
    private void setup() {
        apartmentDao = new InMemoryApartmentDaoImpl();

        apartment = new Apartment();
        apartment.setId(0);
        apartment.setCapacity(1);
        apartment.setPrice(150);
        apartment.setStatus(ApartmentStatus.AVAILABLE);

        Apartment apartment1 = new Apartment();
        apartment1.setId(1);
        apartment1.setCapacity(2);
        apartment1.setPrice(250);
        apartment1.setStatus(ApartmentStatus.AVAILABLE);

        apartmentDao.save(apartment);
        apartmentDao.save(apartment1);

    }

    @Test
    void inMemoryList_getApartmentById_EqualsOfApartmentIdInSetupAndInMemoryTest() {
        Assertions.assertEquals(apartment.getId(), Objects.requireNonNull(apartmentDao.getById(apartment.getId()).orElse(null)).getId());
    }

    @Test
    void inMemoryList_getApartmentByNonExistingId_ReturnedIsEmptyTest() {
        Assertions.assertTrue(apartmentDao.getById(Long.MAX_VALUE).isEmpty());
    }

    @Test
    void inMemoryList_saveApartmentToMemory_EqualsOfApartmentInSetupAndReturnedTest() {
        apartment = new Apartment();
        apartment.setId(IdGenerator.generateApartmentId());
        apartment.setCapacity(1);
        apartment.setPrice(200);
        apartment.setStatus(ApartmentStatus.AVAILABLE);

        Assertions.assertEquals(apartment, apartmentDao.save(apartment));
        Assertions.assertEquals(apartment.getId(), Objects.requireNonNull(apartmentDao.getById(apartment.getId()).orElse(null)).getId());
    }

    @Test
    void inMemoryList_getAllApartments_NotNullAndCountOfReturnedEqualsTwoTest() {
        Assertions.assertNotNull(apartmentDao.getAll());
        Assertions.assertEquals(2, apartmentDao.getAll().size());
    }

    @Test
    void inMemoryList_UpdateApartment_ReturnedNotNullAndNotUpdatedFieldIfSetIsNullAndNotUpdatedByNullApartmentTest() {
        apartment = new Apartment();
        apartment.setId(0);
        apartment.setCapacity(5);
        apartment.setPrice(1000);
        apartment.setStatus(ApartmentStatus.UNAVAILABLE);



        Assertions.assertAll("apartmentUpdate",
                () -> Assertions.assertNotNull(apartmentDao.update(apartment)),
                () -> Assertions.assertEquals(apartment,apartmentDao.update(apartment).orElse(null)),
                () -> {
                    apartment.setPrice(0.0);
                    Assertions.assertEquals(1000, Objects.requireNonNull(apartmentDao.update(apartment).orElse(null)).getPrice());
                },
                () -> {
                    apartment.setPrice(1000);
                    Apartment apartment2 = new Apartment();
                    Assertions.assertEquals(apartment, apartmentDao.update(apartment2).orElse(null));
                }
        );
    }

    @Test
    void inMemoryList_RemoveApartmentByIdAndGetApartmentByDeletedID_ReturnedEmptyTest() {
        Assertions.assertTrue(apartmentDao.remove(0));
        Assertions.assertTrue(apartmentDao.getById(0).isEmpty());
    }
}
