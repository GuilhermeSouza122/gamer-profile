package com.gamerprofile.integration;

public record PlatformAchievement(
		String externalId,
		String name,
		String description,
		boolean achieved,
		Long unlockTime,
		String iconUrl,
		String iconGrayUrl
) {
}
