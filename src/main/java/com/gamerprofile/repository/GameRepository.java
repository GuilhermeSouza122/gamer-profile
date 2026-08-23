package com.gamerprofile.repository;

import com.gamerprofile.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

	Optional<Game> findByPlatformAndExternalId(String platform, String externalId);
}
