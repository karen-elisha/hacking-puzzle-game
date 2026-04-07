package com.hackinggame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.hackinggame.model")
@EnableJpaRepositories("com.hackinggame.repository")
public class HackingGameApplication {
    public static void main(String[] args) {
        SpringApplication.run(HackingGameApplication.class, args);
        System.out.println("""
        \n
        ██╗  ██╗ █████╗  ██████╗██╗  ██╗██╗███╗   ██╗ ██████╗ 
        ██║  ██║██╔══██╗██╔════╝██║ ██╔╝██║████╗  ██║██╔════╝ 
        ███████║███████║██║     █████╔╝ ██║██╔██╗ ██║██║  ███╗
        ██╔══██║██╔══██║██║     ██╔═██╗ ██║██║╚██╗██║██║   ██║
        ██║  ██║██║  ██║╚██████╗██║  ██╗██║██║ ╚████║╚██████╔╝
        ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝ ╚═════╝ 
        🔐 Hacking Puzzle Game Started Successfully! 🎮
        """);
    }
}

@SpringBootApplication(
    exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
    }
)