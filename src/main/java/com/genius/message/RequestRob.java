package com.genius.message;

import lombok.Data;

@Data
public class RequestRob extends Message {
    private String serviceName;
    private String method;
    private Class<?> returnType;
    private Class[] parameterTypes;
    private Object[] parameterValue;

    public RequestRob(String serviceName,String method, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        this.serviceName = serviceName;
        this.method = method;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return REQUEST_FISH_TYPE;
    }
}
