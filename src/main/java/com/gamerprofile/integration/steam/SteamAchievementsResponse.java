package com.gamerprofile.integration.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamAchievementsResponse(SteamPlayerStats playerstats) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamPlayerStats(String steamID, String gameName, List<SteamAchievement> achievements) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamAchievement(
			String apiname,
			Integer achieved,
			Long unlocktime,
			String name,
			String description,
			String icon,
			String icongray
	) {
	}
}
