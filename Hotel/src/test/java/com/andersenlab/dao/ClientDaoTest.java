package com.andersenlab.dao;

import com.andersenlab.dao.inMemoryImpl.InMemoryClientDaoImpl;
import com.andersenlab.entity.Client;
import com.andersenlab.util.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class ClientDaoTest {
    private ClientDao clientDao;
    private Client client, client1;


    @BeforeEach
    private void setup() {
        client = new Client(IdGenerator.generateClientId(), "Evgen", 1);
        client1 = new Client(IdGenerator.generateClientId(), "Viktoria", 2);

        clientDao = new InMemoryClientDaoImpl();
        clientDao.save(client);

    }

    @Test
    void inMemoryList_saveClientToMemory_EqualsOfClientInSetupAndReturnedTest() {
        Assertions.assertEquals(client1, clientDao.save(client1));
        Assertions.assertEquals(client1.getId(), Objects.requireNonNull(clientDao.getById(client1.getId()).orElse(null)).getId());
    }

    @Test
    void inMemoryList_getApartmentById_EqualsOfApartmentIdInSetupAndInMemoryTest() {
        Assertions.assertEquals(client.getId(), Objects.requireNonNull(clientDao.getById(client.getId()).orElse(null)).getId());
    }

    @Test
    void inMemoryList_getClientByNonExistingId_ReturnedIsEmptyTest() {
        Assertions.assertTrue(clientDao.getById(Long.MAX_VALUE).isEmpty());
    }

    @Test
    void inMemoryList_getAllClient_NotNullAndCountOfReturnedEqualsTwoTest() {
        clientDao.save(client1);
        Assertions.assertNotNull(clientDao.getAll());
        Assertions.assertEquals(2,  (long) clientDao.getAll().size());
    }

    @Test
    void inMemoryList_UpdateClientName_ReturnNotNullAndClientEqualsReturnedClientTest() {
            client.setName("Evgen-Evgen");

            Assertions.assertAll("clientUpdate",
                    () -> Assertions.assertNotNull(clientDao.update(client)),
                    () -> Assertions.assertEquals(client,clientDao.update(client).orElse(null))
            );
    }

    @Test
    void inMemoryList_RemoveClientByIdAndGetClientByDeletedID_ReturnedEmptyTest() {
        Assertions.assertTrue(clientDao.remove(client.getId()));
        Assertions.assertTrue(clientDao.getById(client.getId()).isEmpty());
    }
}
