package main.java.com.hackinggame.repository;

import com.hackinggame.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u ORDER BY u.xp DESC")
    List<User> findTop10ByOrderByXpDesc();
    
    @Query("SELECT u FROM User u WHERE u.id != :userId ORDER BY u.xp DESC")
    List<User> findOtherUsers(UUID userId);
}