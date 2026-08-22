package com.gamerprofile.integration.steam;

import org.springframework.stereotype.Service;

@Service
public class SteamIntegrationService {

	private final SteamApiClient steamApiClient;

	public SteamIntegrationService(SteamApiClient steamApiClient) {
		this.steamApiClient = steamApiClient;
	}

	public SteamProfileResponse getPlayerSummary() {
		return steamApiClient.getPlayerSummary();
	}
}
