package com.gamerprofile.integration.steam;

import com.gamerprofile.api.dto.SteamAchievementDto;
import com.gamerprofile.model.Achievement;
import com.gamerprofile.model.Game;
import com.gamerprofile.repository.AchievementRepository;
import com.gamerprofile.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SteamIntegrationService {

	private final SteamApiClient steamApiClient;
	private final GameRepository gameRepository;
	private final AchievementRepository achievementRepository;

	public SteamIntegrationService(SteamApiClient steamApiClient, GameRepository gameRepository,
			AchievementRepository achievementRepository) {
		this.steamApiClient = steamApiClient;
		this.gameRepository = gameRepository;
		this.achievementRepository = achievementRepository;
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

	@Transactional
	public int syncGames() {
		List<SteamGamesResponse.SteamGame> steamGames = getOwnedGames();
		for (SteamGamesResponse.SteamGame steamGame : steamGames) {
			String externalId = String.valueOf(steamGame.getAppid());
			Game game = gameRepository.findByPlatformAndExternalId("STEAM", externalId)
					.orElseGet(() -> new Game("STEAM", externalId,
							steamGame.getName() == null ? externalId : steamGame.getName()));
			String imageUrl = steamGame.getImgIconUrl() == null ? null
					: "https://media.steampowered.com/steamcommunity/public/images/apps/"
							+ externalId + "/" + steamGame.getImgIconUrl() + ".jpg";
			game.updateDetails(steamGame.getName() == null ? externalId : steamGame.getName(),
					steamGame.getPlaytimeForever(), steamGame.getRtimeLastPlayed(), imageUrl);
			gameRepository.save(game);
		}
		return steamGames.size();
	}

	@Transactional
	public int syncAchievements(Integer appId) {
		Game game = gameRepository.findByPlatformAndExternalId("STEAM", String.valueOf(appId))
				.orElseThrow(() -> new IllegalArgumentException("Steam game is not synchronized yet: " + appId));

		List<SteamAchievementDto> achievements = getCompleteAchievements(appId);
		for (SteamAchievementDto dto : achievements) {
			Achievement achievement = achievementRepository
					.findByGameIdAndExternalId(game.getId(), dto.apiName())
					.orElseGet(() -> new Achievement(game, dto.apiName(),
							dto.name() == null ? dto.apiName() : dto.name()));
			achievement.updateDetails(dto.name() == null ? dto.apiName() : dto.name(),
					dto.description(), dto.achieved(), dto.unlockTime(), dto.icon(), dto.iconGray());
			achievementRepository.save(achievement);
		}

		return achievements.size();
	}

	public SteamBulkSyncResult syncAllAchievements() {
		int gamesProcessed = 0;
		int achievementsSynced = 0;
		List<String> failures = new java.util.ArrayList<>();

		for (Game game : gameRepository.findAll()) {
			if (!"STEAM".equals(game.getPlatform())) {
				continue;
			}

			try {
				int appId = Integer.parseInt(game.getExternalId());
				achievementsSynced += syncAchievements(appId);
				gamesProcessed++;
			} catch (RuntimeException exception) {
				failures.add(game.getExternalId() + ": " + exception.getClass().getSimpleName());
			}
		}

		return new SteamBulkSyncResult(gamesProcessed, achievementsSynced, failures);
	}
}
