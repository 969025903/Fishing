package com.genius.virgin;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;

public class NacosServerRegistry {
    public static void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerServer(serviceName,inetSocketAddress);
            System.out.println("注册"+serviceName);
        } catch (NacosException e) {
            throw new RuntimeException("注册Nacos出现异常");
        }
    }

    /**
     * 根据服务名获取地址
     * @param serviceName
     * @return
     */
    public static InetSocketAddress getService(String serviceName) {
        return null;
    }
}
