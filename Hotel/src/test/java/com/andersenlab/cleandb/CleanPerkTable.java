package com.andersenlab.cleandb;

import com.andersenlab.config.SaveOption;
import com.andersenlab.dao.conection.ConnectionPool;
import com.andersenlab.dao.onDiskImpl.OnDiskJsonHandler;
import com.andersenlab.entity.Perk;
import com.andersenlab.factory.HotelFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CleanPerkTable {

    private final HotelFactory hotelFactory;

    public CleanPerkTable(HotelFactory hotelFactory) {
        this.hotelFactory = hotelFactory;
    }

    public void cleanTable() {
        if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.HIBERNATE) {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hotel");
            try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
                entityManager.getTransaction().begin();
                List<Perk> perks = entityManager.createQuery("FROM Perk ORDER BY id").getResultList();
                perks.forEach(entityManager::remove);
                entityManager.getTransaction().commit();
            }
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.DISK) {
            OnDiskJsonHandler onDiskJsonHandler = new OnDiskJsonHandler(hotelFactory);
            var stateEntity = onDiskJsonHandler.load();
            var perks = stateEntity.getPerksList();
            var copy = new ArrayList<>(perks);
            copy.removeAll(perks);
            onDiskJsonHandler.save(stateEntity.addPerkList(copy));
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.JDBC) {
            try {
                ConnectionPool connectionPool = new ConnectionPool(hotelFactory.getConfig().getConfigData().getPostgresDatabase());
                Connection connection = connectionPool.getConnection();
                connection.prepareStatement("DELETE * FROM Perk").executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.MEMORY) {
            List<Perk> perks = hotelFactory.getPerkService().getAll();
            for (Perk perk : perks) {
                perks.remove(perk);
            }
        }
    }
}
