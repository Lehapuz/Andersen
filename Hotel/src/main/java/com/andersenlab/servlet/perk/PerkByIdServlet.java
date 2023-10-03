package com.andersenlab.servlet.perk;

import com.andersenlab.entity.Perk;
import com.andersenlab.exceptions.IdDoesNotExistException;
import com.andersenlab.service.PerkService;
import com.andersenlab.factory.ServletFactory;
import com.andersenlab.util.ServletUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(
        name = "PerkByIdServlet",
        urlPatterns = {"/perks/id"}
)
public class PerkByIdServlet extends HttpServlet {
    private PerkService perkService = ServletFactory.INSTANCE.getPerkService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/perks/id?id=2 for getById()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        try {
            Perk perk = perkService.getById(Long.parseLong(id));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), perk);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //EXAMPLE: http://localhost:8080/perks/id?id=2 for update()
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Perk perk = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Perk.class);
        String id = req.getParameter("id");
        try {
            perk.setId(Long.parseLong(id));
            Perk updatedPerk = perkService.update(perk);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), updatedPerk);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //EXAMPLE: http://localhost:8080/perks/id?id=2 for changePrice()
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Perk perk = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Perk.class);
        String id = req.getParameter("id");
        try {
            perk.setId(Long.parseLong(id));
            Perk changedPricePerk = perkService.changePrice(perk.getId(), perk.getPrice());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), changedPricePerk);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
