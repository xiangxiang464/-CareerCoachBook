package com.fang.careercoachbook.common.exception;
// 继承 RuntimeException
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}