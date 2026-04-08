package com.hackinggame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private int level = 1;
    private int xp = 0;
    private int coins = 0;
    private int vaultLevel = 1;
    private int successfulHacks = 0;

    public Player(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public void addXp(int amount) {
        this.xp += amount;
        if (this.xp >= this.level * 500) {
            this.level++;
            this.coins += 100;
        }
    }
}
