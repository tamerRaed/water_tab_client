package com.tamer.alna99.watertabclient.model;

public class Result<T> {

    public final Status status;
    public T data;

    public Result(Status status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.SUCCESS, data);
    }

    public static <T> Result<T> error(T data) {
        return new Result<>(Status.ERROR, data);
    }

    public enum Status {SUCCESS, ERROR}
}
