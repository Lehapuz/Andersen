package com.andersenlab.dao.onDiskImpl;

import com.andersenlab.dao.PerkDao;
import com.andersenlab.entity.Perk;
import com.andersenlab.factory.HotelFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class OnDiskPerkDaoImpl implements PerkDao {
    private final OnDiskJsonHandler onDiskJsonHandler;

    public OnDiskPerkDaoImpl(HotelFactory hotelFactory) {
        this.onDiskJsonHandler = new OnDiskJsonHandler(hotelFactory);
    }

    @Override
    public Optional<Perk> getById(long id) {
        return onDiskJsonHandler.load().getPerksList()
                .stream()
                .filter(perk -> perk.getId() == id)
                .findFirst();
    }

    @Override
    public List<Perk> getAll() {
        return onDiskJsonHandler.load().getPerksList();
    }

    @Override
    public Perk save(Perk perk) {
        var stateEntity = onDiskJsonHandler.load();
        var perks = stateEntity.getPerksList();
        var copy = new ArrayList<>(perks);
        copy.add(perk);

        onDiskJsonHandler.save(stateEntity.addPerkList(copy));
        return perk;
    }

    @Override
    public Optional<Perk> update(Perk perk) {
        var stateEntity = onDiskJsonHandler.load();
        var existingPerk = stateEntity.getPerksList()
                .stream()
                .filter(perk1 -> perk1.getId() == perk.getId())
                .findFirst();

        existingPerk.ifPresent(updPerk -> {
            if (perk.getName() != null) {
                updPerk.setName(perk.getName());
            }
            updPerk.setPrice(perk.getPrice());
        });

        onDiskJsonHandler.save(stateEntity);
        return existingPerk;
    }

    @Override
    public boolean remove(long id) {
        var stateEntity = onDiskJsonHandler.load();
        var answer = stateEntity.getPerksList()
                .removeIf(perk -> perk.getId() == id);

        onDiskJsonHandler.save(stateEntity);
        return answer;
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