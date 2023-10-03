package com.andersenlab.service;

import com.andersenlab.entity.Perk;

import java.util.*;

public interface PerkService {
    Perk getById(long id);

    Perk save(String name, double price);

    Perk update(Perk perk);

    Perk changePrice(long id, double price);

    List<Perk> getAll();

    List<Perk> getSorted(String type);
}
