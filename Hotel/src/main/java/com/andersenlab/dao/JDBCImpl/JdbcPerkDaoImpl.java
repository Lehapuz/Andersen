package com.andersenlab.dao.JDBCImpl;

import com.andersenlab.dao.PerkDao;
import com.andersenlab.dao.conection.ConnectionPool;
import com.andersenlab.entity.Perk;
import com.andersenlab.factory.HotelFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPerkDaoImpl implements PerkDao {
    private final ConnectionPool connectionPool;
    private long lastID;

    public JdbcPerkDaoImpl(HotelFactory hotelFactory) {
        this.connectionPool = new ConnectionPool(hotelFactory.getConfig().getConfigData().getPostgresDatabase());
        lastID = getPerkLastId();
    }

    @Override
    public Optional<Perk> getById(long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM Perk WHERE perk_id = ?")) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(setPerkFields(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get Perk by ID!");
        }
    }

    @Override
    public List<Perk> getAll() {
        List<Perk> perks = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM Perk");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                perks.add(setPerkFields(resultSet));
            }
            return perks;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to list Perks!");
        }
    }

    @Override
    public Perk save(Perk perk) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("insert into perk (name, price) values (?, ?)")) {

            preparedStatement.setString(1, String.valueOf(perk.getName()));
            preparedStatement.setDouble(2, perk.getPrice());
            preparedStatement.executeUpdate();

            perk.setId(++lastID);
            return perk;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to save the Perk!");
        }
    }

    @Override
    public Optional<Perk> update(Perk perk) {
        try (Connection connection = connectionPool.getConnection()) {
            if (perk.getPrice() != 0.0) {
                updateField(connection, perk.getId(), "price", perk.getPrice());
            }
            if (perk.getName() != null) {
                updateField(connection, perk.getId(), "name", perk.getName());
            }
            return Optional.of(perk);
        } catch (SQLException e) {
            throw new RuntimeException("Filed to update the Perk!");
        }
    }

    @Override
    public boolean remove(long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("DELETE FROM Perk WHERE perk_id=?")) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to remove the Perk!");
        }
    }

    @Override
    public List<Perk> getSortedBy(PerkSortType type) {
        return switch (type) {
            case ID -> sortBy("perk_id");
            case NAME -> sortBy("name");
            case PRICE -> sortBy("price");
        };
    }

    private List<Perk> sortBy(String fieldName) {
        String query = "SELECT * FROM perk ORDER BY " + fieldName;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Perk> perks = new ArrayList<>();
            while (resultSet.next()) {
                perks.add(setPerkFields(resultSet));
            }
            return perks;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to sort Perks");
        }
    }

    private int getPerkLastId() {
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT MAX(perk_id) FROM perk")) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Perk setPerkFields(ResultSet resultSet) throws SQLException {
        Perk perk = new Perk();
        perk.setId(resultSet.getLong("perk_id"));
        perk.setName(String.valueOf(resultSet.getString("name")));
        perk.setPrice(resultSet.getDouble("price"));
        return perk;
    }

    private void updateField(Connection connection, long perkId, String fieldName, Object value) throws SQLException {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("UPDATE Perk SET " + fieldName + "=? WHERE perk_id=?")) {

            preparedStatement.setObject(1, value);
            preparedStatement.setLong(2, perkId);
            preparedStatement.executeUpdate();
        }
    }
}
