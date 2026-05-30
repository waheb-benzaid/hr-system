package com.hr_system.leave.exception;

public class InvalidLeaveRequestException extends RuntimeException {

    public InvalidLeaveRequestException(String message) {
        super(message);
    }
}
