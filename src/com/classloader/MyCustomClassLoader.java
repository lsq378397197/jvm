package com.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author liushangqing
 * @date 2021-11-02
 * 自定义类加载器
 */
public class MyCustomClassLoader extends ClassLoader {
    private String classPath;
    public MyCustomClassLoader( String classPath) {
        this.classPath = classPath;
    }

    private byte[] getClassByte(String name) throws IOException {
        String classFile = classPath + File.separator + name.replace(".", File.separator)+".class";
        System.out.println(classFile);
        File file = new File(classFile);
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int i;
        while (true) {
            i = fileChannel.read(byteBuffer);
            if (i == 0 || i == -1) {
                break;
            }
            byteBuffer.flip();
            writableByteChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        writableByteChannel.close();
        fileChannel.close();
        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

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
        String classFilename = classPath+File.separator + name.replace(".", File.separator)+".class";
        System.out.println(classFilename);
        File classFile = new File(classFilename);
        if (classFile.exists()) {
            try {
                byte[] bytes = getClassByte(name);
                clazz = defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        MyCustomClassLoader myCustomClassLoader = new MyCustomClassLoader("E:\\learn\\jvm-learn\\target");
        Class<?> aClass = myCustomClassLoader.findClass("com.classloader.Hello");
        Object object = aClass.newInstance();
        Method hello = aClass.getMethod("hello");
        hello.invoke(object);
    }
}
