package com.ssafy.homesage.domain.user.exception;

public class LoginFailException extends Exception {
    public LoginFailException() {}
    public LoginFailException(String message) {
        super(message);
    }
}
