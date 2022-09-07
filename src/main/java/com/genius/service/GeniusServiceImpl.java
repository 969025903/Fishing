package com.genius.service;

import com.genius.virgin.LetAlive;

@LetAlive(name = "geniusService")
public class GeniusServiceImpl implements GeniusService {
    @Override
    public int add(Integer a, Integer b) {
        return a+b;
    }
}
