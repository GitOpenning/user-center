package com.ttxs.usercenter.exception;

import com.ttxs.usercenter.common.BaseResponse;
import com.ttxs.usercenter.common.ErrorCode;
import com.ttxs.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
//在调用方法前后进行额外处理
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public <T> BaseResponse<T> businessExceptionHandler(BusinessException e){
        log.error("businessException: "+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public <T> BaseResponse<T> runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException: ",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
