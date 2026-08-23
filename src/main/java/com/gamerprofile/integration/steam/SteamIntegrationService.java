package com.gamerprofile.integration.steam;

import com.gamerprofile.api.dto.SteamAchievementDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	public List<SteamAchievementsResponse.SteamAchievement> getPlayerAchievements(Integer appId) {
		SteamAchievementsResponse response = steamApiClient.getPlayerAchievements(appId);

		if (response == null || response.playerstats() == null || response.playerstats().achievements() == null) {
			return List.of();
		}

		return response.playerstats().achievements();
	}

	public List<SteamAchievementDto> getCompleteAchievements(Integer appId) {
		SteamAchievementsResponse progressResponse = steamApiClient.getPlayerAchievements(appId);
		SteamAchievementSchemaResponse schemaResponse = steamApiClient.getAchievementSchema(appId);

		if (progressResponse == null || progressResponse.playerstats() == null
				|| progressResponse.playerstats().achievements() == null) {
			return List.of();
		}

		Map<String, SteamAchievementSchemaResponse.SteamAchievementDefinition> definitions = Map.of();
		if (schemaResponse != null && schemaResponse.game() != null
				&& schemaResponse.game().availableGameStats() != null
				&& schemaResponse.game().availableGameStats().achievements() != null) {
			definitions = schemaResponse.game().availableGameStats().achievements().stream()
					.collect(Collectors.toMap(
							SteamAchievementSchemaResponse.SteamAchievementDefinition::name,
							Function.identity(),
							(first, ignored) -> first));
		}

		Map<String, SteamAchievementSchemaResponse.SteamAchievementDefinition> definitionsByApiName = definitions;
		return progressResponse.playerstats().achievements().stream()
				.map(progress -> SteamAchievementDto.from(progress, definitionsByApiName.get(progress.apiname())))
				.toList();
	}
}
