package com.andersenlab.servlet;


import com.andersenlab.cleandb.CleanApartmentTable;
import com.andersenlab.cleandb.CleanClientTable;
import com.andersenlab.cleandb.CleanPerkTable;
import com.andersenlab.config.Config;
import com.andersenlab.util.StartServlet;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.Client;
import com.andersenlab.entity.ClientStatus;
import com.andersenlab.entity.Perk;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.util.ConfigHandler;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ClientServletTest {

    private static HotelFactory hotelFactory;


    @BeforeAll
    static void start() {
        try {
            StartServlet.getTomcat().start();
        } catch (LifecycleException e) {
            System.out.println(e.getMessage());
        }
        Config config = new Config();
        config.setConfigData(ConfigHandler.createConfig("src/main/resources/config/config-dev.yaml"));
        hotelFactory = new HotelFactory(config);
    }


    @BeforeEach
    void setup() {
        CleanClientTable cleanClientTable = new CleanClientTable(hotelFactory);
        CleanApartmentTable cleanApartmentTable = new CleanApartmentTable(hotelFactory);
        CleanPerkTable cleanPerkTable = new CleanPerkTable(hotelFactory);
        cleanClientTable.cleanTable();
        cleanApartmentTable.cleanTable();
        cleanPerkTable.cleanTable();
        hotelFactory.getClientService().save("Alex", 2);
        hotelFactory.getApartmentService().save(2, 4000.0);
        hotelFactory.getPerkService().save("laundry", 50);
        hotelFactory.getClientService().save("Zina", 2);
        hotelFactory.getApartmentService().save(4, 8000.0);
        hotelFactory.getPerkService().save("massage", 500);
    }


    @AfterAll
    static void stopServer() throws LifecycleException {
        StartServlet.getTomcat().stop();
    }


    @Test
    void add_new_client_to_hotel() {
        int expected = 3;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Petr");
        requestBody.put("quantityOfPeople", 2);
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("http://localhost:8080/clients")
                .then()
                .statusCode(201);
        Integer actual = hotelFactory.getClientService().getAll().size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void get_client_by_id_from_hotel_service() {
        Client expected = new Client();
        expected.setName("Alex");
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Client actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/clients/id?id="
                                + client.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Client.class);
        Assertions.assertEquals(expected.getName(), actual.getName());
    }


    @Test
    void update_client_by_id_in_hotel_service() {
        Client expected = new Client();
        expected.setName("Test");
        expected.setStatus(ClientStatus.CHECKED_IN);
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Test");
        requestBody.put("status", 1);
        Client actual =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .put("http://localhost:8080/clients/id?id="
                                + client.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Client.class);
        Assertions.assertEquals(expected.getName(), actual.getName());
    }


    @Test
    void get_stay_coast_for_client_in_hotel_service() {
        Double expected = 20000.0;
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        hotelFactory.getClientService().checkInApartment(client.getId(), 5, apartment.getId());
        Double actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/clients/stay-cost/id?id=" + client.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Double.class);
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void add_perk_to_client_in_hotel_service() {
        Perk expected = new Perk();
        expected.setName("laundry");
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        hotelFactory.getClientService().checkInApartment(client.getId(), 5, apartment.getId());
        Perk perk = hotelFactory.getPerkService().getAll().stream().findFirst().get();
        Perk actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .post("http://localhost:8080/clients/perks?clientId=" + client.getId() +
                                "&perkId=" + perk.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Perk.class);
        Assertions.assertEquals(expected.getName(), actual.getName());
    }


    @Test
    void get_perks_for_client_in_hotel_service() {
        Integer expected = 1;
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        Perk perk = hotelFactory.getPerkService().getAll().stream().findFirst().get();
        hotelFactory.getClientService().checkInApartment(client.getId(), 5, apartment.getId());
        hotelFactory.getClientService().addPerk(client.getId(), perk.id);
        List<Perk> actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/clients/perks?clientId=" + client.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(new TypeRef<List<Perk>>() {
                        });
        Assertions.assertEquals(expected, actual.size());
    }


    @Test
    void check_in_apartments_in_hotel_service() {
        Client expected = new Client();
        expected.setName("Alex");
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        Client actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/clients/checkin?clientId=" + client.getId() +
                                "&duration=5&apartmentId=" + apartment.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Client.class);
        Assertions.assertEquals(expected.getName(), actual.getName());
    }


    @Test
    void check_in_any_free_apartments_in_hotel_service() {
        Client expected = new Client();
        expected.setName("Alex");
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Client actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/clients/checkin?clientId=" + client.getId() + "&duration=5")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Client.class);
        Assertions.assertEquals(expected.getName(), actual.getName());
    }


    @Test
    void check_out_apartments_in_hotel_service() {
        Double expected = 20000.0;
        Client client = hotelFactory.getClientService().getAll().stream().findFirst().get();
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        hotelFactory.getClientService().checkInApartment(client.getId(), 5, apartment.getId());
        Double actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/clients/checkout?clientId=" + client.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Double.class);
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_clients_from_hotel_service() {
        Integer expected = 2;
        List<Client> clients = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/clients")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = clients.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_clients_by_name_from_hotel_service() {
        Integer expected = 2;
        List<Client> clients = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/clients?type=name")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = clients.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_clients_by_status_from_hotel_service() {
        Integer expected = 2;
        List<Client> clients = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/clients?type=status")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = clients.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_clients_by_id_from_hotel_service() {
        Integer expected = 2;
        List<Client> clients = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/clients?type=id")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = clients.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_clients_by_check_out_date_from_hotel_service() {
        Integer expected = 2;
        List<Client> clients = hotelFactory.getClientService().getAll();
        List<Apartment> apartments = hotelFactory.getApartmentService().getAll();
        Client client1 = clients.stream().findFirst().get();
        Apartment apartment1 = apartments.stream().findFirst().get();
        Collections.reverse(clients);
        Collections.reverse(apartments);
        Client client2 = clients.stream().findFirst().get();
        Apartment apartment2 = apartments.stream().distinct().findFirst().get();
        hotelFactory.getClientService().checkInApartment(client1.getId(), 5, apartment1.getId());
        hotelFactory.getClientService().checkInApartment(client2.getId(), 1, apartment2.getId());
        List<Client> expectedClients = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/clients?type=check_out_date")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = expectedClients.size();
        Assertions.assertEquals(expected, actual);
    }
}
