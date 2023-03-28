package com.ttxs.usercenter.common;

public enum ErrorCode {

    SUCCESS(0,"ok","ok"),
    PARAMS_ERROR(40000,"请求参数错误","请求参数错误"),
    NULL_ERROR(40001,"请求数据为空","请求数据为空"),
    NOT_LOGIN(40100,"未登录","未登录"),
    NO_AUTH(40101,"无权限","无权限"),
    LENGTH_ERROR(40102,"参数长度不满足","参数长度不满足"),
    PASSWORD_CHECK(40103,"两次密码不匹配","两次密码不匹配"),
    EMAIL_ERROR(40104,"不是合法邮箱","不是合法邮箱"),
    ACCOUNT_ERROR(40105,"账户出现非法字符","账户出现非法字符"),
    NO_ACCOUNT(40107,"请重新核对账号密码","请重新核对账号密码"),
    REPEAT_ACCOUNT(40106,"该账户已被注册","该账户已被注册"),
    SYSTEM_ERROR(50000,"系统内部异常","系统内部异常");

    /**
     * 状态码
     */
    private final int code;


    private   final String message;
    private   final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
