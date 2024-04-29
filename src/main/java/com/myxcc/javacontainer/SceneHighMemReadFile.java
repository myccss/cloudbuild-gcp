package com.myxcc.javacontainer;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
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
public class SceneHighMemReadFile {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    UUID uuid = generateUUID();


    private static UUID generateUUID() {
        return UUID.randomUUID();
    }

    public String genFilePath() {
        String filePath = "./file-" + generateUUID().toString() + ".txt" ;
        return filePath;
    }
    public void createFile(Path path) {
        long startTime = System.currentTimeMillis();
        logger.info("=> Start Create " + path + " 100MB File");
        long fileSizeInBytes = 100 * 1024 * 1024; // 100MB

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Random random = new Random();
            StringBuilder stringBuilder = new StringBuilder();

            while (stringBuilder.length() < fileSizeInBytes) {
                stringBuilder.append(randomString(1024)); // 1KB string length
            }

            byte[] bytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
            Files.write(path, bytes, StandardOpenOption.WRITE);
            long endTime = System.currentTimeMillis();
            long cosTime = endTime - startTime;

            logger.info("=> " + path + " 100MB File created successfully. Cost Time: " + cosTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            stringBuilder.append(chars.charAt(index));
        }

        return stringBuilder.toString();
    }

    @RequestMapping(value = "HighMemReadFile",method = RequestMethod.GET)
    public String HighMemReadFile(String[] args) {

        String filePath = genFilePath();
        Path path = Paths.get(filePath);

        createFile(path);
        logger.info("============>开始模拟循环读大文件到内存");
        try {
            Files.readAllLines(new File(filePath).toPath());
            logger.info("=>read BigFile complate " + path);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "HighMemReadFile";
    }

}