package com.genius.virgin;

import com.genius.config.Config;
import com.genius.util.ClassUtils;
import com.genius.util.InetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class FishCenter {

    private static String scanPath = Config.getScanPath();

    /**
     * 本地注册表
     */
    private static ConcurrentHashMap<String,Object> classMap = new ConcurrentHashMap<>();


    /**
     * 开始注册并将注解上的名字上传到Nacos,与本地注册表中
     */
    public static void start(){
        try {
            try {
                for (Class<?> clazz:getAllService()) {
                    String serviceName = clazz.getAnnotation(LetAlive.class).name();
                    classMap.put(serviceName,clazz.getDeclaredConstructor().newInstance());
                    NacosServerRegistry.register(serviceName, new InetSocketAddress(InetUtils.getLocalHostIP(),Config.serverPort()));
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

    /**
     * 获取所有被注解的注册服务
     * @return
     * @throws IOException
     */
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

    public static <T> T getFish(String clazz){
        return (T) classMap.get(clazz);
    }
}
