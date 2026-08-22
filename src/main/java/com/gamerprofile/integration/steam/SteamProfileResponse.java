package com.gamerprofile.integration.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamProfileResponse(SteamResponse response) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamResponse(List<SteamPlayer> players) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamPlayer(
			String steamid,
			Integer communityvisibilitystate,
			Integer profilestate,
			String personaname,
			String profileurl,
			String avatar,
			String avatarmedium,
			String avatarfull,
			Integer personastate,
			String realname,
			Long lastlogoff
	) {
	}
}
