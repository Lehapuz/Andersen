package com.andersenlab.dao.onDiskImpl;

import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.Client;
import com.andersenlab.entity.Perk;

import java.util.List;

public class StateEntity {
    private List<Apartment> apartmentsList;
    private List<Client> clientsList;
    private List<Perk> perksList;

    public StateEntity(List<Apartment> apartmentsList, List<Client> clientsList, List<Perk> perksList) {
        this.apartmentsList = apartmentsList;
        this.clientsList = clientsList;
        this.perksList = perksList;
    }

    public StateEntity() {
        this(List.of(), List.of(), List.of());
    }

    public StateEntity addPerkList(List<Perk> perks) {
        return new StateEntity(apartmentsList, clientsList, perks);
    }

    public StateEntity addApartmentList(List<Apartment> apartments) {
        return new StateEntity(apartments, clientsList, perksList);
    }

    public StateEntity addClientList(List<Client> clients) {
        return new StateEntity(apartmentsList, clients, perksList);
    }

    public List<Apartment> getApartmentsList() {
        return apartmentsList;
    }

    public void setApartmentsList(List<Apartment> apartmentsList) {
        this.apartmentsList = apartmentsList;
    }

    public List<Client> getClientsList() {
        return clientsList;
    }

    public void setClientsList(List<Client> clientsList) {
        this.clientsList = clientsList;
    }

    public List<Perk> getPerksList() {
        return perksList;
    }

    public void setPerksList(List<Perk> perksList) {
        this.perksList = perksList;
    }
}
