package com.gamerprofile.api.dto;

public record DashboardSummaryDto(
		long totalGames,
		long totalPlaytimeMinutes,
		long totalAchievements,
		long unlockedAchievements,
		double completionPercentage
) {
}
