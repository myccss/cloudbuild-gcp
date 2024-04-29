package com.myxcc.javacontainer;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(value = "/scene")
public class SceneMetaSpaceOOM {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 演示元空间的溢出
     * 方法区中存放的是类的数据结构，只要不断往方法区中加入新的类，就会产生方法区的溢出，可以使用类加载器不断加载类或者动态代理不断生成类来演示。
     * from https://blog.csdn.net/u022812849/article/details/107537021
     * VM args：-XX:MetaspaceSize=16m -XX:MaxMetaspaceSize=16m
     * 运行结果 java.lang.OutOfMemoryError: Metaspace
     */

    @RequestMapping(value = "MetaOOM", method = RequestMethod.GET)
    public String MetaOOM(String[] args) {

        logger.info("============>开始模拟MetaSpace OOM场景");
        List<ClassLoader> classLoaderList = new ArrayList<>();
        while (true) {
            ClassLoader loader = new URLClassLoader(new URL[]{});
            Facade t = (Facade) Proxy.newProxyInstance(loader, new Class<?>[]{Facade.class}, new MetaspaceFacadeInvocationHandler(new FacadeImpl()));
            classLoaderList.add(loader);
        }
    }

    public interface Facade {
    }

    public static class FacadeImpl implements Facade {
    }

    public static class MetaspaceFacadeInvocationHandler implements InvocationHandler {
        private Object impl;

        public MetaspaceFacadeInvocationHandler(Object impl) {
            this.impl = impl;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(impl, args);
        }
    }
}


