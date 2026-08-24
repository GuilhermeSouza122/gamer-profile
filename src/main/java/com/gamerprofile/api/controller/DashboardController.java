package com.gamerprofile.api.controller;

import com.gamerprofile.api.dto.DashboardSummaryDto;
import com.gamerprofile.repository.AchievementRepository;
import com.gamerprofile.repository.GameRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gamerprofile.service.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

	private final GameRepository gameRepository;
	private final AchievementRepository achievementRepository;
	private final CurrentUserService currentUserService;

	public DashboardController(GameRepository gameRepository, AchievementRepository achievementRepository, CurrentUserService currentUserService) {
		this.gameRepository = gameRepository;
		this.achievementRepository = achievementRepository;
		this.currentUserService = currentUserService;
	}

	@GetMapping("/summary")
	public DashboardSummaryDto summary(HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		var games = gameRepository.findAllByUserId(userId);
		var achievements = games.stream().flatMap(game -> achievementRepository.findAllByGameId(game.getId()).stream()).toList();
		long totalPlaytime = games.stream()
				.mapToLong(game -> game.getPlaytimeMinutes() == null ? 0 : game.getPlaytimeMinutes())
				.sum();
		long unlocked = achievements.stream().filter(achievement -> achievement.isAchieved()).count();
		double percentage = achievements.isEmpty() ? 0 : Math.round(unlocked * 10000.0 / achievements.size()) / 100.0;

		return new DashboardSummaryDto(games.size(), totalPlaytime, achievements.size(), unlocked, percentage);
	}
}
