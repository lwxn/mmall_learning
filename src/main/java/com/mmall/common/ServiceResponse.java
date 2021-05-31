package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

// 序列化时忽视null的对象，连key也会没有，不会出现key:null
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServiceResponse(int status){
        this.status = status;
    }

    private ServiceResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private ServiceResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServiceResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    // 序列化以后不会显示在json里面
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public String getMsg(){
        return msg;
    }

    public T getData(){
        return data;
    }

    public static <T> ServiceResponse<T> createBySuccess(){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    // 防止 T data和string 弄混
    public static <T> ServiceResponse<T> createBySuccessMsg(String msg){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServiceResponse<T> createBySuccess(T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServiceResponse<T> createBySuccess(String msg,T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServiceResponse<T> createByError(){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServiceResponse<T> createByErrorMsg(String errMsg){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),errMsg);
    }

    public static <T> ServiceResponse<T> createByErrorCodeMsg(int errcode,String errmsg){
        return new ServiceResponse<T>(errcode,errmsg);
    }
}