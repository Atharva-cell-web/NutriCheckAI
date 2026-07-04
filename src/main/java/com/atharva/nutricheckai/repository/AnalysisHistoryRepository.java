package com.atharva.nutricheckai.repository;

import com.atharva.nutricheckai.entity.AnalysisHistory;
import com.atharva.nutricheckai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Long> {

    List<AnalysisHistory> findByUserOrderByAnalyzedAtDesc(User user);

}