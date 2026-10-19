package com.gexiayouzha.mijvshopping.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> Result<T> success(int code, String message, T data) {
        return new Result(code, message, data);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result(code, message, null);
    }
}
