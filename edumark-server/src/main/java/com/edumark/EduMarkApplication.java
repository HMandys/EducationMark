package com.edumark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * EduMark 智能阅卷与家长查分平台
 * 主启动类
 *
 * @author EduMark
 */
@SpringBootApplication
@MapperScan("com.edumark.**.mapper")
@EnableAsync
@EnableScheduling
public class EduMarkApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduMarkApplication.class, args);
        System.out.println("====================================");
        System.out.println("  EduMark 智能阅卷平台启动成功!");
        System.out.println("  接口文档: http://localhost:8080/api/doc.html");
        System.out.println("====================================");
    }
}
