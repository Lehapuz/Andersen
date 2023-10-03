package com.andersenlab.dao.onDiskImpl;

import com.andersenlab.factory.HotelFactory;
import com.andersenlab.util.IdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OnDiskJsonHandler {
    private final String pathJson;

    public OnDiskJsonHandler(HotelFactory hotelFactory) {
        pathJson = hotelFactory.getConfig().getConfigData().getDatabase().getPath();
    }

    public void save(StateEntity stateEntity) {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(pathJson), stateEntity);

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with outgoing files");
        }
        IdGenerator.setGenerateId(stateEntity.getClientsList().size(), stateEntity.getApartmentsList().size(),
                stateEntity.getPerksList().size());
    }

    public StateEntity load() {
        StateEntity stateEntity = new StateEntity();
        if (checkIfExistsJson()) {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            try {
                var reader = Files.newBufferedReader(Path.of(pathJson));
                stateEntity = objectMapper.readValue(reader, StateEntity.class);
            } catch (IOException e) {
                throw new RuntimeException("There is a problem with incoming files");
            }
            IdGenerator.setGenerateId(stateEntity.getClientsList().size(), stateEntity.getApartmentsList().size(),
                    stateEntity.getPerksList().size());

            return stateEntity;
        }
        return stateEntity;
    }

    public boolean checkIfExistsJson() {
        return new File(pathJson).exists();
    }
}
