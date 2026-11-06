package org.example.short_code.pojo;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public static <T> Result<T> success(T data){
        Result <T> result = new Result<T>();
        result.setCode(200);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> error(int code,String msg){
        Result <T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
