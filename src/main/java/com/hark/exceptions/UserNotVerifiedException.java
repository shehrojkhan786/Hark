package com.hark.exceptions;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserNotVerifiedException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private String exceptionMessage;
}
