package com.andersenlab.servlet.perk;

import com.andersenlab.entity.Perk;
import com.andersenlab.service.PerkService;
import com.andersenlab.factory.ServletFactory;
import com.andersenlab.util.ServletUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "PerksServlet",
        urlPatterns = {"/perks"}
)
public class PerksServlet extends HttpServlet {
    private PerkService perkService = ServletFactory.INSTANCE.getPerkService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/perks for getAll() and http://localhost:8080/perks?type=price for getSorted()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sortType = req.getParameter("type");
        if (sortType != null && sortType.length() > 0) {
            getSortedPerks(resp, sortType.toUpperCase());
        } else {
            getAll(resp);
        }
    }

    //EXAMPLE: http://localhost:8080/perks for save()
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Perk newPerk = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Perk.class);
        Perk savedPerk = perkService.save(newPerk.getName(), newPerk.getPrice());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), savedPerk);
    }

    private void getAll(HttpServletResponse resp) throws IOException {
        List<Perk> perks = perkService.getAll();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), perks);
    }

    private void getSortedPerks(HttpServletResponse resp, String sortType) throws IOException {
        try {
            List<Perk> perks = perkService.getSorted(sortType);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), perks);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
