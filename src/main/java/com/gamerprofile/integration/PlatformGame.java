package com.gamerprofile.integration;

public record PlatformGame(
		String externalId,
		String name,
		Integer playtimeMinutes,
		Long lastPlayedEpochSeconds,
		String imageUrl
) {
}
