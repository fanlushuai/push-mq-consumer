package com.auh.open.mq.consumer.vo;

public class Result<T> {

    String code;

    String msg;

    T data;

    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result ok() {
        return new Result("00000", "", null);
    }

    public static <E> Result<E> ok(E e) {
        return new Result("00000", "", e);
    }
}
