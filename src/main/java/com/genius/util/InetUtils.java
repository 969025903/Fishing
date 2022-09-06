package com.genius.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetUtils {
    public static String getLocalHostIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
