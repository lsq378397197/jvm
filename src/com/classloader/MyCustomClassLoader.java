package com.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liushangqing
 * @date 2021-11-02
 * 自定义类加载器
 */
public class MyCustomClassLoader extends ClassLoader {
    /**
     * 重写父类方法,得到一個Class对象
     *
     * @param name 字节码文件名称
     * @return Class对象
     * @throws ClassNotFoundException 类未被发现
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        String classFilename = name + ".class";
        File classFile = new File(classFilename);
        if (classFile.exists()) {
            try (FileChannel fileChannel = new FileInputStream(classFile).getChannel()) {
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
                byte[] array = mappedByteBuffer.array();
                clazz = defineClass(name, array, 0, array.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MyCustomClassLoader myCustomClassLoader = new MyCustomClassLoader();
        Class<?> aClass = myCustomClassLoader.loadClass(args[0]);
        Method hello = aClass.getMethod("hello");
        hello.invoke(null, null);
    }
}
