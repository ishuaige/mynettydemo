package com.netty.mynettydemo;

import com.netty.mynettydemo.chat.ChatWebSocketServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author niumazlb
 */
@SpringBootApplication
public class MynettydemoApplication implements CommandLineRunner {
    @Resource
    ChatWebSocketServer chatWebSocketServer;

    public static void main(String[] args) {
        SpringApplication.run(MynettydemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //启动聊天室服务 打开/templates/index.html
        chatWebSocketServer.run();
    }
}
