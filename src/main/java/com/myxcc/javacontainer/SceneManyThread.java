package com.myxcc.javacontainer;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/scene")
public class SceneManyThread {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "ManyThread",method = RequestMethod.GET)
    public String ManyThread(String[] args) throws InterruptedException {

        logger.info("============>开始模拟创建大量线程场景");
        int loopCount = 0;
        while (true) {
            Thread thread = new Thread(() -> {
                // 执行你的任务代码
                Thread curThread = Thread.currentThread();
                logger.info("Current thread ID: " + curThread.getId());

                try {
                    Thread.sleep(600000); // 线程睡眠600秒钟
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            loopCount++;
            logger.info("=>Loop count " + loopCount);
            Thread.sleep(1);

        }
    }

}