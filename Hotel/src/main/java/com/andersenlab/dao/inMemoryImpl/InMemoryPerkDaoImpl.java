package com.andersenlab.dao.inMemoryImpl;

import com.andersenlab.dao.PerkDao;
import com.andersenlab.entity.Apartment;
import com.andersenlab.entity.Perk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryPerkDaoImpl implements PerkDao {

    private final List<Perk> perks;

    public InMemoryPerkDaoImpl() {
        this.perks = new ArrayList<>();
    }

    @Override
    public Optional<Perk> getById(long id) {
        return perks.stream()
                .filter(perk -> perk.getId() == id)
                .findFirst();
    }

    @Override
    public List<Perk> getAll() {
        return new ArrayList<>(perks);
    }

    @Override
    public Perk save(Perk perk) {
        perks.add(perk);
        return perk;
    }

    @Override
    public Optional<Perk> update(Perk perk) {
        Optional<Perk> existingPerk = getById(perk.getId());
        existingPerk.ifPresent(updPerk -> {
            if (perk.getName() != null) {
                updPerk.setName(perk.getName());
            }
            updPerk.setPrice(perk.getPrice());
        });
        return existingPerk;
    }

    @Override
    public boolean remove(long id) {
        return perks.removeIf(perk -> perk.getId() == id);
    }

    @Override
    public List<Perk> getSortedBy(PerkSortType type) {
        return switch (type) {
            case ID -> sortBy(Perk::getId);
            case NAME -> sortBy(Perk::getName);
            case PRICE -> sortBy(Perk::getPrice);
        };
    }

    private List<Perk> sortBy(Function<Perk, Comparable> extractor) {
        return getAll().stream()
                .sorted(Comparator.comparing(extractor))
                .toList();
    }
}
