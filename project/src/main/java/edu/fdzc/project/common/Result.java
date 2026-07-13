package edu.fdzc.project.common;

import lombok.AllArgsConstructor;
import lombok.Data;
//import org.zc.net.exception.CustomException;

import java.io.Serializable;

/**
 * 响应结果封装类
 */
@Data
@AllArgsConstructor
public class Result<T> implements Serializable {

    private Integer code;

    private String msg;

    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(Code.OK, "", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(Code.OK, msg, data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

}