package com.andersenlab.service;

import com.andersenlab.config.Config;
import com.andersenlab.config.SaveOption;
import com.andersenlab.dao.onDiskImpl.OnDiskPerkDaoImpl;
import com.andersenlab.entity.Perk;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.util.ConfigHandler;
import com.andersenlab.util.IdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PerkServiceTest {

    private PerkService perkService;
    private HotelFactory hotelFactory;


    @BeforeEach
    void setup() {
        IdGenerator.cancelGenerateId();
        Config config = new Config();
        config.setConfigData(ConfigHandler.createConfig("src/test/resources/config/config-test.yaml"));
        hotelFactory = new HotelFactory(config);
        perkService = hotelFactory.getPerkService();
        perkService.save("massage", 300);
        perkService.save("ironing", 150);
        perkService.save("laundry", 100);
    }

    @AfterEach
    void teardown() {
        if (this.hotelFactory.getConfig().getConfigData().getSaveOption() == SaveOption.DISK) {
                OnDiskPerkDaoImpl onDiskPerkDao = new OnDiskPerkDaoImpl(hotelFactory);
                perkService.getAll().forEach(perk -> onDiskPerkDao.remove(perk.getId()));
        }
    }
    @Test
    void whenSearchingForPerkById_thenItShouldBeFound() {
        int id = 3;
        String expectedName = "laundry";

        Perk perk = perkService.getById(id);
        String actualName = perk.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void whenSavingNewPerk_thenStorageSizeShouldIncreaseByOne() {
        int expectedSize = perkService.getAll().size() + 1;

        perkService.save("Delivery", 130);

        int actualSize = perkService.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void whenUpdatingExistingPerk_thenItShouldReturnUpdatedPerk() {
        int id = 1;
        Perk perk = new Perk(id, "Test name", 301);

        String nameBeforeUpdate = perkService.getById(id).getName();
        double priceBeforeUpdate = perkService.getById(id).getPrice();
        perkService.update(perk);
        String nameAfterUpdate = perkService.getById(id).getName();
        double priceAfterUpdate = perkService.getById(id).getPrice();

        assertNotEquals(nameBeforeUpdate, nameAfterUpdate);
        assertFalse(priceBeforeUpdate == priceAfterUpdate);
    }
    @Test
    void whenChangingPrice_thenItShouldReturnNewPrice() {
        int id = 1;
        double expectedPrice = 999.9;

        double priceBefore = perkService.getById(id).getPrice();
        perkService.changePrice(id, expectedPrice);
        double priceAfter = perkService.getById(id).getPrice();

        assertNotEquals(expectedPrice, priceBefore);
        assertEquals(expectedPrice, priceAfter);
    }
    @Test
    void whenGetAll_thenStorageSizeShouldEqualTo3() {
        int expectedSize = 3;

        int actualSize = perkService.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void given3Perks_whenSortByID_thenGetCorrectList() {
        Perk perk1 = new Perk(1, "massage", 300);
        Perk perk2 = new Perk(2, "ironing", 150);
        Perk perk3 = new Perk(3, "laundry", 100);
        List<Perk> expectedPerks = new ArrayList<>();
        expectedPerks.add(perk1);
        expectedPerks.add(perk2);
        expectedPerks.add(perk3);

        List<Perk> actualPerks = perkService.getSorted("id");

        assertEquals(expectedPerks, actualPerks);
    }


    @Test
    void given3Perks_whenSortByName_thenGetCorrectList() {
        Perk perk1 = new Perk(1, "massage", 300);
        Perk perk2 = new Perk(2, "ironing", 150);
        Perk perk3 = new Perk(3, "laundry", 100);
        List<Perk> expectedPerks = new ArrayList<>();
        expectedPerks.add(perk2);
        expectedPerks.add(perk3);
        expectedPerks.add(perk1);

        List<Perk> actualPerks = perkService.getSorted("name");

        assertEquals(expectedPerks, actualPerks);
    }


    @Test
    void given3Perks_whenSortByPrice_thenGetCorrectList() {
        Perk perk1 = new Perk(1, "massage", 300);
        Perk perk2 = new Perk(2, "ironing", 150);
        Perk perk3 = new Perk(3, "laundry", 100);
        List<Perk> expectedPerks = new ArrayList<>();
        expectedPerks.add(perk3);
        expectedPerks.add(perk2);
        expectedPerks.add(perk1);

        List<Perk> actualPerks = perkService.getSorted("price");

        assertEquals(expectedPerks, actualPerks);
    }
}
