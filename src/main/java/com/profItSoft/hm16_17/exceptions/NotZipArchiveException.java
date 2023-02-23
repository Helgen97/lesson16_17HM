package com.profItSoft.hm16_17.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotZipArchiveException extends RuntimeException {

    public NotZipArchiveException(String message) {
        super(message);
    }
}
