package com.myxcc.javacontainer;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.UUID;


@Controller
@RequestMapping(value = "/scene")
public class SceneBigFile {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    UUID uuid = generateUUID();
    String filePath = "./Bigfile-" + generateUUID().toString() + ".txt" ;
    Path path = Paths.get(filePath);

    private static UUID generateUUID() {
        return UUID.randomUUID();
    }

    private static String generateRandomContent(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            stringBuilder.append(chars.charAt(index));
        }

        return stringBuilder.toString();
    }


    @RequestMapping(value = "BigFile",method = RequestMethod.GET)
    public String BigFile(String[] args) {
        try {

            try {
                if (!Files.exists(path)) {
                    Files.createFile(path);
                    logger.info(path + "  File created successfully!");
                } else {
                    logger.info(path + "  File already exists!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            logger.info("============>开始模拟创建大文件场景");
            int loopCount = 0;
            while (true) {
                String content = generateRandomContent(100 * 1024 * 1024); // 100MB string length
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                Files.write(path, bytes, StandardOpenOption.APPEND);
                loopCount++;
                logger.info("=>Loop " + path + " count " + loopCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "BigFile";
    }

}
