package com.demo.welfaring.repository;

import com.demo.welfaring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    
    boolean existsByEmail(String email);
}
