package com.andersenlab.servlet.client;

import com.andersenlab.entity.Client;
import com.andersenlab.exceptions.IdDoesNotExistException;
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

@WebServlet(
        name = "ClientByIdServlet",
        urlPatterns = {"/clients/id"}
)
public class ClientByIdServlet extends HttpServlet {
    private ClientService clientService = ServletFactory.INSTANCE.getClientService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/clients/id?id=1 for getById()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String id = req.getParameter("id");
        try {
            Client client = clientService.getById(Long.parseLong(id));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), client);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //EXAMPLE: http://localhost:8080/clients/id?id=1 for update()
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Client client = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Client.class);
        String id = req.getParameter("id");
        try {
            client.setId(Long.parseLong(id));
            Client updatedClient = clientService.update(client);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), updatedClient);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
