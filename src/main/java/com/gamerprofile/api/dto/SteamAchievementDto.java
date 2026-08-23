package com.gamerprofile.api.dto;

import com.gamerprofile.integration.steam.SteamAchievementSchemaResponse;
import com.gamerprofile.integration.steam.SteamAchievementsResponse;

public record SteamAchievementDto(
		String apiName,
		String name,
		String description,
		boolean achieved,
		Long unlockTime,
		String icon,
		String iconGray
) {

	public static SteamAchievementDto from(
			SteamAchievementsResponse.SteamAchievement progress,
			SteamAchievementSchemaResponse.SteamAchievementDefinition definition) {
		return new SteamAchievementDto(
				progress.apiname(),
				definition == null ? null : definition.displayName(),
				definition == null ? null : definition.description(),
				progress.achieved() != null && progress.achieved() == 1,
				progress.unlocktime() == null || progress.unlocktime() == 0 ? null : progress.unlocktime(),
				definition == null ? null : definition.icon(),
				definition == null ? null : definition.icongray());
	}
}
