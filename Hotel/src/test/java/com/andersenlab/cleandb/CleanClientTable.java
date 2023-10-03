package com.andersenlab.cleandb;

import com.andersenlab.config.SaveOption;
import com.andersenlab.dao.conection.ConnectionPool;
import com.andersenlab.dao.onDiskImpl.OnDiskJsonHandler;
import com.andersenlab.entity.Client;
import com.andersenlab.factory.HotelFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CleanClientTable {

    private final HotelFactory hotelFactory;

    public CleanClientTable(HotelFactory hotelFactory) {
        this.hotelFactory = hotelFactory;
    }

    public void cleanTable() {
        if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.HIBERNATE) {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hotel");
            try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
                entityManager.getTransaction().begin();
                List<Client> clients = entityManager.createQuery("FROM Client ORDER BY id").getResultList();
                clients.forEach(entityManager::remove);
                entityManager.getTransaction().commit();
            }
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.DISK) {
            OnDiskJsonHandler onDiskJsonHandler = new OnDiskJsonHandler(hotelFactory);
            var stateEntity = onDiskJsonHandler.load();
            var clients = stateEntity.getClientsList();
            var copy = new ArrayList<>(clients);
            copy.removeAll(clients);
            onDiskJsonHandler.save(stateEntity.addClientList(copy));
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.JDBC) {
            try {
                ConnectionPool connectionPool = new ConnectionPool(hotelFactory.getConfig().getConfigData().getPostgresDatabase());
                Connection connection = connectionPool.getConnection();
                connection.prepareStatement("DELETE * FROM Client").executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.MEMORY) {
            List<Client> clients = hotelFactory.getClientService().getAll();
            for (Client client : clients) {
                clients.remove(client);
            }
        }
    }
}
