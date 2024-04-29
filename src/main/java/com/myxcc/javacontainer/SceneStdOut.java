package com.myxcc.javacontainer;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/scene")
public class SceneStdOut {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "StdOut",method = RequestMethod.GET)
    public String StdOut(String[] args) {

        logger.info("============>开始模拟大量标准输出场景");
        int loopCount = 0;
        while (true) {
            loopCount++;
            logger.info("=>Hello, World!Hello, World!Hello, World!Hello, World!Hello, World!Hello, World!Hello, World!");
            logger.info("=>Loop count " + loopCount);
        }
    }

}
