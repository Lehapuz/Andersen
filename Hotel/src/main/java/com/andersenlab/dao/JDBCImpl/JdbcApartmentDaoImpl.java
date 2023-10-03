package com.andersenlab.dao.JDBCImpl;

import com.andersenlab.dao.ApartmentDao;
import com.andersenlab.dao.conection.ConnectionPool;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.ApartmentStatus;
import com.andersenlab.factory.HotelFactory;
import org.hibernate.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcApartmentDaoImpl implements ApartmentDao {
    private final ConnectionPool connectionPool;

    private long lastID;

    public JdbcApartmentDaoImpl(HotelFactory hotelFactory) {
        this.connectionPool = new ConnectionPool(hotelFactory.getConfig().getConfigData().getPostgresDatabase());
        lastID = getApartmentLastId();
    }

    @Override
    public Optional<Apartment> getById(long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM Apartment WHERE apartment_id = ?")) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(setApartmentFields(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get the Apartment by ID!");
        }
    }

    @Override
    public List<Apartment> getAll() {
        List<Apartment> apartments = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM Apartment");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                apartments.add(setApartmentFields(resultSet));
            }
            return apartments;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list Apartments!");
        }
    }

    @Override
    public Apartment save(Apartment apartment) {

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("insert into apartment (capacity, price, status) values (?, ?, ?)")) {

            preparedStatement.setInt(1, apartment.getCapacity());
            preparedStatement.setDouble(2, apartment.getPrice());
            preparedStatement.setInt(3, apartment.getStatus().ordinal());
            preparedStatement.executeUpdate();

            apartment.setId(++lastID);
            return apartment;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save the Apartment!");
        }
    }

    @Override
    public Optional<Apartment> update(Apartment apartment) {
        try (Connection connection = connectionPool.getConnection()) {
            if (apartment.getPrice() != 0.0) {
                updateField(connection, apartment.getId(), "price", apartment.getPrice());
            }
            if (apartment.getCapacity() != 0) {
                updateField(connection, apartment.getId(), "capacity", apartment.getCapacity());
            }
            if (apartment.getStatus() != null) {
                updateField(connection, apartment.getId(), "status", apartment.getStatus().ordinal());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Filed to Update the Apartment!");
        }
        return Optional.of(apartment);
    }

    @Override
    public boolean remove(long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("DELETE FROM Apartment WHERE apartment_id=?")) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove the Apartment!");
        }
    }

    @Override
    public List<Apartment> getSortedBy(ApartmentSortType type) {
        return switch (type) {
            case ID -> sortBy("apartment_id");
            case PRICE -> sortBy("price");
            case CAPACITY -> sortBy("capacity");
            case STATUS -> sortBy("status");
        };
    }

    private List<Apartment> sortBy(String fieldName) {
        String query = "SELECT * FROM apartment ORDER BY " + fieldName;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Apartment> apartments = new ArrayList<>();
            while (resultSet.next()) {
                apartments.add(setApartmentFields(resultSet));
            }
            return apartments;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to sort Apartments");
        }
    }

    private int getApartmentLastId() {
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT MAX(apartment_id) FROM apartment")) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Apartment setApartmentFields(ResultSet resultSet) throws SQLException {
        Apartment apartment = new Apartment();
        apartment.setId(resultSet.getLong("apartment_id"));
        apartment.setCapacity(resultSet.getInt("capacity"));
        apartment.setPrice(resultSet.getDouble("price"));
        int statusValue = resultSet.getInt("status");
        String statusString = (statusValue == 0) ? "AVAILABLE" : "UNAVAILABLE";
        apartment.setStatus(ApartmentStatus.valueOf(statusString));

        return apartment;
    }

    private void updateField(Connection connection, long apartmentId, String fieldName, Object value) throws SQLException {
        String query = "UPDATE Apartment SET " + fieldName + "=? WHERE apartment_id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, value);
            preparedStatement.setLong(2, apartmentId);
            preparedStatement.executeUpdate();
        }
    }
}