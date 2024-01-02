package com.example.facebookbackend.controllers;

import com.example.facebookbackend.dtos.response.MessageResponse;
import com.example.facebookbackend.exceptions.DataNotFoundException;
import com.example.facebookbackend.exceptions.InvalidDataException;
import com.example.facebookbackend.exceptions.NotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.cert.CertificateExpiredException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({InvalidDataException.class,
            IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MessageResponse handleInvalidDataException(RuntimeException ex) {
        MessageResponse errorResponse = new MessageResponse();
        errorResponse.setMessage(ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler({DataNotFoundException.class,
            UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public MessageResponse handleDataNotFoundException(RuntimeException ex) {
        MessageResponse errorResponse = new MessageResponse();
        errorResponse.setMessage(ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(NotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public MessageResponse handleNotAllowedException(RuntimeException ex){
        MessageResponse errorResponse = new MessageResponse();
        errorResponse.setMessage(ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(CertificateExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public MessageResponse handleCertificateExpiredException(RuntimeException ex){
        MessageResponse errorResponse = new MessageResponse();
        errorResponse.setMessage(ex.getMessage());
        return errorResponse;
    }
}
