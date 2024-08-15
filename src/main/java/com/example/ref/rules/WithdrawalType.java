package com.example.ref.rules;

import lombok.Getter;

@Getter
public enum WithdrawalType {

    WITHDRAWAL_TYPE("ROLE_WITHDRAWAL");

    private final String type;

    WithdrawalType(String type) {this.type = type;}

}
