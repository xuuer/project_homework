package edu.fdzc.project.exception;

import edu.fdzc.project.common.Code;
import edu.fdzc.project.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理一般异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(Code.INTERNAL_SERVER_ERROR, "系统错误");
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(CustomException.class)
    public Result<Object> handleCustomException(CustomException e) {
        log.warn("业务异常：{}", e.getMsg());
        return Result.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理JWT相关的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();

        // 处理JWT相关异常
        if (message != null && (message.contains("Token无效") ||
                message.contains("未携带token") ||
                message.contains("已过期"))) {
            log.warn("认证异常：{}", message);
            return Result.error(Code.UNAUTHORIZED, "登录已过期，请重新登录");
        }

        // 其他运行时异常
        log.error("运行时异常：", e);
        return Result.error(Code.INTERNAL_SERVER_ERROR, "系统错误");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Object> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常：", e);

        // 特殊处理JWT相关的空指针异常
        if (e.getStackTrace().length > 0 &&
                e.getStackTrace()[0].getClassName().contains("JwtUtil")) {
            return Result.error(Code.UNAUTHORIZED, "登录已过期，请重新登录");
        }

        return Result.error(Code.INTERNAL_SERVER_ERROR, "系统内部错误");
    }
}