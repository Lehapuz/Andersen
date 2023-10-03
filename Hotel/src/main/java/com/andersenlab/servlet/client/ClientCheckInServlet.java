package com.andersenlab.servlet.client;

import com.andersenlab.entity.Client;
import com.andersenlab.exceptions.IdDoesNotExistException;
import com.andersenlab.service.ClientService;
import com.andersenlab.factory.ServletFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ClientCheckInServlet",
        urlPatterns = {"/clients/checkin"}
)
public class ClientCheckInServlet extends HttpServlet {
    private ClientService clientService = ServletFactory.INSTANCE.getClientService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/clients/checkin?clientId=1&duration=5&apartmentId=2 for checkInApartment()
    //and http://localhost:8080/clients/checkin?clientId=1&duration=5 for checkInAnyFreeApartment()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String clientId = req.getParameter("clientId");
        String apartmentId = req.getParameter("apartmentId");
        String duration = req.getParameter("duration");
        if (apartmentId != null && apartmentId.length() > 0) {
            checkInApartment(resp, clientId, apartmentId, duration);
        } else {
            checkInAnyFreeApartment(resp, clientId, duration);
        }
    }

    private void checkInAnyFreeApartment(HttpServletResponse resp, String clientId, String duration) throws IOException {
        try {
            Client client = clientService.checkInAnyFreeApartment(Long.parseLong(clientId),
                    Integer.parseInt(duration));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), client);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void checkInApartment(HttpServletResponse resp, String clientId,
                                  String apartmentId, String duration) throws IOException {
        try {
            Client client = clientService.checkInApartment(Long.parseLong(clientId),
                    Integer.parseInt(duration), Long.parseLong(apartmentId));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), client);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
