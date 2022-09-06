package com.genius.message;

import lombok.Data;

@Data
public class ResponseFish extends Message{
    /**
     *  return value
     */
    private Object returnValue;

    /**
     *  Exception info
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RESPONSE_FISH_TYPE;
    }
}
