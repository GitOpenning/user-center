package com.ttxs.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 4704903035120121798L;
    private String userAccount;
    private String userPassword;
}
