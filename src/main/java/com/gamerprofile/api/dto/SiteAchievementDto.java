package com.gamerprofile.api.dto;

import java.time.Instant;

public record SiteAchievementDto(
		String code,
		String name,
		String description,
		int xp,
		boolean unlocked,
		Instant unlockedAt
) {
}
