package com.gamerprofile.api.dto;

import com.gamerprofile.integration.steam.SteamGamesResponse;

public record SteamGameDto(
		Integer appId,
		String name,
		Integer playtimeMinutes,
		Integer playtimeLastTwoWeeksMinutes,
		Long lastPlayedEpochSeconds,
		String iconUrl,
		String logoUrl
) {

	public static SteamGameDto from(SteamGamesResponse.SteamGame game) {
		return new SteamGameDto(
				game.getAppid(),
				game.getName(),
				game.getPlaytimeForever(),
				game.getPlaytimeTwoWeeks(),
				game.getRtimeLastPlayed(),
				game.getImgIconUrl(),
				game.getImgLogoUrl());
	}
}
