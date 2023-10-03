//package com.andersenlab;
//
//import com.andersenlab.config.Config;
//import com.andersenlab.dao.onDiskImpl.OnDiskJsonHandler;
//import com.andersenlab.util.ConfigHandler;
//import com.andersenlab.factory.HotelFactory;
//import com.andersenlab.view.Console;
//
//public class AdministratorApp {
//    public static void main(String[] args) {
//        String configPath = args.length >= 1 ? args[0] : null;
//        var configData = ConfigHandler.createConfig(configPath);
//        Config config = new Config();
//        config.setConfigData(configData);
//        HotelFactory hotelFactory = new HotelFactory(config);
//        OnDiskJsonHandler onDiskJsonHandler = new OnDiskJsonHandler(hotelFactory);
//
//        onDiskJsonHandler.load();
//        new Console(hotelFactory).start();
//    }
//}