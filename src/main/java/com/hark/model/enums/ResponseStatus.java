package com.hark.model.enums;

public enum ResponseStatus {
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    ERROR("ERROR");

    String messageType = null;
    ResponseStatus(String message) {
        this.messageType = message;
    }
}