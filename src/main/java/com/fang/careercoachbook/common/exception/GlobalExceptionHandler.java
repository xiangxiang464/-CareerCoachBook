package com.fang.careercoachbook.common.exception;

import com.fang.careercoachbook.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 关键注解：表示这是一个全局处理类
public class GlobalExceptionHandler {

    /**
     * 专门处理我们自定义的“业务异常”
     * 比如：用户取消已经结束的课程
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        // 返回 error，把异常里的 message (例如"课程已开始") 传给前端
        return Result.error(e.getMessage());
    }
}