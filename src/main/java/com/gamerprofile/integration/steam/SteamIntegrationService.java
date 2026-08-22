package com.gamerprofile.integration.steam;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SteamIntegrationService {

	private final SteamApiClient steamApiClient;

	public SteamIntegrationService(SteamApiClient steamApiClient) {
		this.steamApiClient = steamApiClient;
	}

	public SteamProfileResponse getPlayerSummary() {
		return steamApiClient.getPlayerSummary();
	}

	public List<SteamGamesResponse.SteamGame> getOwnedGames() {
		SteamGamesResponse response = steamApiClient.getOwnedGames();

		if (response == null || response.response() == null || response.response().games() == null) {
			return List.of();
		}

		return response.response().games();
	}
}
