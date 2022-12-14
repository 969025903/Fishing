package com.genius.virgin;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.genius.loadBalancer.LoadBalancer;
import com.genius.loadBalancer.RandomBalancer;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServerDiscovery {
    private final LoadBalancer loadBalancer;

    public NacosServerDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer == null ? new RandomBalancer() : loadBalancer;
    }

    /**
     * 根据服务名找到服务地址
     *
     * @param serviceName
     * @return
     */
    public InetSocketAddress getService(String serviceName) throws NacosException {
        List<Instance> instanceList = NacosUtils.getAllInstance(serviceName);
        if (instanceList.size() == 0) {
            throw new RuntimeException("找不到对应服务");
        }
        Instance instance = loadBalancer.getInstance(instanceList);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }
}
