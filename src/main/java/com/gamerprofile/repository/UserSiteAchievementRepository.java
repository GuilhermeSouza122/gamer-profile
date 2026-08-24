package com.gamerprofile.repository;

import com.gamerprofile.model.UserSiteAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSiteAchievementRepository extends JpaRepository<UserSiteAchievement, Long> {

	List<UserSiteAchievement> findAllByUserId(Long userId);

	Optional<UserSiteAchievement> findByUserIdAndAchievementCode(Long userId, String achievementCode);
}
