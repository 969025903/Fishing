package com.genius.service;

import com.genius.virgin.LetAlive;

@LetAlive(name = "helloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String msg) {
        return "Fish say:"+msg;
    }
}
