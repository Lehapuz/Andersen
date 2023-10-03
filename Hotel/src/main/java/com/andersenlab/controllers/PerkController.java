package com.andersenlab.controllers;

import com.andersenlab.entity.Perk;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.service.PerkService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perks")
public class PerkController extends BaseController{
    private final PerkService perkService;

    public PerkController(HotelFactory hotelFactory) {
        this.perkService = hotelFactory.getPerkService();
    }

    @GetMapping("/id")
    public ResponseEntity<Perk> getById(@RequestParam long id) {
        return ResponseEntity.ok(perkService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Perk>> getAll() {
        return ResponseEntity.ofNullable(perkService.getAll());
    }

    @GetMapping(params={"type"})
    public ResponseEntity<List<Perk>> getSorted(@RequestParam String type) {
        return ResponseEntity.ofNullable(perkService.getSorted(type));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Perk> save(@RequestBody Perk perk) throws JsonProcessingException {
        return ResponseEntity.ofNullable(perkService.save(perk.getName(), perk.getPrice()));
    }

    @PutMapping(value = "/id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Perk> update(@RequestParam long id, @RequestBody Perk perk) throws JsonProcessingException {
        perk.setId(id);
        return ResponseEntity.ok(perkService.update(perk));
    }

    @PostMapping(value = "/id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Perk> changePrice(@RequestParam long id, @RequestBody Perk perk) throws JsonProcessingException {
        return ResponseEntity.ok(perkService.changePrice(id, perk.getPrice()));
    }
}
