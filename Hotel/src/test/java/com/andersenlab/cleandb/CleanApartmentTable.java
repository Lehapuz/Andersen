package com.andersenlab.cleandb;

import com.andersenlab.config.SaveOption;
import com.andersenlab.dao.conection.ConnectionPool;
import com.andersenlab.dao.onDiskImpl.OnDiskJsonHandler;
import com.andersenlab.entity.Apartment;
import com.andersenlab.factory.HotelFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CleanApartmentTable {

    private final HotelFactory hotelFactory;

    public CleanApartmentTable(HotelFactory hotelFactory) {
        this.hotelFactory = hotelFactory;
    }

    public void cleanTable() {
        if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.HIBERNATE) {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hotel");
            try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
                entityManager.getTransaction().begin();
                List<Apartment> apartments = entityManager.createQuery("FROM Apartment ORDER BY id").getResultList();
                apartments.forEach(entityManager::remove);
                entityManager.getTransaction().commit();
            }
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.DISK) {
            OnDiskJsonHandler onDiskJsonHandler = new OnDiskJsonHandler(hotelFactory);
            var stateEntity = onDiskJsonHandler.load();
            var apartments = stateEntity.getApartmentsList();
            var copy = new ArrayList<>(apartments);
            copy.removeAll(apartments);
            onDiskJsonHandler.save(stateEntity.addApartmentList(copy));
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.JDBC) {
            try {
                ConnectionPool connectionPool = new ConnectionPool(hotelFactory.getConfig().getConfigData().getPostgresDatabase());
                Connection connection = connectionPool.getConnection();
                connection.prepareStatement("DELETE * FROM Apartment").executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.MEMORY) {
            List<Apartment> apartments = hotelFactory.getApartmentService().getAll();
            for (Apartment apartment : apartments) {
                apartments.remove(apartment);
            }
        }
    }
}
