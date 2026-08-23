package com.gamerprofile.repository;

import com.gamerprofile.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

	List<Achievement> findAllByGameId(Long gameId);

	Optional<Achievement> findByGameIdAndExternalId(Long gameId, String externalId);
}
