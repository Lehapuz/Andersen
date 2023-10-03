package com.andersenlab.controllers;

import com.andersenlab.entity.Client;
import com.andersenlab.entity.Perk;
import com.andersenlab.factory.HotelFactory;
import com.andersenlab.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController extends BaseController{
    private final ClientService clientService;

    public ClientController(HotelFactory hotelFactory) {
        this.clientService = hotelFactory.getClientService();
    }

    @GetMapping("/id")
    public ResponseEntity<Client> getById(@RequestParam long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAll() {
        return ResponseEntity.ofNullable(clientService.getAll());
    }

    @GetMapping(params = {"type"})
    public ResponseEntity<List<Client>> getSorted(@RequestParam String type) {
        return ResponseEntity.ofNullable(clientService.getSorted(type));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> save(@RequestBody Client client) throws JsonProcessingException {
        return ResponseEntity.ofNullable(clientService.save(client.getName(), client.getQuantityOfPeople()));
    }

    @PutMapping(value = "/id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> update(@RequestParam long id, @RequestBody Client client) throws JsonProcessingException {
        client.setId(id);
        return ResponseEntity.ok(clientService.update(client));
    }

    @GetMapping("stay-cost/id")
    public Double getStayCost(@RequestParam long id) {
        return clientService.getStayCost(id);
    }

    @GetMapping("/perks")
    public ResponseEntity<List<Perk>> getClientPerks(@RequestParam long clientId) {
        return ResponseEntity.ofNullable(clientService.getAllPerks(clientId));
    }

    @GetMapping(value = "/perks", params = {"clientId","perkId"})
    public ResponseEntity<Perk> addPerkToClient(@RequestParam long clientId,
                                                @RequestParam long perkId) {
        return ResponseEntity.ok(clientService.addPerk(clientId, perkId));
    }

    @GetMapping(value = "/checkin", params = {"clientId","duration","apartmentId"})
    public ResponseEntity<Client> checkInApartment(@RequestParam long clientId,
                                                   @RequestParam int duration,
                                                   @RequestParam long apartmentId) {
        return ResponseEntity.ok(clientService.checkInApartment(clientId, duration, apartmentId));
    }

    @GetMapping(value = "/checkin", params = {"clientId","duration","!apartmentId"})
    public ResponseEntity<Client> checkInAnyFreeApartment(@RequestParam long clientId,
                                                          @RequestParam int duration) {
        return ResponseEntity.ok(clientService.checkInApartment(clientId, duration, 0));
    }

    @GetMapping("/checkout")
    public ResponseEntity checkOutApartment(@RequestParam long clientId) {
        return ResponseEntity.ok(clientService.checkOutApartment(clientId));
    }
}
