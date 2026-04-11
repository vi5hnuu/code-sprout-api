package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemOfDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ProblemOfDayRepository extends JpaRepository<ProblemOfDay, String> {
    Optional<ProblemOfDay> findByScheduledDate(LocalDate date);
}
