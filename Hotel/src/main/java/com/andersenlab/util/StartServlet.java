package com.andersenlab.util;

import com.andersenlab.servlet.apartment.ApartmentByIdServlet;
import com.andersenlab.servlet.apartment.ApartmentChangeStatusByIdServlet;
import com.andersenlab.servlet.apartment.ApartmentsServlet;
import com.andersenlab.servlet.client.*;
import com.andersenlab.servlet.perk.PerkByIdServlet;
import com.andersenlab.servlet.perk.PerksServlet;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class StartServlet {
    public static final String WEBAPP_DIR = "src/main/webapp/";
    public static final Integer PORT = 8080;
    public static final String TARGET_CLASSES = "target/tomcat";
    public static final String CONTEXT_PATH = "/";


    public static Tomcat getTomcat(){
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(PORT);
        tomcat.setConnector(connector);
        tomcat.setBaseDir(new File(TARGET_CLASSES).getAbsolutePath());
        Context context = tomcat.addWebapp(CONTEXT_PATH, new File(WEBAPP_DIR).getAbsolutePath());
        addServletsToTomcat(tomcat, context);
        return tomcat;
    }


    private static void addServletsToTomcat(Tomcat tomcat, Context context) {
        tomcat.addServlet("", "PerksServlet", new PerksServlet());
        context.addServletMappingDecoded("/perks", "PerksServlet");

        tomcat.addServlet("", "PerkByIdServlet", new PerkByIdServlet());
        context.addServletMappingDecoded("/perks/id", "PerkByIdServlet");

        tomcat.addServlet("", "ApartmentsServlet", new ApartmentsServlet());
        context.addServletMappingDecoded("/apartments", "ApartmentsServlet");

        tomcat.addServlet("", "ApartmentByIdServlet", new ApartmentByIdServlet());
        context.addServletMappingDecoded("/apartments/id", "ApartmentByIdServlet");

        tomcat.addServlet("", "ApartmentChangeStatusByIdServlet", new ApartmentChangeStatusByIdServlet());
        context.addServletMappingDecoded("/apartments/change-status/id", "ApartmentChangeStatusByIdServlet");

        tomcat.addServlet("", "ClientsServlet", new ClientsServlet());
        context.addServletMappingDecoded("/clients", "ClientsServlet");

        tomcat.addServlet("", "ClientByIdServlet", new ClientByIdServlet());
        context.addServletMappingDecoded("/clients/id", "ClientByIdServlet");

        tomcat.addServlet("", "ClientCheckInServlet", new ClientCheckInServlet());
        context.addServletMappingDecoded("/clients/checkin", "ClientCheckInServlet");

        tomcat.addServlet("", "ClientCheckOutServlet", new ClientCheckOutServlet());
        context.addServletMappingDecoded("/clients/checkout", "ClientCheckOutServlet");

        tomcat.addServlet("", "ClientPerksServlet", new ClientPerksServlet());
        context.addServletMappingDecoded("/clients/perks", "ClientPerksServlet");

        tomcat.addServlet("", "ClientStayCostByIdServlet", new ClientStayCostByIdServlet());
        context.addServletMappingDecoded("/clients/stay-cost/id", "ClientStayCostByIdServlet");
    }

}
