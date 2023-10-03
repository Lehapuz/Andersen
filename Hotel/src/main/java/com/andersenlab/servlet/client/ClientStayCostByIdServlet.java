package com.andersenlab.servlet.client;

import com.andersenlab.exceptions.IdDoesNotExistException;
import com.andersenlab.service.ClientService;
import com.andersenlab.factory.ServletFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ClientStayCostByIdServlet",
        urlPatterns = {"/clients/stay-cost/id"}
)
public class ClientStayCostByIdServlet extends HttpServlet {
    private ClientService clientService = ServletFactory.INSTANCE.getClientService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/clients/stay-cost/id?id=1 for getStayCost()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        try {
            double stayCost = clientService.getStayCost(Long.parseLong(id));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), stayCost);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
