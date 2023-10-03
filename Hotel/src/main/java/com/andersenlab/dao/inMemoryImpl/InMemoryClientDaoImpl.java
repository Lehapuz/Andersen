package com.andersenlab.dao.inMemoryImpl;

import com.andersenlab.dao.ClientDao;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.Client;
import com.andersenlab.entity.ClientStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryClientDaoImpl implements ClientDao {

    private final List<Client> clients;

    public InMemoryClientDaoImpl() {
        this.clients = new ArrayList<>();
    }

    @Override
    public Optional<Client> getById(long id) {
        return clients.stream()
                .filter(client -> client.getId() == id)
                .findFirst();
    }

    @Override
    public List<Client> getAll() {
        return new ArrayList<>(clients);
    }

    @Override
    public Client save(Client client) {
        clients.add(client);
        return client;
    }

    @Override
    public Optional<Client> update(Client client) {
        Optional<Client> existingClient = getById(client.getId());
        existingClient.ifPresent(updClient -> {
            existingClient.get().setName(client.getName());
            existingClient.get().setCheckOutDate(client.getCheckOutDate());
            existingClient.get().setStatus(client.getStatus());
            existingClient.get().setApartment(client.getApartment());
            existingClient.get().setPerks(client.getPerks());
        });
        return existingClient;
    }

    @Override
    public boolean remove(long id) {
        return clients.removeIf(client -> client.getId() == id);
    }

    @Override
    public List<Client> getSortedBy(ClientSortType type) {
        return switch (type) {
            case ID -> sortBy(Client::getId);
            case CHECK_OUT_DATE -> sortByCheckOutDate();
            case NAME -> sortBy(Client::getName);
            case STATUS -> sortBy(Client::getStatus);
        };
    }
    private List<Client> sortBy(Function<Client, Comparable> extractor) {
        return getAll().stream()
                .sorted(Comparator.comparing(extractor))
                .toList();
    }
    private List<Client> sortByCheckOutDate() {
        return getAll().stream()
                .filter(client -> client.getStatus() != ClientStatus.NEW)
                .sorted(Comparator.comparing(Client::getCheckOutDate))
                .toList();
    }
}