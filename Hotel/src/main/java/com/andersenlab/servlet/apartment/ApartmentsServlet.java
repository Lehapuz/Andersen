package com.andersenlab.servlet.apartment;


import com.andersenlab.entity.Apartment;
import com.andersenlab.service.ApartmentService;
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
        name = "ApartmentsServlet",
        urlPatterns = {"/apartments"}
)
public class ApartmentsServlet extends HttpServlet {
    private ApartmentService apartmentService = ServletFactory.INSTANCE.getApartmentService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/apartments for getAll() and http://localhost:8080/apartments?type=capacity for getSorted()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sortType = req.getParameter("type");
        if (sortType != null && sortType.length() > 0) {
            getSortedApartments(resp, sortType.toUpperCase());
        } else {
            getAll(resp);
        }
    }

    //EXAMPLE: http://localhost:8080/apartments for save()
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Apartment newApartment = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Apartment.class);
        Apartment savedApartment = apartmentService.save(newApartment.getCapacity(), newApartment.getPrice());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), savedApartment);
    }

    private void getAll(HttpServletResponse resp) throws IOException {
        List<Apartment> apartments = apartmentService.getAll();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), apartments);
    }

    private void getSortedApartments(HttpServletResponse resp, String sortType) throws IOException {
        try {
            List<Apartment> apartments = apartmentService.getSorted(sortType);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), apartments);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
