package com.andersenlab.dao.hibernateImpl;

import com.andersenlab.dao.ClientDao;
import com.andersenlab.entity.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class HibernateClientDaoImpl implements ClientDao {
    private final EntityManagerFactory entityManagerFactory;

    public HibernateClientDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Client> getById(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Client client = entityManager.find(Client.class, id);
            entityManager.getTransaction().commit();
            return Optional.ofNullable(client);
        }
    }

    @Override
    public List<Client> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            List<Client> clients = entityManager.createQuery(
                            """
                            FROM Client c 
                            LEFT JOIN FETCH c.apartment a 
                            LEFT JOIN FETCH c.perks p 
                            ORDER BY c.id
                            """
                    ).getResultList();
            entityManager.getTransaction().commit();
            return clients;
        }
    }

    @Override
    public Client save(Client client) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            client.setId(0);
            entityManager.persist(client);
            entityManager.getTransaction().commit();
            return client;
        }
    }

    @Override
    public Optional<Client> update(Client client) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Optional<Client> existingClient = Optional.ofNullable(entityManager.find(Client.class, client.getId()));
            existingClient.ifPresent(clnt -> updateClientFields(clnt, client));
            entityManager.getTransaction().commit();
            return existingClient;
        }
    }

    @Override
    public boolean remove(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Optional<Client> existingCLient = Optional.ofNullable(entityManager.find(Client.class, id));
            existingCLient.ifPresent(clnt -> deleteClient(clnt, entityManager));
            return existingCLient.isPresent();
        }
    }

    @Override
    public List<Client> getSortedBy(ClientSortType type) {
        return switch (type) {
            case ID -> getAll();
            case NAME -> sortBy("c.name");
            case CHECK_OUT_DATE -> sortBy("c.checkOutDate");
            case STATUS -> sortBy("c.status");
        };
    }

    private List<Client> sortBy(String parameter) {
        String getAllQuery = "FROM Client c LEFT JOIN FETCH c.apartment a LEFT JOIN FETCH c.perks p ORDER BY " + parameter;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            List<Client> clients = entityManager.createQuery(getAllQuery).getResultList();
            entityManager.getTransaction().commit();
            return clients;
        }
    }

    private void updateClientFields(Client existingClient, Client updatedClient) {
        existingClient.setName(updatedClient.getName());
        existingClient.setCheckOutDate(updatedClient.getCheckOutDate());
        existingClient.setCheckInDate(updatedClient.getCheckInDate());
        existingClient.setStatus(updatedClient.getStatus());
        existingClient.setApartment(updatedClient.getApartment());
        existingClient.setPerks(updatedClient.getPerks());
        existingClient.setStayCost(updatedClient.getStayCost());
    }

    private void deleteClient(Client client, EntityManager entityManager) {
        entityManager.remove(client);
        entityManager.getTransaction().commit();
    }
}
