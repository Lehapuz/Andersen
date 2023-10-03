package com.andersenlab.dao;

import com.andersenlab.entity.Perk;

import java.util.*;

public interface PerkDao {
    Optional<Perk> getById(long id);

    List<Perk> getAll();

    Perk save(Perk perk);

    Optional<Perk> update(Perk perk);

    boolean remove(long id);

    List<Perk> getSortedBy(PerkSortType type);

    enum PerkSortType {
        ID, NAME, PRICE
    }
}
