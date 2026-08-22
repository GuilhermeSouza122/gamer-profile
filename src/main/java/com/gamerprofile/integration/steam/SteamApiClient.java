package com.gamerprofile.integration.steam;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SteamApiClient {

	private final RestClient restClient;
	private final String apiKey;
	private final String steamId;

	public SteamApiClient(
			RestClient steamRestClient,
			@Value("${steam.api.key}") String apiKey,
			@Value("${steam.api.steam-id}") String steamId
	) {
		this.restClient = steamRestClient;
		this.apiKey = apiKey;
		this.steamId = steamId;
	}

	public SteamProfileResponse getPlayerSummary() {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/ISteamUser/GetPlayerSummaries/v0002/")
						.queryParam("key", apiKey)
						.queryParam("steamids", steamId)
						.build())
				.retrieve()
				.body(SteamProfileResponse.class);
	}
}
