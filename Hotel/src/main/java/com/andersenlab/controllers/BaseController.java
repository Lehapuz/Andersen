package com.andersenlab.controllers;

import com.andersenlab.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BaseController {

    @ExceptionHandler({IdDoesNotExistException.class})
    public ResponseEntity handleIdDoesNotExistException(IdDoesNotExistException e) {
        return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler({
            ClientAlreadyCheckedInException.class,
            ClientIsNotCheckedInException.class,
            NoAvailableApartmentsException.class
    })
    public ResponseEntity handleInnerLogicException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler({ConfigurationRestrictionException.class})
    public ResponseEntity handleConfigurationRestrictionException(ConfigurationRestrictionException e) {
        return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(403));
    }

    @ExceptionHandler({JsonProcessingException.class})
    public ResponseEntity handleJsonProcessingException(JsonProcessingException e) {
        return new ResponseEntity("Json formatting error", HttpStatusCode.valueOf(422 ));
    }
}
