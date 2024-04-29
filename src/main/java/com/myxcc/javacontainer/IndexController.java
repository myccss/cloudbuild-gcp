package com.myxcc.javacontainer;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import io.micrometer.core.instrument.Counter;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
public class IndexController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private final Counter counter;


    public IndexController(MeterRegistry counter) {
        this.counter = counter.counter("my_index_counter");
    }


    @RequestMapping("/")
    public String index(ModelMap m) {
        counter.increment();
        logger.info("============>Host/IP:");
        try {
            InetAddress localHost = InetAddress.getLocalHost();
//            InetAddress HostServer = InetAddress.getByName("www.qq.com");  // 获取的是域名对应IP
            logger.info("Host name: " + localHost.getHostName());
            logger.info("IP address: " + localHost.getHostAddress());   // 未考虑多网卡多IP情况
            m.addAttribute("HostName", localHost.getHostName());
            m.addAttribute("HostAddrr", localHost.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        logger.info("============>DNS Info:");
        String resolvConfFilePath = "/etc/resolv.conf";
        try {
            Path path = Paths.get(resolvConfFilePath);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            logger.info("Contents of " + resolvConfFilePath + ":");
            for (String line : lines) {
                logger.info(line);
            }
            m.addAttribute("dnsInfo", lines);
        } catch (IOException e) {
            e.printStackTrace();
        }


        logger.info("============>Current Time:");
        LocalDateTime currentTime = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        ZoneOffset offset = zone.getRules().getOffset(java.time.Instant.now());
        logger.info("Current time: " + currentTime);
        logger.info("Current time zone: " + zone);
        logger.info("Current time zone offset: " + offset);
        m.addAttribute("currentTime", currentTime);
        m.addAttribute("TimeZone", zone);
        m.addAttribute("TimeOffset", offset);



        logger.info("============>Mem/CPU:");
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long usedMemory = totalMemory - freeMemory;
        logger.info("TotalMemory:" + totalMemory + "Byte, FreeMemory:" + freeMemory + "Byte, UsedMemory:" + usedMemory + "Byte");
        logger.info("Available processors: " + osMxBean.getAvailableProcessors());
        m.addAttribute("memTotal", totalMemory);
        m.addAttribute("freeMem", freeMemory);
        m.addAttribute("usedMem", usedMemory);
        m.addAttribute("core", osMxBean.getAvailableProcessors());


        logger.info("============>JVM arguments:");
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = runtimeMxBean.getInputArguments();
        m.addAttribute("jvmArg", jvmArgs);
        for (String arg : jvmArgs) {
            logger.info(arg);
        }

        logger.info("============>Environment variables:");
        Map<String, String> env = System.getenv();
        m.addAttribute("envSystem", env);
        for (Map.Entry<String, String> entry : env.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        return "index";
    }

}