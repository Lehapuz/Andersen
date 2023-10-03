package com.andersenlab.factory;

import com.andersenlab.config.Config;
import com.andersenlab.service.ApartmentService;
import com.andersenlab.service.ClientService;
import com.andersenlab.service.PerkService;
import com.andersenlab.util.ConfigHandler;

public enum ServletFactory {
    INSTANCE;
    private final ApartmentService apartmentService;
    private final PerkService perkService;
    private final ClientService clientService;

    ServletFactory() {
        var config = new Config();
        config.setConfigData(ConfigHandler.createConfig("src/main/resources/config/config-dev.yaml"));
        var hotelFactory = new HotelFactory(config);
        apartmentService = hotelFactory.getApartmentService();
        perkService = hotelFactory.getPerkService();
        clientService = hotelFactory.getClientService();
    }

    public ApartmentService getApartmentService() {
        return apartmentService;
    }

    public PerkService getPerkService() {
        return perkService;
    }

    public ClientService getClientService() {
        return clientService;
    }
}
