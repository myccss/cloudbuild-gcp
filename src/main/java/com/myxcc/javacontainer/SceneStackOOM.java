package com.myxcc.javacontainer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(value = "/scene")
public class SceneStackOOM {
        private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
        private int length = 0;
        private void recursion() {
            length++;
            recursion();
        }

        /**
         * 演示栈的溢出
         * from https://zzuli-tech.github.io/interview/jvm/memory/OutOfMemoryError.html
         * VM Args: -Xss128k（减少栈的容量）
         * 关于虚拟机栈和本地方法栈，在Java虚拟机规范中描述了两种异常：
         *   如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError异常。
         *   如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出OutOfMemoryError异常异常。
         * 每个线程栈的大小，默认1M，我们可以调小JVM参数-Xss来模拟内存溢出。虚拟机参数-Xss在64位机器上默认的大小为1m，栈越大，能够容纳的栈帧就会越多，方法调用的深度就会越深。
         * 运行结果 java.lang.StackOverflowError
         */
        @RequestMapping(value = "StackOOM", method = RequestMethod.GET)
        public String StackOOM(String[] args) {
            logger.info("============>开始模拟栈OOM场景");
            SceneStackOOM sof = new SceneStackOOM();
            try {
                sof.recursion();
            } catch (Throwable e) {
                logger.info("=>Length  " + sof.length);
                e.printStackTrace();
            }
            return "StackOOM";

        }
    }
