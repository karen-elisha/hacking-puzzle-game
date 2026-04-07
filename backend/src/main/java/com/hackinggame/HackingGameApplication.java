package com.hackinggame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
    }
)
public class HackingGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackingGameApplication.class, args);

        System.out.println("""
        
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