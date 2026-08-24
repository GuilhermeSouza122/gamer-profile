package com.gamerprofile.api.controller;

import com.gamerprofile.api.dto.SteamGameDto;
import com.gamerprofile.api.dto.SteamAchievementDto;
import com.gamerprofile.integration.steam.SteamIntegrationService;
import com.gamerprofile.integration.steam.SteamGamesResponse;
import com.gamerprofile.integration.steam.SteamAchievementsResponse;
import com.gamerprofile.integration.steam.SteamBulkSyncResult;
import com.gamerprofile.integration.steam.SteamProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.gamerprofile.service.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/integrations/steam")
public class SteamProfileController {

	private final SteamIntegrationService steamIntegrationService;
	private final CurrentUserService currentUserService;

	public SteamProfileController(SteamIntegrationService steamIntegrationService, CurrentUserService currentUserService) {
		this.steamIntegrationService = steamIntegrationService;
		this.currentUserService = currentUserService;
	}

	@GetMapping("/profile")
	public SteamProfileResponse.SteamPlayer profile(HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		SteamProfileResponse response = steamIntegrationService.getPlayerSummary(userId);

		if (response == null || response.response() == null || response.response().players().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Steam profile not found");
		}

		return response.response().players().getFirst();
	}

	@GetMapping("/games")
	public List<SteamGameDto> games(HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		return steamIntegrationService.getOwnedGames(userId).stream()
				.map(SteamGameDto::from)
				.toList();
	}

	@RequestMapping(value = "/games/sync", method = RequestMethod.POST)
	public Map<String, Object> syncGames(HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		int count = steamIntegrationService.syncGames(userId);
		return Map.of("status", "SYNCED", "games", count);
	}

	@RequestMapping(value = "/games/achievements/sync-all", method = RequestMethod.POST)
	public SteamBulkSyncResult syncAllAchievements(HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		return steamIntegrationService.syncAllAchievements(userId);
	}

	@GetMapping("/games/{appId}/achievements")
	public List<SteamAchievementDto> achievements(@PathVariable Integer appId, HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		return steamIntegrationService.getCompleteAchievements(userId, appId);
	}

	@RequestMapping(value = "/games/{appId}/achievements/sync", method = RequestMethod.POST)
	public Map<String, Object> syncAchievements(@PathVariable Integer appId, HttpServletRequest request) {
		try {
			Long userId = currentUserService.requireUser(request).getId();
			int count = steamIntegrationService.syncAchievements(userId, appId);
			return Map.of("status", "SYNCED", "appId", appId, "achievements", count);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
		}
	}

}
