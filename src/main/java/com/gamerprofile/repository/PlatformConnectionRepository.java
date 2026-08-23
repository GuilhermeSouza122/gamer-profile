package com.gamerprofile.repository;

import com.gamerprofile.model.PlatformConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlatformConnectionRepository extends JpaRepository<PlatformConnection, Long> {

	List<PlatformConnection> findAllByUserId(Long userId);

	Optional<PlatformConnection> findByUserIdAndPlatform(Long userId, String platform);
}
