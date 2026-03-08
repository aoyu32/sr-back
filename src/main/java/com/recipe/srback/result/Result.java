package com.recipe.srback.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 */
@Data
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {
    
    @Schema(description = "状态码：200-成功，其他-失败")
    private Integer code;
    
    @Schema(description = "返回消息")
    private String message;
    
    @Schema(description = "返回数据")
    private T data;
    
    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return result;
    }
    
    /**
     * 成功返回（带消息）
     */
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }
    
    /**
     * 成功返回（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }
    
    /**
     * 成功返回（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    /**
     * 失败返回（使用枚举）
     */
    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }
    
    /**
     * 失败返回（带消息）
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode());
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败返回（带状态码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
