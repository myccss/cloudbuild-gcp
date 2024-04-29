package com.myxcc.javacontainer;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.misc.Unsafe;

import java.lang.reflect.Field;


@Controller
@RequestMapping(value = "/scene")
public class SceneLocalMemOOM {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 演示本地内存的溢出
     * VM args: -Xmx20M -XX:MaxDirectMemorySize=10M
     * 运行之后就会抛出OOM异常：java.lang.OutOfMemoryError。
     * 注意：通过top命令去观察的，看它的RES列的数值；反之，如果使用jmap命令去看内存占用，得到的只是堆的大小，只能看到一小块可怜的空间
     */
    @RequestMapping(value = "LocalMemOOM", method = RequestMethod.GET)
    public String LocalMemOOM(String[] args) throws IllegalArgumentException, IllegalAccessException {
        logger.info("============>开始模拟本地内存OOM场景");
        Field field = Unsafe.class.getDeclaredFields()[0];
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        int loopCount = 0;
        while (true) {
            unsafe.allocateMemory(1024 * 1024);
            loopCount++;
            logger.info("=>Loop count " + loopCount);
        }

    }

}
