package com.genius.virgin;

import com.genius.config.Config;
import com.genius.util.ClassUtils;
import com.genius.util.InetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FishCenter {

    private static String scanPath = Config.getScanPath();

    private static ConcurrentHashMap<Class<?>,Object> classMap = new ConcurrentHashMap<>();

    static{
        try {
            try {
                for (Class<?> clazz:getAllService()) {
                    classMap.put(clazz,clazz.getDeclaredConstructor().newInstance());
                    NacosServerRegistry.register(clazz.getAnnotation(LetAlive.class).name(), new InetSocketAddress(InetUtils.getLocalHostIP(),Config.serverPort()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }catch (InstantiationException  |NoSuchMethodException | IOException e){
            e.printStackTrace();
        }

    }

    public static Set<Class<?>> getAllService() throws IOException{
        Set<Class<?>> classSet = ClassUtils.getClass(scanPath, (packUrl) -> {
            if (packUrl.endsWith(".class") || packUrl.endsWith(".java")) {
                try {
                    String className = packUrl.split("\\.")[0].replace('\\', '.');
                    Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                    return aClass.isAnnotationPresent(LetAlive.class);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
        return classSet;
    }

    public static <T> T getFish(Class<?> clazz){
        return (T) classMap.get(clazz);
    }
}
