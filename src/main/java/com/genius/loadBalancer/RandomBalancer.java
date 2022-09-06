package com.genius.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomBalancer implements LoadBalancer {
    private final Random random=new Random();
    @Override
    public Instance getInstance(List<Instance> list) {
        return list.get(random.nextInt(list.size()));

    }
}
