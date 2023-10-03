package com.andersenlab.controllers;

import com.andersenlab.entity.Apartment;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.service.ApartmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartments")
public class ApartmentController extends BaseController{
    private final ApartmentService apartmentService;

    public ApartmentController(HotelFactory hotelFactory) {
        this.apartmentService = hotelFactory.getApartmentService();
    }

    @GetMapping("/id")
    public ResponseEntity<Apartment> getById(@RequestParam long id) {
        return ResponseEntity.ok(apartmentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Apartment>> getAll() {
        return ResponseEntity.ofNullable(apartmentService.getAll());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Apartment> save(@RequestBody Apartment apartment) throws JsonProcessingException {
        return ResponseEntity.ofNullable(apartmentService.save(apartment.getCapacity(), apartment.getPrice()));
    }

    @GetMapping(params={"type"})
    public ResponseEntity<List<Apartment>> getSorted(@RequestParam String type) {
        return ResponseEntity.ofNullable(apartmentService.getSorted(type));
    }

    @PutMapping(value = "/id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Apartment> update(@RequestParam long id, @RequestBody Apartment apartment)
            throws JsonProcessingException {
        apartment.setId(id);
        return ResponseEntity.ok(apartmentService.update(apartment));
    }

    @PostMapping(value = "/id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Apartment> changePrice(@RequestParam long id, @RequestBody Apartment apartment)
            throws JsonProcessingException {
        return ResponseEntity.ok(apartmentService.changePrice(id, apartment.getPrice()));
    }

    @GetMapping("/change-status/id")
    public ResponseEntity<Apartment> changeStatus(@RequestParam long id) {
        return ResponseEntity.ok(apartmentService.changeStatus(id));
    }
}
