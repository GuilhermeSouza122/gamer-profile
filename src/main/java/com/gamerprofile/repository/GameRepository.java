package com.gamerprofile.repository;

import com.gamerprofile.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

	Optional<Game> findByUserIdAndPlatformAndExternalId(Long userId, String platform, String externalId);

	List<Game> findAllByUserId(Long userId);
}
