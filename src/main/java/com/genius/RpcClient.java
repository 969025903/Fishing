package com.genius;

import com.genius.fishMan.FishMan;

public class RpcClient {
    public static void main(String[] args) {
        Object res = FishMan.goFishing("helloService/sayHello","Genius");
        System.out.println(res);
        Object sum = FishMan.goFishing("geniusService/add",1,2);
        System.out.println(sum);
    }
}
