package com.gamerprofile.integration.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamAchievementSchemaResponse(SteamSchemaGame game) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamSchemaGame(String gameName, SteamAvailableGameStats availableGameStats) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamAvailableGameStats(List<SteamAchievementDefinition> achievements) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamAchievementDefinition(
			String name,
			String displayName,
			String description,
			String icon,
			String icongray
	) {
	}
}
