package com.andersenlab.dao.JDBCImpl;

import com.andersenlab.dao.ApartmentDao;
import com.andersenlab.dao.ClientDao;
import com.andersenlab.dao.conection.ConnectionPool;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.Client;
import com.andersenlab.entity.ClientStatus;
import com.andersenlab.entity.Perk;
import com.andersenlab.exceptions.InappropriateValueException;
import com.andersenlab.factory.HotelFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcClientDaoImpl implements ClientDao {

    private final ConnectionPool connectionPool;
    private final ApartmentDao apartmentDao;

    private long lastID;

    public JdbcClientDaoImpl(HotelFactory hotelFactory) {
        this.connectionPool = new ConnectionPool(hotelFactory.getConfig().getConfigData().getPostgresDatabase());
        this.apartmentDao = new JdbcApartmentDaoImpl(hotelFactory);
        lastID = getClientLastId();
    }

    @Override
    public Optional<Client> getById(long id) {

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM Client WHERE client_id = ?")) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(setClientFields(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Filed to get Client by the ID!");
        }
    }

    @Override
    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM Client");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                clients.add(setClientFields(resultSet));
            }
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to list Clients");
        }
    }

    @Override
    public Client save(Client client) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement
                     ("INSERT INTO client (name, checkin, checkout, apartment_id, " +
                             "status, staycost, quantityofpeople) VALUES (?,?,?,?,?,?,?)")) {

            preparedStatement.setString(1, client.getName());

            if (client.getCheckInDate() != null) {
                Timestamp timestamp = Timestamp.valueOf(client.getCheckInDate());
                preparedStatement.setTimestamp(2, timestamp);
            } else {
                preparedStatement.setNull(2, Types.TIMESTAMP);
            }

            if (client.getCheckOutDate() != null) {
                Timestamp timestamp = Timestamp.valueOf(client.getCheckOutDate());
                preparedStatement.setTimestamp(3, timestamp);
            } else {
                preparedStatement.setNull(3, Types.TIMESTAMP);
            }

            if (client.getApartment() != null) {
                preparedStatement.setLong(4, client.getApartment().getId());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }

            preparedStatement.setInt(5, client.getStatus().ordinal());
            preparedStatement.setDouble(6, client.getStayCost());
            preparedStatement.setInt(7, client.getQuantityOfPeople());

            preparedStatement.executeUpdate();
            client.setId(++lastID);

            return client;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to add a Client");
        }
    }

    @Override
    public Optional<Client> update(Client client) {
        List<Perk> perkListFromDB = getPerksForClient(client.getId());
        List<Perk> clientPerks = client.getPerks();

        //checking if perks of client are the same
        if (clientPerks.equals(perkListFromDB) || client.getStatus().equals(ClientStatus.CHECKED_OUT)) {
            return updateClientWithoutPerks(client);
        } else {
            Perk addedPerk = clientPerks.get(clientPerks.size() - 1);

            if (perkListFromDB.contains(addedPerk)) {
                throw new InappropriateValueException("Perk was already served to this client!");
            } else {
                try (Connection connection = connectionPool.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "INSERT INTO client_perk (client_id, perk_id) VALUES (?, ?)")) {

                    preparedStatement.setLong(1, client.getId());
                    preparedStatement.setLong(2, addedPerk.getId());

                    preparedStatement.executeUpdate();
                    return updateClientWithoutPerks(client);
                } catch (SQLException e) {
                    throw new RuntimeException("Filer to add the Perk to the Client!");
                }
            }
        }
    }

    @Override
    public boolean remove(long id) {
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM Client WHERE client_id=?");
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to remove the Client!");
        }
    }

    @Override
    public List<Client> getSortedBy(ClientSortType type) {
        return switch (type) {
            case ID -> sortBy("client_id");
            case CHECK_OUT_DATE -> sortBy("checkout");
            case NAME -> sortBy("name");
            case STATUS -> sortBy("status");
        };
    }

    private List<Client> sortBy(String fieldName) {
        String query = "SELECT * FROM client ORDER BY " + fieldName;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Client> clients = new ArrayList<>();
            while (resultSet.next()) {
                clients.add(setClientFields(resultSet));
            }
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to sort Clients");
        }
    }

    private int getClientLastId() {
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT MAX(client_id) FROM client")) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Client setClientFields(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setId(resultSet.getLong("client_id"));
        client.setName(resultSet.getString("name"));

        Timestamp checkinTimestamp = resultSet.getTimestamp("checkin");
        if (checkinTimestamp != null) {
            LocalDateTime checkinDateTime = checkinTimestamp.toLocalDateTime();
            client.setCheckInDate(checkinDateTime);
        }
        Timestamp checkoutTimestamp = resultSet.getTimestamp("checkout");
        if (checkoutTimestamp != null) {
            LocalDateTime checkoutDateTime = checkoutTimestamp.toLocalDateTime();
            client.setCheckOutDate(checkoutDateTime);
        }
        long apartmentId = resultSet.getLong("apartment_id");
        client.setApartment(apartmentDao.getById(apartmentId).orElse(null));

        int statusValue = resultSet.getInt("status");
        switch (statusValue) {
            case 0 -> client.setStatus(ClientStatus.NEW);
            case 1 -> client.setStatus(ClientStatus.CHECKED_IN);
            case 2 -> client.setStatus(ClientStatus.CHECKED_OUT);
        }
        List<Perk> perks = getPerksForClient(client.getId());
        client.setPerks(perks);

        client.setStayCost(resultSet.getDouble("staycost"));
        client.setQuantityOfPeople(resultSet.getInt("quantityofpeople"));
        return client;
    }

    private List<Perk> getPerksForClient(long clientId) {
        List<Perk> perks = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.* FROM Perk p " +
                    "INNER JOIN Client_Perk cp ON p.perk_id = cp.perk_id " +
                    "WHERE cp.client_id = ?");
            preparedStatement.setLong(1, clientId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Perk perk = new Perk();
                perk.setId(resultSet.getLong("perk_id"));
                perk.setName(resultSet.getString("name"));
                perk.setPrice(resultSet.getDouble("price"));
                perks.add(perk);
            }
            return perks;
        } catch (SQLException e) {
            throw new RuntimeException("Filed to get Perks of the Client!");
        }
    }

    private Optional<Client> updateClientWithoutPerks(Client client) {
        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Client SET name=?, checkin=?, checkout=?, apartment_id=?, status=?, staycost=?, " +
                            "quantityofpeople=?" +
                            " WHERE client_id=?");

            preparedStatement.setString(1, client.getName());

            LocalDateTime checkinDateTime = client.getCheckInDate();
            if (checkinDateTime != null) {
                Timestamp checkinTimestamp = Timestamp.valueOf(checkinDateTime);
                preparedStatement.setTimestamp(2, checkinTimestamp);
            } else {
                preparedStatement.setNull(2, Types.TIMESTAMP);
            }
            LocalDateTime checkoutDateTime = client.getCheckOutDate();
            if (checkoutDateTime != null) {
                Timestamp checkoutTimestamp = Timestamp.valueOf(checkoutDateTime);
                preparedStatement.setTimestamp(3, checkoutTimestamp);
            } else {
                preparedStatement.setNull(3, Types.TIMESTAMP);
            }
            Apartment apartment = client.getApartment();
            if (apartment != null) {
                preparedStatement.setLong(4, apartment.getId());
            } else {
                preparedStatement.setNull(4, Types.BIGINT);
            }
            if (client.getStatus().equals(ClientStatus.CHECKED_OUT)) {
                removeAllPerksById(client.getId(), connection);
                setApartmentStatusAvailableAfterCheckout(client.getId(), connection);
            }
            preparedStatement.setInt(5, client.getStatus().ordinal());

            preparedStatement.setDouble(6, client.getStayCost());
            preparedStatement.setInt(7, client.getQuantityOfPeople());

            preparedStatement.setLong(8, client.getId());

            preparedStatement.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update the Client");
        }
        return Optional.of(client);
    }

    private boolean removeAllPerksById(long id, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM Client_Perk WHERE client_id=?");
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove all perks by ClientId!");
        }
    }

    private boolean setApartmentStatusAvailableAfterCheckout(long id, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.
                    prepareStatement("UPDATE apartment SET status = 1 FROM apartment ap JOIN client c " +
                            "ON ap.apartment_id = c.apartment_id WHERE client_id = ?");
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to change Apartment status after checkout!");
        }
    }
}