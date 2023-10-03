package com.andersenlab.dao.onDiskImpl;

import com.andersenlab.dao.ClientDao;
import com.andersenlab.entity.Client;
import com.andersenlab.entity.ClientStatus;
import com.andersenlab.factory.HotelFactory;

import java.util.*;
import java.util.function.Function;

public class OnDiskClientDaoImpl implements ClientDao {
    private final OnDiskJsonHandler onDiskJsonHandler;

    public OnDiskClientDaoImpl(HotelFactory hotelFactory) {
        this.onDiskJsonHandler = new OnDiskJsonHandler(hotelFactory);
    }

    @Override
    public Optional<Client> getById(long id) {
        return onDiskJsonHandler.load().getClientsList()
                .stream()
                .filter(client -> client.getId() == id)
                .findFirst();
    }

    @Override
    public List<Client> getAll() {
        return onDiskJsonHandler.load().getClientsList();
    }

    @Override
    public Client save(Client client) {
        var stateEntity = onDiskJsonHandler.load();
        var clients = stateEntity.getClientsList();
        var copy = new ArrayList<>(clients);
        copy.add(client);

        onDiskJsonHandler.save(stateEntity.addClientList(copy));
        return client;
    }

    @Override
    public Optional<Client> update(Client client) {
        var stateEntity = onDiskJsonHandler.load();
        var existingClient = stateEntity.getClientsList()
                .stream()
                .filter(client1 -> Objects.equals(client1.getId(), client.getId()))
                .findFirst();

        existingClient.ifPresent(updClient -> {
            existingClient.get().setName(client.getName());
            existingClient.get().setCheckOutDate(client.getCheckOutDate());
            existingClient.get().setCheckInDate(client.getCheckInDate());
            existingClient.get().setStatus(client.getStatus());
            existingClient.get().setApartment(client.getApartment());
            existingClient.get().setStayCost(client.getStayCost());
            existingClient.get().setPerks(client.getPerks());
        });

        onDiskJsonHandler.save(stateEntity);
        return existingClient;
    }

    @Override
    public boolean remove(long id) {
        var stateEntity = onDiskJsonHandler.load();
        var answer = stateEntity.getClientsList()
                .removeIf(client -> client.getId() == id);

        onDiskJsonHandler.save(stateEntity);
        return answer;
    }

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
