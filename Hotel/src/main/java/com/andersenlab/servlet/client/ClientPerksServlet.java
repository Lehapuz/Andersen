package com.andersenlab.servlet.client;

import com.andersenlab.entity.Perk;
import com.andersenlab.exceptions.HotelException;
import com.andersenlab.exceptions.IdDoesNotExistException;
import com.andersenlab.service.ClientService;
import com.andersenlab.factory.ServletFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "ClientPerksServlet",
        urlPatterns = {"/clients/perks"}
)
public class ClientPerksServlet extends HttpServlet {
    private ClientService clientService = ServletFactory.INSTANCE.getClientService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/clients/perks?clientId=1
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("clientId");
        try {
            List<Perk> perks = clientService.getAllPerks(Long.parseLong(id));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), perks);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //EXAMPLE: http://localhost:8080/clients/perks?clientId=1&perkId=3
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String clientId = req.getParameter("clientId");
        String perkId = req.getParameter("perkId");
        try {
            Perk perk = clientService.addPerk(Long.parseLong(clientId), Long.parseLong(perkId));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), perk);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (HotelException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
