package com.gamerprofile.integration.steam;

import com.gamerprofile.api.dto.SteamAchievementDto;
import com.gamerprofile.model.Achievement;
import com.gamerprofile.model.Game;
import com.gamerprofile.model.User;
import com.gamerprofile.repository.AchievementRepository;
import com.gamerprofile.repository.GameRepository;
import com.gamerprofile.repository.PlatformConnectionRepository;
import com.gamerprofile.repository.UserRepository;
import com.gamerprofile.service.SiteAchievementService;
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
	private final PlatformConnectionRepository connectionRepository;
	private final UserRepository userRepository;
	private final SiteAchievementService siteAchievementService;

	public SteamIntegrationService(SteamApiClient steamApiClient, GameRepository gameRepository,
			AchievementRepository achievementRepository, PlatformConnectionRepository connectionRepository,
			UserRepository userRepository, SiteAchievementService siteAchievementService) {
		this.steamApiClient = steamApiClient;
		this.gameRepository = gameRepository;
		this.achievementRepository = achievementRepository;
		this.connectionRepository = connectionRepository;
		this.userRepository = userRepository;
		this.siteAchievementService = siteAchievementService;
	}

	public SteamProfileResponse getPlayerSummary(Long userId) {
		return steamApiClient.getPlayerSummary(steamId(userId));
	}

	public List<SteamGamesResponse.SteamGame> getOwnedGames(Long userId) {
		SteamGamesResponse response = steamApiClient.getOwnedGames(steamId(userId));

		if (response == null || response.response() == null || response.response().games() == null) {
			return List.of();
		}

		return response.response().games();
	}

	public List<SteamAchievementsResponse.SteamAchievement> getPlayerAchievements(Long userId, Integer appId) {
		SteamAchievementsResponse response = steamApiClient.getPlayerAchievements(appId, steamId(userId));

		if (response == null || response.playerstats() == null || response.playerstats().achievements() == null) {
			return List.of();
		}

		return response.playerstats().achievements();
	}

	public List<SteamAchievementDto> getCompleteAchievements(Long userId, Integer appId) {
		SteamAchievementsResponse progressResponse = steamApiClient.getPlayerAchievements(appId, steamId(userId));
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
	public int syncGames(Long userId) {
		User user = userRepository.findById(userId).orElseThrow();
		List<SteamGamesResponse.SteamGame> steamGames = getOwnedGames(userId);
		for (SteamGamesResponse.SteamGame steamGame : steamGames) {
			String externalId = String.valueOf(steamGame.getAppid());
			Game game = gameRepository.findByUserIdAndPlatformAndExternalId(userId, "STEAM", externalId)
					.orElseGet(() -> new Game(user, "STEAM", externalId,
							steamGame.getName() == null ? externalId : steamGame.getName()));
			String imageUrl = steamGame.getImgIconUrl() == null ? null
					: "https://media.steampowered.com/steamcommunity/public/images/apps/"
							+ externalId + "/" + steamGame.getImgIconUrl() + ".jpg";
			game.updateDetails(steamGame.getName() == null ? externalId : steamGame.getName(),
					steamGame.getPlaytimeForever(), steamGame.getRtimeLastPlayed(), imageUrl);
			gameRepository.save(game);
		}
		siteAchievementService.evaluate(user);
		return steamGames.size();
	}

	@Transactional
	public int syncAchievements(Long userId, Integer appId) {
		Game game = gameRepository.findByUserIdAndPlatformAndExternalId(userId, "STEAM", String.valueOf(appId))
				.orElseThrow(() -> new IllegalArgumentException("Steam game is not synchronized yet: " + appId));

		List<SteamAchievementDto> achievements = getCompleteAchievements(userId, appId);
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

	public SteamBulkSyncResult syncAllAchievements(Long userId) {
		int gamesProcessed = 0;
		int achievementsSynced = 0;
		List<String> failures = new java.util.ArrayList<>();

		for (Game game : gameRepository.findAllByUserId(userId)) {
			if (!"STEAM".equals(game.getPlatform())) {
				continue;
			}

			try {
				int appId = Integer.parseInt(game.getExternalId());
				achievementsSynced += syncAchievements(userId, appId);
				gamesProcessed++;
			} catch (RuntimeException exception) {
				failures.add(game.getExternalId() + ": " + exception.getClass().getSimpleName());
			}
		}
		userRepository.findById(userId).ifPresent(siteAchievementService::evaluate);

		return new SteamBulkSyncResult(gamesProcessed, achievementsSynced, failures);
	}

	private String steamId(Long userId) {
		return connectionRepository.findByUserIdAndPlatform(userId, "STEAM")
				.orElseThrow(() -> new IllegalArgumentException("Steam account is not connected"))
				.getExternalAccountId();
	}
}
