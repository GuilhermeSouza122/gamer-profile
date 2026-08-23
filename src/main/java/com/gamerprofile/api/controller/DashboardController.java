package com.gamerprofile.api.controller;

import com.gamerprofile.api.dto.DashboardSummaryDto;
import com.gamerprofile.repository.AchievementRepository;
import com.gamerprofile.repository.GameRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

	private final GameRepository gameRepository;
	private final AchievementRepository achievementRepository;

	public DashboardController(GameRepository gameRepository, AchievementRepository achievementRepository) {
		this.gameRepository = gameRepository;
		this.achievementRepository = achievementRepository;
	}

	@GetMapping("/summary")
	public DashboardSummaryDto summary() {
		var games = gameRepository.findAll();
		var achievements = achievementRepository.findAll();
		long totalPlaytime = games.stream()
				.mapToLong(game -> game.getPlaytimeMinutes() == null ? 0 : game.getPlaytimeMinutes())
				.sum();
		long unlocked = achievements.stream().filter(achievement -> achievement.isAchieved()).count();
		double percentage = achievements.isEmpty() ? 0 : Math.round(unlocked * 10000.0 / achievements.size()) / 100.0;

		return new DashboardSummaryDto(games.size(), totalPlaytime, achievements.size(), unlocked, percentage);
	}
}
