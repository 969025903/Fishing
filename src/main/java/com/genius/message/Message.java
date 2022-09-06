package com.genius.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message {

    public static Class<? extends Message> getMessageClass(Byte messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;
    private int messageType;

    public static final byte RESPONSE_FISH_TYPE = 10;
    public static final byte REQUEST_FISH_TYPE = 11;

    private static final Map<Byte, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        messageClasses.put( RESPONSE_FISH_TYPE, ResponseFish.class);
        messageClasses.put( REQUEST_FISH_TYPE, RequestRob.class);
    }
}
