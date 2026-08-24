package com.gamerprofile.service;

import com.gamerprofile.model.Game;
import com.gamerprofile.model.User;
import com.gamerprofile.model.UserSiteAchievement;
import com.gamerprofile.repository.AchievementRepository;
import com.gamerprofile.repository.GameRepository;
import com.gamerprofile.repository.PlatformConnectionRepository;
import com.gamerprofile.repository.UserSiteAchievementRepository;
import com.gamerprofile.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SiteAchievementService {

	public static final List<String> CODES = List.of(
			"FIRST_CONNECTION", "FIRST_SYNC", "COLLECTOR", "BIG_LIBRARY", "TROPHY_HUNTER",
			"VETERAN_HUNTER", "COMPLETE_GAME", "MULTI_PLATFORM", "EXPLORER", "MARATHONER",
			"VETERAN", "PERFECTIONIST", "COMPLETE_PROFILE");

	private final GameRepository gameRepository;
	private final AchievementRepository achievementRepository;
	private final PlatformConnectionRepository connectionRepository;
	private final UserSiteAchievementRepository siteAchievementRepository;
	private final UserRepository userRepository;

	public SiteAchievementService(GameRepository gameRepository, AchievementRepository achievementRepository,
			PlatformConnectionRepository connectionRepository, UserSiteAchievementRepository siteAchievementRepository,
			UserRepository userRepository) {
		this.gameRepository = gameRepository;
		this.achievementRepository = achievementRepository;
		this.connectionRepository = connectionRepository;
		this.siteAchievementRepository = siteAchievementRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void evaluate(User user) {
		user = userRepository.findById(user.getId()).orElseThrow();
		int alreadyEarnedXp = siteAchievementRepository.findAllByUserId(user.getId()).stream()
				.mapToInt(achievement -> xpFor(achievement.getAchievementCode())).sum();
		user.ensureExperience(alreadyEarnedXp);
		List<Game> games = gameRepository.findAllByUserId(user.getId());
		long totalAchievements = 0;
		long unlockedAchievements = 0;
		long completedGames = 0;
		long totalPlaytime = games.stream().mapToLong(game -> game.getPlaytimeMinutes() == null ? 0 : game.getPlaytimeMinutes()).sum();
		for (Game game : games) {
			var achievements = achievementRepository.findAllByGameId(game.getId());
			totalAchievements += achievements.size();
			unlockedAchievements += achievements.stream().filter(achievement -> achievement.isAchieved()).count();
			if (!achievements.isEmpty() && achievements.stream().allMatch(achievement -> achievement.isAchieved())) completedGames++;
		}
		long platforms = connectionRepository.findAllByUserId(user.getId()).stream().map(connection -> connection.getPlatform()).distinct().count();
		if (platforms >= 1) unlock(user, "FIRST_CONNECTION", 50);
		if (!games.isEmpty()) unlock(user, "FIRST_SYNC", 100);
		if (games.size() >= 10) unlock(user, "COLLECTOR", 250);
		if (games.size() >= 50) unlock(user, "BIG_LIBRARY", 500);
		if (unlockedAchievements >= 10) unlock(user, "TROPHY_HUNTER", 250);
		if (unlockedAchievements >= 100) unlock(user, "VETERAN_HUNTER", 1000);
		if (completedGames >= 1) unlock(user, "COMPLETE_GAME", 300);
		if (platforms >= 2) unlock(user, "MULTI_PLATFORM", 300);
		if (platforms >= 3) unlock(user, "EXPLORER", 750);
		if (totalPlaytime >= 6000) unlock(user, "MARATHONER", 500);
		if (totalPlaytime >= 30000) unlock(user, "VETERAN", 1500);
		if (completedGames >= 5) unlock(user, "PERFECTIONIST", 1000);
		if (user.getAvatarUrl() != null && platforms >= 1) unlock(user, "COMPLETE_PROFILE", 100);
		userRepository.save(user);
	}

	public List<String> unlockedCodes(Long userId) {
		return siteAchievementRepository.findAllByUserId(userId).stream().map(UserSiteAchievement::getAchievementCode).toList();
	}

	private void unlock(User user, String code, int xp) {
		if (siteAchievementRepository.findByUserIdAndAchievementCode(user.getId(), code).isEmpty()) {
			siteAchievementRepository.save(new UserSiteAchievement(user, code));
			user.addExperience(xp);
		}
	}

	private int xpFor(String code) {
		return switch (code) {
			case "FIRST_CONNECTION" -> 50;
			case "FIRST_SYNC" -> 100;
			case "COLLECTOR", "TROPHY_HUNTER" -> 250;
			case "BIG_LIBRARY", "MARATHONER" -> 500;
			case "COMPLETE_GAME", "MULTI_PLATFORM" -> 300;
			case "EXPLORER" -> 750;
			case "VETERAN_HUNTER", "PERFECTIONIST" -> 1000;
			case "VETERAN" -> 1500;
			case "COMPLETE_PROFILE" -> 100;
			default -> 0;
		};
	}
}
