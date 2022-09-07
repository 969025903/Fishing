package com.genius.message;

import lombok.Data;

@Data
public class RequestRob extends Message {
    private String serviceName;
    private String method;
    private Class[] parameterTypes;
    private Object[] parameterValue;

    public RequestRob(int sequenceId,String serviceName,String method, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.serviceName = serviceName;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return REQUEST_FISH_TYPE;
    }
}
