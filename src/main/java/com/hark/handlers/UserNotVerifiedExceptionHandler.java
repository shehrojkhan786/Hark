package com.hark.handlers;

import com.hark.exceptions.UserNotVerifiedException;
import com.hark.model.User;
import com.hark.model.enums.ResponseStatus;
import com.hark.model.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserNotVerifiedExceptionHandler {

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<MessageResponse> userNotVerifiedException(UserNotVerifiedException userNotVerifiedException){
        System.out.println("User is not verified: "+userNotVerifiedException.getExceptionMessage());
        MessageResponse messageResponse = new MessageResponse(userNotVerifiedException.getExceptionMessage());
        messageResponse.setStatus(ResponseStatus.FAILED.name());
        return new ResponseEntity<MessageResponse>(messageResponse, HttpStatus.PRECONDITION_FAILED);
    }
}
