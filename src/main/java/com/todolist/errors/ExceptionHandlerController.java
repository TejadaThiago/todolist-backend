package com.todolist.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//definir classes globais para tratamento de execoes do spring
@ControllerAdvice
public class ExceptionHandlerController {

    //define o tipo de execao que o metodo trata
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){

    //        return ResponseEntity.status(400).body(e.getMessage());
        return ResponseEntity.status(400).body(e.getMostSpecificCause().getMessage());
    }
}
