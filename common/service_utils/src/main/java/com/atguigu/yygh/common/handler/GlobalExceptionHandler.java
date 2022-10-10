package com.atguigu.yygh.common.handler;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月15日 14:27
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e){
        e.printStackTrace();    //输出日志文件
        return R.error().message(e.getMessage());
    }

    //自定义异常
    @ExceptionHandler(value = YyghException.class )//细粒度的异常处理
    public R handleYyghException(YyghException ex){
        ex.printStackTrace();//输出异常：日志文件
        log.error(ex.getMessage());
        return R.error().message(ex.getMessage()).code(ex.getCode());
    }

}
