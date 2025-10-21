package com.milk.exception;

/**
 * 账号被锁定异常
 */
public class AccountExistException extends BaseException {

    public AccountExistException() {
    }

    public AccountExistException(String msg) {
        super(msg);
    }

}
