package com.genius.config;

import com.genius.protocol.Serializer;
import com.genius.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public abstract class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")){
             properties = new Properties();
             properties.load(in);
        }catch (IOException e){
            throw new ExceptionInInitializerError(e);
        }
    }

    public static byte getMessageVersion(){
        String value = properties.getProperty("message.version");
        return (byte) Integer.parseInt(Optional.ofNullable(value).orElse("1"));
    }

    public static Serializer.Algorithm getSerializer(){
        String property = properties.getProperty("message.serializer");
        return Serializer.Algorithm.valueOf(Optional.ofNullable(property).orElse("Json"));
    }

    public static int serverPort(){
        String property = properties.getProperty("server.port");
        return Integer.parseInt(Optional.ofNullable(property).orElse("8686"));
    }

    public static String getNacosUrl(){
        String property = properties.getProperty("nacos.server.url");
        return Optional.ofNullable(property).orElse("127.0.0.1:8848");
    }

    public static String getScanPath(){
        String scanPath = properties.getProperty("server.scan.package");
        return Optional.ofNullable(scanPath).orElse(ClassUtils.getMainClass());
    }

}
