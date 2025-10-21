package com.milk.exception;

/**
 * 账号被锁定异常
 */
public class PhoneException extends BaseException {

    public PhoneException() {
    }

    public PhoneException(String msg) {
        super(msg);
    }

}
