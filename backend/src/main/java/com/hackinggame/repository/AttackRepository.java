package main.java.com.hackinggame.repository;

import com.hackinggame.model.Attack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttackRepository extends JpaRepository<Attack, UUID> {
    List<Attack> findByAttackerId(UUID attackerId);
    List<Attack> findByVictimId(UUID victimId);
    
    @Query("SELECT COUNT(a) FROM Attack a WHERE a.attackerId = :userId AND a.success = true")
    long countSuccessfulAttacks(UUID userId);
}