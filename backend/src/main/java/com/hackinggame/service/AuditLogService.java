package com.hackinggame.service;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private static final String LOG_FILE = "server_audit.txt";

    public void logSuccessfulHack(String username, int reward, String puzzleType) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(LocalDateTime.now() + " | SUCCESS | User: " + username + " | Reward: " + reward + " XP | Puzzle: " + puzzleType);
        } catch (IOException e) {
            System.err.println("Exception Handling (File I/O Error): Failed to write audit log! " + e.getMessage());
        }
    }
    
    public void logSystemEvent(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(LocalDateTime.now() + " | SYSTEM | " + message);
        } catch (IOException e) {
            System.err.println("Exception Handling (File I/O Error): Failed to write system log! " + e.getMessage());
        }
    }
}
