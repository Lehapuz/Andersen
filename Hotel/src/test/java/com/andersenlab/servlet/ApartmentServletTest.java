package com.andersenlab.servlet;

import com.andersenlab.cleandb.CleanApartmentTable;
import com.andersenlab.cleandb.CleanClientTable;
import com.andersenlab.config.Config;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.ApartmentStatus;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.util.ConfigHandler;
import com.andersenlab.util.StartServlet;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class ApartmentServletTest {

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
        cleanClientTable.cleanTable();
        cleanApartmentTable.cleanTable();
        hotelFactory.getClientService().save("Alex", 2);
        hotelFactory.getApartmentService().save(4, 4000.0);
        hotelFactory.getClientService().save("Zina", 2);
        hotelFactory.getApartmentService().save(2, 5000.0);
    }


    @AfterAll
    static void stopServer() throws LifecycleException {
        StartServlet.getTomcat().stop();
    }


    @Test
    void add_new_apartment_to_hotel_service() {
        Integer expected = 3;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("capacity", 10);
        requestBody.put("price", 5550.0);
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("http://localhost:8080/apartments")
                .then()
                .statusCode(201);
        Integer actual = hotelFactory.getApartmentService().getAll().size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void get_apartment_by_id_in_hotel_service() {
        Apartment expected = new Apartment();
        expected.setCapacity(4);
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        Apartment actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/apartments/id?id=" + apartment.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Apartment.class);
        Assertions.assertEquals(expected.getCapacity(), actual.getCapacity());
    }


    @Test
    void update_apartment_by_id_in_hotel_service() {
        Apartment expected = new Apartment();
        expected.setCapacity(11);
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("capacity", 11);
        requestBody.put("price", 5300.0);
        requestBody.put("status", "AVAILABLE");
        Apartment actual =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .put("http://localhost:8080/apartments/id?id=" + apartment.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Apartment.class);
        Assertions.assertEquals(expected.getCapacity(), actual.getCapacity());
    }


    @Test
    void change_apartment_price_in_hotel_service() {
        Double expected = 2500.0;
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("price", 2500);
        Apartment apartmentActual =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("http://localhost:8080/apartments/id?id=" + apartment.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Apartment.class);
        Double actual = apartmentActual.getPrice();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void change_apartment_status_in_hotel_service() {
        Apartment expected = new Apartment();
        expected.setStatus(ApartmentStatus.UNAVAILABLE);
        Apartment apartment = hotelFactory.getApartmentService().getAll().stream().findFirst().get();
        Apartment actual =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("http://localhost:8080/apartments/change-status/id?id=" + apartment.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Apartment.class);
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }


    @Test
    void look_all_apartments_in_hotel_service() {
        Integer expected = 2;
        List<Apartment> apartments = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/apartments")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = apartments.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_apartments_by_id_in_hotel_service() {
        Integer expected = 2;
        List<Apartment> apartments = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/apartments?type=id")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = apartments.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_apartments_by_price_in_hotel_service() {
        Integer expected = 2;
        List<Apartment> apartments = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/apartments?type=price")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = apartments.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_apartments_by_capacity_in_hotel_service() {
        Integer expected = 2;
        List<Apartment> apartments = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/apartments?type=capacity")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = apartments.size();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void look_all_sorted_apartments_by_status_in_hotel_service() {
        Integer expected = 2;
        List<Apartment> apartments = given()
                .contentType(ContentType.JSON)
                .when()
                .get("http://localhost:8080/apartments?type=status")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        Integer actual = apartments.size();
        Assertions.assertEquals(expected, actual);
    }
}
