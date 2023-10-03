package com.andersenlab.servlet.apartment;

import com.andersenlab.entity.Apartment;
import com.andersenlab.exceptions.IdDoesNotExistException;
import com.andersenlab.service.ApartmentService;
import com.andersenlab.factory.ServletFactory;
import com.andersenlab.util.ServletUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ApartmentByIdServlet",
        urlPatterns = {"/apartments/id"}
)
public class ApartmentByIdServlet extends HttpServlet {
    private ApartmentService apartmentService = ServletFactory.INSTANCE.getApartmentService();
    private ObjectMapper objectMapper = new ObjectMapper();

    //EXAMPLE: http://localhost:8080/apartments/id?id=3 for getById()
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        try {
            Apartment apartment = apartmentService.getById(Long.parseLong(id));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), apartment);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //EXAMPLE: http://localhost:8080/apartments/id?id=3 for update()
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Apartment apartment = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Apartment.class);
        String id = req.getParameter("id");
        try {
            apartment.setId(Long.parseLong(id));
            Apartment updatedApartment = apartmentService.update(apartment);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), updatedApartment);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //EXAMPLE: http://localhost:8080/apartments/id?id=3 for changePrice()
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Apartment apartment = objectMapper.readValue(ServletUtils.readBody(req.getReader()), Apartment.class);
        String id = req.getParameter("id");
        try {
            apartment.setId(Long.parseLong(id));
            Apartment changedPriceApartment = apartmentService.changePrice(apartment.getId(), apartment.getPrice());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), changedPriceApartment);
        } catch (IdDoesNotExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

