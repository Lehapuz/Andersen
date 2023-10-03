package com.andersenlab.factory;

import com.andersenlab.config.Config;
import com.andersenlab.dao.JDBCImpl.JdbcApartmentDaoImpl;
import com.andersenlab.dao.JDBCImpl.JdbcClientDaoImpl;
import com.andersenlab.dao.JDBCImpl.JdbcPerkDaoImpl;
import com.andersenlab.dao.hibernateImpl.HibernateApartmentDaoImpl;
import com.andersenlab.dao.hibernateImpl.HibernateClientDaoImpl;
import com.andersenlab.dao.hibernateImpl.HibernatePerkDaoImpl;
import com.andersenlab.dao.inMemoryImpl.InMemoryApartmentDaoImpl;
import com.andersenlab.dao.inMemoryImpl.InMemoryClientDaoImpl;
import com.andersenlab.dao.inMemoryImpl.InMemoryPerkDaoImpl;
import com.andersenlab.dao.onDiskImpl.OnDiskApartmentDaoImpl;
import com.andersenlab.dao.onDiskImpl.OnDiskClientDaoImpl;
import com.andersenlab.dao.onDiskImpl.OnDiskPerkDaoImpl;
import com.andersenlab.service.*;
import com.andersenlab.service.impl.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.stereotype.Component;

@Component
public class HotelFactory {

    private final ClientService clientService;
    private final ApartmentService apartmentService;
    private final PerkService perkService;
    private final Config config;

    public HotelFactory(Config config) {
        this.config = config;
        switch (this.config.getConfigData().getSaveOption()) {
            case DISK -> {
                apartmentService = new ApartmentServiceImpl(new OnDiskApartmentDaoImpl(this), this);
                perkService = new PerkServiceImpl(new OnDiskPerkDaoImpl(this), this);
                clientService = new ClientServiceImpl(new OnDiskClientDaoImpl(this), this);
            }
            case JDBC -> {
                apartmentService = new ApartmentServiceImpl(new JdbcApartmentDaoImpl(this), this);
                perkService = new PerkServiceImpl(new JdbcPerkDaoImpl(this), this);
                clientService = new ClientServiceImpl(new JdbcClientDaoImpl(this), this);
            }
            case HIBERNATE -> {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hotel");
                apartmentService = new ApartmentServiceImpl(new HibernateApartmentDaoImpl(entityManagerFactory), this);
                perkService = new PerkServiceImpl(new HibernatePerkDaoImpl(entityManagerFactory), this);
                clientService = new ClientServiceImpl(new HibernateClientDaoImpl(entityManagerFactory), this);
            }
            default -> {
                this.apartmentService = new ApartmentServiceImpl(new InMemoryApartmentDaoImpl(), this);
                this.perkService = new PerkServiceImpl(new InMemoryPerkDaoImpl(), this);
                this.clientService = new ClientServiceImpl(new InMemoryClientDaoImpl(), this);
            }
        }
    }

    public ApartmentService getApartmentService() {
        return apartmentService;
    }

    public ClientService getClientService() {
        return clientService;
    }

    public PerkService getPerkService() {
        return perkService;
    }

    public Config getConfig() {
        return config;
    }
}
