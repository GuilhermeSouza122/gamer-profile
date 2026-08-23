package com.gamerprofile.api.dto;

import com.gamerprofile.model.Game;

public record GameDto(
		Long id,
		String platform,
		String externalId,
		String name,
		Integer playtimeMinutes,
		Long lastPlayedEpochSeconds,
		String imageUrl
) {

	public static GameDto from(Game game) {
		return new GameDto(game.getId(), game.getPlatform(), game.getExternalId(), game.getName(),
				game.getPlaytimeMinutes(), game.getLastPlayedEpochSeconds(), game.getImageUrl());
	}
}
