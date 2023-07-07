package com.example.dinner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;
import java.util.UUID;

@SpringBootTest
class DinnerApplicationTests {

    @Value("${dish.path}")
    private String basePath;

    @Test
    void contextLoads() {
        System.out.println(UUID.randomUUID());
    }

//    @Test
//    void zkx() throws IOException {
//        FileInputStream fis = new FileInputStream("D:\\Dish\\kun.png");
//        BufferedInputStream bis = new BufferedInputStream(fis);
//
//        FileOutputStream fos = new FileOutputStream("D:\\Dish\\777.png");
//        BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = bis.read(buffer)) > 0) {
//            bos.write(buffer, 0, length);
//        }
//        bos.close();
//        fos.close();
//        bis.close();
//        fis.close();
//    }


}
