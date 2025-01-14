package com.github.devrafaht.userapi.domain.enums;

public enum Status {

    PENDING(0),
    ACTIVE(1),
    INACTIVE(2);

    private int code;

    private Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
