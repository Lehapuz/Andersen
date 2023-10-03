package com.andersenlab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Client")
public class Client {
    @Id
    @Column(name = "client_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "checkout")
    private LocalDateTime checkOutDate;
    @Column(name = "checkin")
    private LocalDateTime checkInDate;
    @Column(name = "status")
    private ClientStatus status;
    @OneToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_perk",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "perk_id")
    )
    private List<Perk> perks;
    @Column(name = "staycost")
    private double stayCost;
    @Column(name = "quantityofpeople")
    private int quantityOfPeople;

    public Client() {
    }

    public Client(long id, String name, int quantityOfPeople) {
        this.id = id;
        this.name = name;
        this.quantityOfPeople = quantityOfPeople;
        status = ClientStatus.NEW;
        perks = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public List<Perk> getPerks() {
        return perks;
    }

    public void setPerks(List<Perk> perks) {
        this.perks = perks;
    }

    public double getStayCost() {
        return stayCost;
    }

    public void setStayCost(double stayCost) {
        this.stayCost = stayCost;
    }

    public int getQuantityOfPeople() {
        return quantityOfPeople;
    }

    public void setQuantityOfPeople(int quantityOfPeople) {
        this.quantityOfPeople = quantityOfPeople;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id && Double.compare(client.stayCost, stayCost) == 0
                && quantityOfPeople == client.quantityOfPeople && Objects.equals(name, client.name)
                && Objects.equals(checkOutDate, client.checkOutDate)
                && Objects.equals(checkInDate, client.checkInDate)
                && status == client.status && Objects.equals(apartment, client.apartment)
                && Objects.equals(perks, client.perks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, checkOutDate, checkInDate, status, apartment, perks, stayCost, quantityOfPeople);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: ").append(id)
                .append(", name: ").append(name)
                .append(", quantity of people: ").append(quantityOfPeople)
                .append(", status: ").append(status);
        if (apartment != null) {
            builder.append(", apartment id: ")
                    .append(apartment.getId());
        }
        if (checkInDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            builder.append(", checking date: ")
                    .append(checkInDate.format(formatter));
        }
        if (checkOutDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            builder.append(", checkout date: ")
                    .append(checkOutDate.format(formatter));
        }
        if (!perks.isEmpty()) {
            builder.append(", extra services: [");
            for (int i = 0; i < perks.size(); i++) {
                builder.append(perks.get(i).getName());
                if (i != perks.size() - 1) {
                    builder.append(", ");
                } else {
                    builder.append("]");
                }
            }
        }
        builder.append(", current stay cost: ").append(stayCost);
        return builder.toString();
    }
}
