package com.andersenlab.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Perk")
public class Perk {
    @Id
    @Column(name = "perk_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @Column(name = "name")
    public String name;
    @Column(name = "price")
    public double price;

    public Perk(long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Perk(long id, double price) {
        this.id = id;
        this.price = price;
    }

    public Perk() {
    }

    public long getId() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Perk perk = (Perk) o;
        return id == perk.id && Double.compare(perk.price, price) == 0 && Objects.equals(name, perk.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: ").append(id)
                .append(", name: ").append(name)
                .append(", price: ").append(price);
        return builder.toString();
    }
}
