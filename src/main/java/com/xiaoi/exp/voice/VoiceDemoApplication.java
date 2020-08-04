package com.xiaoi.exp.voice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.xiaoi.exp.voice.mapper")
@SpringBootApplication
public class VoiceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoiceDemoApplication.class, args);
    }

}
