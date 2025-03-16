package com.neyrad.bot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.neyrad.bot.BotApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            TelegramBot bot = ctx.getBean(TelegramBot.class);
            bot.start();
        };
    }
}