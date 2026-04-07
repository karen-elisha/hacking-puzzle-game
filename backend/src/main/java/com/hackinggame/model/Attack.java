package com.hackinggame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attack {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "attacker_id")
    private UUID attackerId;
    
    @Column(name = "victim_id")
    private UUID victimId;
    
    private String puzzleType;
    private boolean success;
    private int stolenCoins;
    private int xpGained;
    private LocalDateTime timestamp = LocalDateTime.now();
}