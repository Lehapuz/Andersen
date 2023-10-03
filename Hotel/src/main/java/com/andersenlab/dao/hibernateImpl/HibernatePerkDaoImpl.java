package com.andersenlab.dao.hibernateImpl;

import com.andersenlab.dao.PerkDao;
import com.andersenlab.entity.Perk;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class HibernatePerkDaoImpl implements PerkDao {
    private final EntityManagerFactory entityManagerFactory;

    public HibernatePerkDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Perk> getById(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Perk perk = entityManager.find(Perk.class, id);
            entityManager.getTransaction().commit();
            return Optional.ofNullable(perk);
        }
    }

    @Override
    public List<Perk> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            List<Perk> perks = entityManager.createQuery("FROM Perk ORDER BY id").getResultList();
            entityManager.getTransaction().commit();
            return perks;
        }
    }

    @Override
    public Perk save(Perk perk) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            perk.setId(0);
            entityManager.persist(perk);
            entityManager.getTransaction().commit();
            return perk;
        }
    }

    @Override
    public Optional<Perk> update(Perk perk) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Optional<Perk> existingPerk = Optional.ofNullable(entityManager.find(Perk.class, perk.getId()));
            existingPerk.ifPresent(prk -> updatePerkFields(prk, perk));
            entityManager.getTransaction().commit();
            return existingPerk;
        }
    }

    @Override
    public boolean remove(long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Optional<Perk> existingPerk = Optional.ofNullable(entityManager.find(Perk.class, id));
            existingPerk.ifPresent(perk -> deletePerk(perk, entityManager));
            return existingPerk.isPresent();
        }
    }

    @Override
    public List<Perk> getSortedBy(PerkSortType type) {
        return switch (type) {
            case ID -> getAll();
            case NAME -> sortBy("name");
            case PRICE -> sortBy("price");
        };
    }

    private List<Perk> sortBy(String parameter) {
        String getAllQuery = "FROM Perk ORDER BY " + parameter;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            List<Perk> perks = entityManager.createQuery(getAllQuery).getResultList();
            entityManager.getTransaction().commit();
            return perks;
        }
    }

    private void updatePerkFields(Perk existingPerk, Perk updatedPerk) {
        if (updatedPerk.getName() != null) {
            existingPerk.setName(updatedPerk.getName());
        }
        existingPerk.setPrice(updatedPerk.getPrice());
    }

    private void deletePerk(Perk perk, EntityManager entityManager) {
        entityManager.remove(perk);
        entityManager.getTransaction().commit();
    }
}
