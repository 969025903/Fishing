package com.genius.service;

import com.genius.virgin.LetAlive;

@LetAlive(name = "helloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String msg) {
        System.out.println("Fish say:"+msg);
    }
}
