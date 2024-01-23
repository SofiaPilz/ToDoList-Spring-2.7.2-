package com.sofiapilz.todosimple.services.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DataBindingViolationExceptions extends DataIntegrityViolationException {

    public DataBindingViolationExceptions(String message) {
        super(message);
    }


}
