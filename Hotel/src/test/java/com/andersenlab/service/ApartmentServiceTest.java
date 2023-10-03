package com.andersenlab.service;

import com.andersenlab.config.Config;
import com.andersenlab.config.SaveOption;
import com.andersenlab.dao.onDiskImpl.OnDiskApartmentDaoImpl;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.ApartmentStatus;
import com.andersenlab.exceptions.ConfigurationRestrictionException;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.util.ConfigHandler;
import com.andersenlab.util.IdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApartmentServiceTest {

    private ApartmentService apartmentService;
    private HotelFactory hotelFactory;


    @BeforeEach
    void setup() {
        IdGenerator.cancelGenerateId();
        Config config = new Config();
        config.setConfigData(ConfigHandler.createConfig("src/test/resources/config/config-test.yaml"));
        hotelFactory = new HotelFactory(config);
        apartmentService = hotelFactory.getApartmentService();
        apartmentService.save(1, 100.0);
        apartmentService.save(2, 350.0);
        apartmentService.save(4, 500.0);
        apartmentService.save(3, 200.0);
    }

    @AfterEach
    void teardown() {
        if (this.hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.DISK) {
            OnDiskApartmentDaoImpl onDiskApartmentDao = new OnDiskApartmentDaoImpl(hotelFactory);
            apartmentService.getAll().forEach(apartment -> onDiskApartmentDao.remove(apartment.getId()));
        }
    }

    @Test
    void whenSearchingForApartmentById_thenItShouldBeFound() {
        int id = 2;
        int expectedCapacity = 2;
        double expectedPrice = 350.0;

        Apartment apartment = apartmentService.getById(id);
        int actualCapacity = apartment.getCapacity();
        double actualPrice = apartment.getPrice();

        assertEquals(expectedCapacity, actualCapacity);
        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    void whenGetAll_thenStorageSizeShouldEqualTo4() {
        int expectedSize = 4;

        int actualSize = apartmentService.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void whenSavingNewApartment_thenStorageSizeShouldIncreaseByOne() {
        int expectedSize = apartmentService.getAll().size() + 1;

        apartmentService.save(10, 675.5);

        int actualSize = apartmentService.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void whenUpdatingExistingApartment_thenItShouldReturnUpdatedApartment() {
        int id = 1;
        Apartment apartment = new Apartment(1, 7, 550, ApartmentStatus.AVAILABLE);

        int capacityBeforeUpdate = apartmentService.getById(id).getCapacity();
        double priceBeforeUpdate = apartmentService.getById(id).getPrice();
        apartmentService.update(apartment);
        int capacityAfterUpdate = apartmentService.getById(id).getCapacity();
        double priceAfterUpdate = apartmentService.getById(id).getPrice();

        assertNotEquals(capacityBeforeUpdate, capacityAfterUpdate);
        assertFalse(priceBeforeUpdate == priceAfterUpdate);
    }

    @Test
    void whenChangingPrice_thenItShouldReturnNewPrice() {
        int id = 4;
        double expectedPrice = 999.9;

        double priceBefore = apartmentService.getById(id).getPrice();
        apartmentService.changePrice(id, expectedPrice);
        double priceAfter = apartmentService.getById(id).getPrice();

        assertNotEquals(expectedPrice, priceBefore);
        assertEquals(expectedPrice, priceAfter);
    }

    @Test
    void whenChangingStatus_thenItShouldReturnOppositeStatus() {
        int id = 2;

        ApartmentStatus statusBefore = apartmentService.getById(id).getStatus();
        apartmentService.changeStatus(id);
        ApartmentStatus statusAfter = apartmentService.getById(id).getStatus();

        assertNotEquals(statusBefore, statusAfter);
    }

    @Test
    void whenChangingStatusNotAllowed_thenExceptionShouldBeThrown() {
        int id = 2;

        hotelFactory.getConfig().getConfigData().getApartment().setAllowApartmentStatusChange(false);

        assertThrows(ConfigurationRestrictionException.class, () -> {
            apartmentService.changeStatus(id);
        });
    }


    @Test
    void given4Apartments_whenSortByID_thenGetCorrectList() {
        Apartment apartment1 = new Apartment(1, 1, 100.0, ApartmentStatus.AVAILABLE);
        Apartment apartment2 = new Apartment(2, 2, 350.0, ApartmentStatus.AVAILABLE);
        Apartment apartment3 = new Apartment(3, 4, 500.0, ApartmentStatus.AVAILABLE);
        Apartment apartment4 = new Apartment(4, 3, 200.0, ApartmentStatus.AVAILABLE);
        List<Apartment> expectedApartments = new ArrayList<>();
        expectedApartments.add(apartment1);
        expectedApartments.add(apartment2);
        expectedApartments.add(apartment3);
        expectedApartments.add(apartment4);

        List<Apartment> actualApartments = apartmentService.getSorted("id");

        assertEquals(expectedApartments, actualApartments);
    }


    @Test
    void given4Apartments_whenSortByCapacity_thenGetCorrectList() {
        Apartment apartment1 = new Apartment(1, 1, 100.0, ApartmentStatus.AVAILABLE);
        Apartment apartment2 = new Apartment(2, 2, 350.0, ApartmentStatus.AVAILABLE);
        Apartment apartment3 = new Apartment(3, 4, 500.0, ApartmentStatus.AVAILABLE);
        Apartment apartment4 = new Apartment(4, 3, 200.0, ApartmentStatus.AVAILABLE);
        List<Apartment> expectedApartments = new ArrayList<>();
        expectedApartments.add(apartment1);
        expectedApartments.add(apartment2);
        expectedApartments.add(apartment4);
        expectedApartments.add(apartment3);

        List<Apartment> actualApartments = apartmentService.getSorted("capacity");

        assertEquals(expectedApartments, actualApartments);
    }


    @Test
    void given4Apartments_whenSortByPrice_thenGetCorrectList() {

        Apartment apartment1 = new Apartment(1, 1, 100.0, ApartmentStatus.AVAILABLE);
        Apartment apartment2 = new Apartment(2, 2, 350.0, ApartmentStatus.AVAILABLE);
        Apartment apartment3 = new Apartment(3, 4, 500.0, ApartmentStatus.AVAILABLE);
        Apartment apartment4 = new Apartment(4, 3, 200.0, ApartmentStatus.AVAILABLE);
        List<Apartment> expectedApartments = new ArrayList<>();
        expectedApartments.add(apartment1);
        expectedApartments.add(apartment4);
        expectedApartments.add(apartment2);
        expectedApartments.add(apartment3);

        List<Apartment> actualApartments = apartmentService.getSorted("price");

        assertEquals(expectedApartments, actualApartments);
    }


    @Test
    void given4Apartments_whenSortByStatus_thenGetCorrectList() {
        Apartment apartment1 = new Apartment(1, 1, 100.0, ApartmentStatus.UNAVAILABLE);
        Apartment apartment2 = new Apartment(2, 2, 350.0, ApartmentStatus.UNAVAILABLE);
        Apartment apartment3 = new Apartment(3, 4, 500.0, ApartmentStatus.UNAVAILABLE);
        Apartment apartment4 = new Apartment(4, 3, 200.0, ApartmentStatus.AVAILABLE);

        List<Apartment> expectedApartments = new ArrayList<>();
        expectedApartments.add(apartment4);
        expectedApartments.add(apartment1);
        expectedApartments.add(apartment2);
        expectedApartments.add(apartment3);

        apartmentService.changeStatus(1);
        apartmentService.changeStatus(2);
        apartmentService.changeStatus(3);
        List<Apartment> actualApartments = apartmentService.getSorted("status");
        assertEquals(expectedApartments, actualApartments);
    }
}
