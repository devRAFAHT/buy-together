package com.github.devrafaht.userapi.domain.enums;

public enum Role {

    ROLE_CLIENT(0),
    ROLE_ADMINISTRATOR(1);

    private int code;

    private Role(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
