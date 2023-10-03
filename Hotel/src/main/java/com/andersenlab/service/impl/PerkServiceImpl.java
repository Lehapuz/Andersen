package com.andersenlab.service.impl;

import com.andersenlab.dao.PerkDao;
import com.andersenlab.entity.Perk;
import com.andersenlab.exceptions.IdDoesNotExistException;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.service.PerkService;
import com.andersenlab.util.EntityValidityCheck;
import com.andersenlab.util.IdGenerator;

import java.util.List;

public class PerkServiceImpl implements PerkService {
    private final PerkDao perkDao;

    public PerkServiceImpl(PerkDao perkDao, HotelFactory hotelFactory) {
        this.perkDao = perkDao;
    }

    @Override
    public Perk getById(long id) {
        return perkDao.getById(id)
                .orElseThrow(() -> new IdDoesNotExistException("Perk with this id doesn't exist. Id: " + id));
    }

    @Override
    public Perk save(String name, double price) {
        EntityValidityCheck.perkPriceCheck(price);
        return perkDao.save(new Perk(IdGenerator.generatePerkId(), name, price));
    }

    @Override
    public Perk update(Perk perk) {
        return perkDao.update(perk)
                .orElseThrow(() -> new IdDoesNotExistException("Perk with this id doesn't exist. Id: " + perk.getId()));
    }

    @Override
    public Perk changePrice(long id, double price) {
        EntityValidityCheck.perkPriceCheck(price);
        return update(new Perk(id, price));
    }

    @Override
    public List<Perk> getAll() {
        return perkDao.getAll();
    }

    @Override
    public List<Perk> getSorted(String type) {
        return perkDao.getSortedBy(
                switch (type.toLowerCase()) {
                    case "name" -> PerkDao.PerkSortType.NAME;
                    case "price" -> PerkDao.PerkSortType.PRICE;
                    default -> PerkDao.PerkSortType.ID;
                });
    }
}