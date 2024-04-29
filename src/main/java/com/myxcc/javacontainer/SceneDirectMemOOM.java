package com.myxcc.javacontainer;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping(value = "/scene")
public class SceneDirectMemOOM {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 演示直接内存的溢出
     * 直接内存的容量可以通过-XX:MaxDirectMemorySize来设置（默认与堆内存最大值一样），与元空间是分开来管理的
     * VM args:-Xmx20M -XX:MaxDirectMemorySize=10M
     * 运行之后就会抛出OOM异常：java.lang.OutOfMemoryError: Direct buffer memory。
     * 注意：-XX:MaxDirectMemorySize只能限制通过DirectByteBuffer申请的内存，而其他堆外内存，如使用了Unsafe或者其他JNI手段直接直接申请的内存是无法限制的。
     */
    @RequestMapping(value = "DirectMemOOM", method = RequestMethod.GET)
    public String DirectMemOOM(String[] args) throws IllegalArgumentException, IllegalAccessException {
        logger.info("============>开始模拟直接内存OOM场景");

        int loopCount = 0;
        List<ByteBuffer> list = new LinkedList<>();
        while (true) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024 * 1024);
            list.add(byteBuffer);
            loopCount++;
            int size = list.size();
            logger.info("=>Loop count " + loopCount + "  List count " + size);

        }

    }

}

