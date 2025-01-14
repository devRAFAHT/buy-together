package com.github.devrafaht.userapi.domain.enums;

public enum CodeType {

    ACCOUNT_CREATION(0),
    PASSWORD_RESET(1);

    private int code;

    private CodeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
