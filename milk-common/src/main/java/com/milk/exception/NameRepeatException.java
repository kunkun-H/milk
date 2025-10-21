package com.milk.exception;

/**
 * 账号被锁定异常
 */
public class NameRepeatException extends BaseException {

    public NameRepeatException() {
    }

    public NameRepeatException(String msg) {
        super(msg);
    }

}
