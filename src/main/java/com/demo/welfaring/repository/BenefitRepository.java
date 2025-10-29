package com.demo.welfaring.repository;

import com.demo.welfaring.domain.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    
    // 모든 혜택 조회 (조건 평가는 애플리케이션에서)
    @Query("SELECT b FROM Benefit b")
    List<Benefit> findAllBenefits();
    
    // 카테고리별 혜택 조회
    @Query("SELECT b FROM Benefit b WHERE b.category = :category")
    List<Benefit> findByCategory(String category);
    
    // 혜택명으로 검색
    @Query("SELECT b FROM Benefit b WHERE b.benefitName LIKE %:keyword%")
    List<Benefit> findByBenefitNameContaining(String keyword);
}
