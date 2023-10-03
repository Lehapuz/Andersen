package com.andersenlab.servlet.client;

import com.andersenlab.entity.Client;
import com.andersenlab.service.ClientService;
import com.andersenlab.factory.ServletFactory;
import com.andersenlab.util.ServletUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "ClientsServlet",
        urlPatterns = {"/clients"}
)
public class ClientsServlet extends HttpServlet {
    private ClientService clientService = ServletFactory.INSTANCE.getClientService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/clients for getAll() and http://localhost:8080/clients?type=name for getSorted()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String sortType = req.getParameter("type");
        if (sortType != null && sortType.length() > 0) {
            getSortedClients(resp, sortType.toUpperCase());
        } else {
            getAll(resp);
        }
    }

    //EXAMPLE: http://localhost:8080/clients for save()
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        Client newClient = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Client.class);
        Client savedClient = clientService.save(newClient.getName(), newClient.getQuantityOfPeople());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), savedClient);
    }

    private void getAll(HttpServletResponse resp) throws IOException {
        List<Client> clients = clientService.getAll();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), clients);
    }

    private void getSortedClients(HttpServletResponse resp, String sortType) throws IOException {
        try {
            List<Client> clients = clientService.getSorted(sortType);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), clients);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
