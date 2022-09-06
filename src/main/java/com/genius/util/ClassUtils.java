package com.genius.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ClassUtils {

    public static Set<Class<?>> getClass(String packageName, Function<String,Boolean> function) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String packageDirName = packageName.replace('.', '/');
        String path = Thread.currentThread().getContextClassLoader().getResource(packageDirName).getPath().substring(1);
        Files.walkFileTree(Paths.get(path),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)throws IOException {
                String strPath = path.toString();
                int index = strPath.indexOf(packageName.replace('.', '\\'));
                String packUrl = strPath.substring(index);
                if(function.apply(packUrl)){
                    String className = packUrl.split("\\.")[0].replace('\\','.');
                    try {
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                    }catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }
                return super.visitFile(path, attrs);
            }
        });
        return classes;
    }

    /**
     * 获取所有.java和.class文件包
     * @param packageName
     * @return
     * @throws IOException
     */
    public static Set<Class<?>> getAllClass(String packageName) throws IOException {
        return getClass(packageName,(packUrl)->packUrl.endsWith(".class")|| packUrl.endsWith(".java"));
    }

    public static String getMainClass(){
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return stack[stack.length - 1].getClassName();
    }
}

