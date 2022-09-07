package com.genius;

import com.genius.pool.FishPool;

public class RpcServer {
    public static void main(String[] args) {
        new FishPool().start();
    }
}
