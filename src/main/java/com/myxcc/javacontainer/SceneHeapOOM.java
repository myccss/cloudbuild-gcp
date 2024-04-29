package com.myxcc.javacontainer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/scene")
public class SceneHeapOOM {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 演示堆的溢出
     * from https://blog.csdn.net/u022812849/article/details/107537021
     * VM args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=c:\dump\heap.hprof -XX:+PrintGCDetails -XX:+PrintGCTimeStamps
     * 可以使用JVM参数限制堆内存大小，并在达到该限制后触发OOM。例如，使用-Xmx参数设置最大堆内存为较小的值（如128m）
     * 当程序尝试分配超过128MB的内存时，将抛出OutOfMemoryError
     * 运行结果 java.lang.OutOfMemoryError: Java heap space
     */
    @RequestMapping(value = "HeapOOM",method = RequestMethod.GET)
    public String heapOOM(String[] args) {

        logger.info("============>开始模拟堆OOM场景");
        List<byte[]> list = new ArrayList<>();
        int loopCount = 0;
        while (true) {
            list.add(new byte[1024 * 1024]); // 每次增加一个1M大小的数组对象
            loopCount++;
            int size = list.size();
            logger.info("=>Loop count " + loopCount + "  List count " + size);

        }


    }

}