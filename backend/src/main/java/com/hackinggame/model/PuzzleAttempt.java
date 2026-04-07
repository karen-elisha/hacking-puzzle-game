package main.java.com.hackinggame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "puzzle_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id")
    private UUID userId;
    
    private String puzzleType;
    private boolean solved;
    private int xpEarned;
    private LocalDateTime timestamp = LocalDateTime.now();
}
