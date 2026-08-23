package com.gamerprofile.api.controller;

import com.gamerprofile.api.dto.SteamGameDto;
import com.gamerprofile.api.dto.SteamAchievementDto;
import com.gamerprofile.integration.steam.SteamIntegrationService;
import com.gamerprofile.integration.steam.SteamGamesResponse;
import com.gamerprofile.integration.steam.SteamAchievementsResponse;
import com.gamerprofile.integration.steam.SteamProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/integrations/steam")
public class SteamProfileController {

	private final SteamIntegrationService steamIntegrationService;

	public SteamProfileController(SteamIntegrationService steamIntegrationService) {
		this.steamIntegrationService = steamIntegrationService;
	}

	@GetMapping("/profile")
	public SteamProfileResponse.SteamPlayer profile() {
		SteamProfileResponse response = steamIntegrationService.getPlayerSummary();

		if (response == null || response.response() == null || response.response().players().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Steam profile not found");
		}

		return response.response().players().getFirst();
	}

	@GetMapping("/games")
	public List<SteamGameDto> games() {
		return steamIntegrationService.getOwnedGames().stream()
				.map(SteamGameDto::from)
				.toList();
	}

	@RequestMapping(value = "/games/sync", method = RequestMethod.POST)
	public Map<String, Object> syncGames() {
		int count = steamIntegrationService.syncGames();
		return Map.of("status", "SYNCED", "games", count);
	}

	@GetMapping("/games/{appId}/achievements")
	public List<SteamAchievementDto> achievements(@PathVariable Integer appId) {
		return steamIntegrationService.getCompleteAchievements(appId);
	}

	@RequestMapping(value = "/games/{appId}/achievements/sync", method = RequestMethod.POST)
	public Map<String, Object> syncAchievements(@PathVariable Integer appId) {
		try {
			int count = steamIntegrationService.syncAchievements(appId);
			return Map.of("status", "SYNCED", "appId", appId, "achievements", count);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
		}
	}

}
