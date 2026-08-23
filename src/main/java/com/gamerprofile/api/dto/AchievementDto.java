package com.gamerprofile.api.dto;

import com.gamerprofile.model.Achievement;

public record AchievementDto(
		Long id,
		String externalId,
		String name,
		String description,
		boolean achieved,
		Long unlockTime,
		String iconUrl,
		String iconGrayUrl
) {

	public static AchievementDto from(Achievement achievement) {
		return new AchievementDto(achievement.getId(), achievement.getExternalId(), achievement.getName(),
				achievement.getDescription(), achievement.isAchieved(), achievement.getUnlockTime(),
				achievement.getIconUrl(), achievement.getIconGrayUrl());
	}
}
