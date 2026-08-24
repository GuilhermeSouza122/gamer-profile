package com.gamerprofile.integration.steam;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		return getPlayerSummary(steamId);
	}

	public SteamProfileResponse getPlayerSummary(String playerSteamId) {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/ISteamUser/GetPlayerSummaries/v0002/")
						.queryParam("key", apiKey)
					.queryParam("steamids", playerSteamId)
						.build())
				.retrieve()
				.body(SteamProfileResponse.class);
	}

	public SteamAchievementsResponse getPlayerAchievements(Integer appId) {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/ISteamUserStats/GetPlayerAchievements/v0001/")
						.queryParam("key", apiKey)
						.queryParam("steamid", steamId)
						.queryParam("appid", appId)
						.build())
				.retrieve()
				.body(SteamAchievementsResponse.class);
	}

	public SteamAchievementSchemaResponse getAchievementSchema(Integer appId) {
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/ISteamUserStats/GetSchemaForGame/v2/")
						.queryParam("key", apiKey)
						.queryParam("appid", appId)
						.build())
				.retrieve()
				.body(SteamAchievementSchemaResponse.class);
	}

	public SteamGamesResponse getOwnedGames() {
		Map<String, Object> payload = restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/IPlayerService/GetOwnedGames/v0001/")
						.queryParam("key", apiKey)
						.queryParam("steamid", steamId)
						.queryParam("include_appinfo", 1)
						.queryParam("include_played_free_games", 1)
						.build())
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});

		if (payload == null || !(payload.get("response") instanceof Map<?, ?> response)) {
			return new SteamGamesResponse(null);
		}

		List<SteamGamesResponse.SteamGame> games = new ArrayList<>();
		if (response.get("games") instanceof List<?> rawGames) {
			for (Object rawGame : rawGames) {
				if (rawGame instanceof Map<?, ?> game) {
					games.add(new SteamGamesResponse.SteamGame(
							integerValue(game.get("appid")),
							stringValue(game.get("name")),
							integerValue(value(game, "playtime_forever", "playtimeForever")),
							integerValue(value(game, "playtime_2weeks", "playtimeTwoWeeks")),
							longValue(value(game, "rtime_last_played", "rtimeLastPlayed")),
							stringValue(value(game, "img_icon_url", "imgIconUrl")),
							stringValue(value(game, "img_logo_url", "imgLogoUrl"))));
				}
			}
		}

		return new SteamGamesResponse(new SteamGamesResponse.SteamGamesData(
				integerValue(response.get("game_count")), games));
	}

	private static Integer integerValue(Object value) {
		if (value instanceof Number number) return number.intValue();
		return value instanceof String string ? Integer.valueOf(string) : null;
	}

	private static Object value(Map<?, ?> map, String primaryKey, String fallbackKey) {
		return map.containsKey(primaryKey) ? map.get(primaryKey) : map.get(fallbackKey);
	}

	private static Long longValue(Object value) {
		if (value instanceof Number number) return number.longValue();
		return value instanceof String string ? Long.valueOf(string) : null;
	}

	private static String stringValue(Object value) {
		return value instanceof String string ? string : null;
	}
}
