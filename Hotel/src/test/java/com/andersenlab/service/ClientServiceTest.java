package com.andersenlab.service;

import com.andersenlab.config.Config;
import com.andersenlab.config.SaveOption;
import com.andersenlab.dao.onDiskImpl.OnDiskApartmentDaoImpl;
import com.andersenlab.dao.onDiskImpl.OnDiskClientDaoImpl;
import com.andersenlab.dao.onDiskImpl.OnDiskPerkDaoImpl;
import com.andersenlab.entity.*;
import com.andersenlab.exceptions.ClientAlreadyCheckedInException;
import com.andersenlab.exceptions.NoAvailableApartmentsException;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.util.ConfigHandler;
import com.andersenlab.util.IdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {

    private ClientService clientService;
    private HotelFactory hotelFactory;

    @BeforeEach
    public void setup() {
        IdGenerator.cancelGenerateId();
        Config config = new Config();
        config.setConfigData(ConfigHandler.createConfig("src/test/resources/config/config-test.yaml"));
        hotelFactory = new HotelFactory(config);
        clientService = hotelFactory.getClientService();
        ApartmentService apartmentService = hotelFactory.getApartmentService();
        PerkService perkService = hotelFactory.getPerkService();
        clientService.save("Oleg", 8);
        clientService.save("Alex", 2);
        clientService.save("Petr", 3);
        clientService.save("Lola", 5);
        apartmentService.save(9, 100.0);
        apartmentService.save(2, 550.0);
        apartmentService.save(4, 500.0);
        apartmentService.save(6, 200.0);
        perkService.save("ironing", 150);
        perkService.save("laundry", 100);
    }

    @AfterEach
    public void teardown() {
        if (this.hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.DISK) {
            OnDiskClientDaoImpl onDiskClientDao = new OnDiskClientDaoImpl(hotelFactory);
            clientService.getAll().forEach(client -> onDiskClientDao.remove(client.getId()));
            OnDiskApartmentDaoImpl onDiskApartmentDao = new OnDiskApartmentDaoImpl(hotelFactory);
            hotelFactory.getApartmentService().getAll().forEach(apartment -> onDiskApartmentDao.remove(apartment.getId()));
            OnDiskPerkDaoImpl onDiskPerkDao = new OnDiskPerkDaoImpl(hotelFactory);
            hotelFactory.getPerkService().getAll().forEach(perk -> onDiskPerkDao.remove(perk.getId()));
        }
    }

    @Test
    void whenSearchingForClientById_thenItShouldBeFound() {
        int id = 2;
        String expectedName = "Alex";
        int expectedQuantityOfPeople = 2;


        Client client = clientService.getById(id);
        String actualName = client.getName();
        int actualQuantityOfPeople = client.getQuantityOfPeople();

        assertEquals(expectedName, actualName);
        assertEquals(expectedQuantityOfPeople, actualQuantityOfPeople);
    }

    @Test
    void whenGetAll_thenStorageSizeShouldEqualTo4() {
        int expectedSize = 4;

        int actualSize = clientService.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void whenSavingNewClient_thenStorageSizeShouldIncreaseByOne() {
        int expectedSize = clientService.getAll().size() + 1;

        clientService.save("Dmytro", 2);

        int actualSize = clientService.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void whenUpdatingExistingClient_thenItShouldReturnUpdatedClient() {
        int id = 1;
        Client client = new Client(1, "Dmytro", 13);
        client.setStatus(ClientStatus.CHECKED_OUT);

        String nameBeforeUpdate = clientService.getById(id).getName();
        ClientStatus statusBefore = clientService.getById(id).getStatus();
        clientService.update(client);
        String nameAfterUpdate = clientService.getById(id).getName();
        ClientStatus statusAfter = clientService.getById(id).getStatus();

        assertNotEquals(nameBeforeUpdate, nameAfterUpdate);
        assertNotSame(statusBefore, statusAfter);
    }

    @Test
    void whenGettingStayCost_thenCorrectSumReturned() {
        int clientId = 4;
        int perk1Id = 1;
        int perk2Id = 2;
        int apartmentId = 4;

        double expectedClientStayCoast = 650.0;
        int expectedPerkListSize = 2;

        clientService.checkInApartment(clientId, 2, apartmentId);
        clientService.addPerk(clientId, perk1Id);
        clientService.addPerk(clientId, perk2Id);

        assertEquals(expectedPerkListSize, clientService.getAllPerks(clientId).size());
        assertEquals(expectedClientStayCoast, clientService.getStayCost(clientId));
    }

    @Test
    void whenClientCheckedIn_thenStatusShouldChangeOnCheckedInAndStayCostShouldIncrease() {
        int apartmentId = 1;
        int clientId = 1;

        ClientStatus statusBeforeCheckIn = clientService.getById(clientId).getStatus();
        double stayCostBeforeCheckIn = clientService.getById(clientId).getStayCost();

        clientService.checkInApartment(clientId, 1, apartmentId);

        ClientStatus statusAfterCheckIn = clientService.getById(clientId).getStatus();
        double stayCostAfterCheckIn = clientService.getById(clientId).getStayCost();

        assertNotSame(statusBeforeCheckIn, statusAfterCheckIn);
        assertSame(statusAfterCheckIn, ClientStatus.CHECKED_IN);
        assertTrue(stayCostBeforeCheckIn < stayCostAfterCheckIn);
    }

    @Test
    void whenCheckedInAnyFreeApartment_thenReturnTheFirstAvailableAndSuitableApartment() {
        int clientId = 1;

        long expectedApartmentId = 1;

        long actualApartmentId = clientService.checkInAnyFreeApartment(clientId, 1)
                .getApartment().getId();

        assertEquals(expectedApartmentId, actualApartmentId);
    }

    @Test
    void whenApartmentIsNotAvailableDuringCheckIn_thenExceptionShouldBeThrown() {
        Client client = clientService.save("Dmytro", 20);
        long clientID = client.getId();

        assertThrows(NoAvailableApartmentsException.class, () -> {
            clientService.checkInAnyFreeApartment(clientID, 8);
        });
        assertThrows(NoAvailableApartmentsException.class, () -> {
            clientService.checkInApartment(clientID, 18, 1);
        });
    }

    @Test
    void whenAlreadyCheckedInClientTriesToCheckInAgain_thenExceptionShouldBeThrown() {
        Client client = clientService.getById(1);
        client.setStatus(ClientStatus.CHECKED_IN);
        client.setApartment(new Apartment());
        clientService.update(client);
        long clientID = client.getId();


        assertThrows(ClientAlreadyCheckedInException.class, () -> {
            clientService.checkInApartment(clientID, 1, 1);
        });
        assertThrows(ClientAlreadyCheckedInException.class, () -> {
            clientService.checkInAnyFreeApartment(clientID, 1);
        });
    }

    @Test
    void whenClientCheckedOut_thenStatusShouldChangeOnCheckedOutAndStayCostShouldBeZero() {
        int apartmentId = 2;
        int clientId = 2;

        clientService.checkInApartment(clientId, 1, apartmentId);

        ClientStatus statusAfterCheckIn = clientService.getById(clientId).getStatus();
        double stayCostAfterCheckIn = clientService.getById(clientId).getStayCost();

        clientService.checkOutApartment(clientId);

        ClientStatus statusAfterCheckOut = clientService.getById(clientId).getStatus();
        double stayCostAfterCheckOut = clientService.getById(clientId).getStayCost();

        assertNotSame(statusAfterCheckIn, statusAfterCheckOut);
        assertSame(statusAfterCheckOut, ClientStatus.CHECKED_OUT);
        assertNotEquals(stayCostAfterCheckIn, stayCostAfterCheckOut, 0.0);
        assertEquals(0.0, stayCostAfterCheckOut);
    }

    @Test
    void whenAddNewPerkToClient_thenCorrectPerkShouldReturn() {
        int clientId = 1;
        int perkId = 2;
        int apartmentId = 1;
        Perk expectedPerk = new Perk(perkId, "laundry", 100);

        clientService.checkInApartment(clientId, 1, apartmentId);

        Perk actualPerk = clientService.addPerk(clientId, perkId);

        assertEquals(expectedPerk, actualPerk);
    }

    @Test
    void whenGettingAllPerks_thenListSizeShouldEqualToTheNumberOfPerksAdded() {
        int clientId = 4;
        int apartmentId = 4;
        int perk1Id = 1;
        int perk2Id = 2;

        int expectedPerkListSize = 2;

        clientService.checkInApartment(clientId, 1, apartmentId);
        clientService.addPerk(clientId, perk1Id);
        clientService.addPerk(clientId, perk2Id);

        int actualPerkListSize = clientService.getById(clientId).getPerks().size();

        assertEquals(expectedPerkListSize, actualPerkListSize);
    }

    @Test
    void given4Clients_whenSortByID_thenGetCorrectList() {
        List<Client> expectedClients = new ArrayList<>();
        expectedClients.add(new Client(1, "Oleg", 8));
        expectedClients.add(new Client(2, "Alex", 2));
        expectedClients.add(new Client(3, "Petr", 3));
        expectedClients.add(new Client(4, "Lola", 5));

        List<Client> actualClients = clientService.getSorted("id");

        assertEquals(expectedClients, actualClients);
    }


    @Test
    void given4Clients_whenSortByName_thenGetCorrectList() {
        List<Client> expectedClients = new ArrayList<>();
        expectedClients.add(new Client(2, "Alex", 2));
        expectedClients.add(new Client(4, "Lola", 5));
        expectedClients.add(new Client(1, "Oleg", 8));
        expectedClients.add(new Client(3, "Petr", 3));

        List<Client> actualClients = clientService.getSorted("name");

        assertEquals(expectedClients, actualClients);
    }


    @Test
    void given4Clients_whenSortByCheckOutDate_thenGetCorrectList() {
        List<Client> expectedClients = new ArrayList<>();
        Client client1 = new Client(1, "Oleg", 8);
        Client client2 = new Client(2, "Alex", 2);
        Client client3 = new Client(3, "Petr", 3);
        Client client4 = new Client(4, "Lola", 5);

        client1.setCheckInDate(LocalDateTime.of(2023, 8, 14, 12, 0));
        client1.setCheckOutDate(LocalDateTime.of(2023, 8, 19, 12, 0));
        client2.setCheckInDate(LocalDateTime.of(2023, 8, 14, 12, 0));
        client2.setCheckOutDate(LocalDateTime.of(2023, 8, 24, 12, 0));
        client4.setCheckInDate(LocalDateTime.of(2023, 8, 14, 12, 0));
        client3.setCheckOutDate(LocalDateTime.of(2023, 8, 15, 12, 0));
        client4.setCheckInDate(LocalDateTime.of(2023, 8, 14, 12, 0));
        client4.setCheckOutDate(LocalDateTime.of(2023, 8, 21, 12, 0));
        expectedClients.add(client3);
        expectedClients.add(client1);
        expectedClients.add(client4);
        expectedClients.add(client2);

        List<Client> actualClients = clientService.getSorted("checkOUTdate");

        for (int i = 0; i < actualClients.size(); i++) {
            assertSame(actualClients.get(i).getCheckOutDate().getDayOfMonth(),
                    expectedClients.get(i).getCheckOutDate().getDayOfMonth());
        }
    }


    @Test
    void given4Clients_whenSortByStatus_thenGetCorrectList() {
        List<Client> expectedClients = new ArrayList<>();
        Client client1 = new Client(1, "Oleg", 8);
        Client client2 = new Client(2, "Alex", 2);
        Client client3 = new Client(3, "Petr", 3);
        Client client4 = new Client(4, "Lola", 5);
        client1.setStatus(ClientStatus.CHECKED_IN);
        client3.setStatus(ClientStatus.CHECKED_OUT);
        client4.setStatus(ClientStatus.CHECKED_IN);
        expectedClients.add(client2);
        expectedClients.add(client1);
        expectedClients.add(client4);
        expectedClients.add(client3);

        clientService.checkInApartment(1, 5, 1);
        clientService.checkInApartment(3, 10, 4);
        clientService.checkOutApartment(3);
        clientService.checkInApartment(4, 7, 4);
        List<Client> actualClients = clientService.getSorted("status");

        for (int i = 0; i < actualClients.size(); i++) {
            assertSame(actualClients.get(i).getStatus(), expectedClients.get(i).getStatus());
        }
    }
}