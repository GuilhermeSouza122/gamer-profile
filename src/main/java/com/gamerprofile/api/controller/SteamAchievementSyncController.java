package com.gamerprofile.api.controller;

import com.gamerprofile.integration.steam.SteamIntegrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/integrations/steam/games")
public class SteamAchievementSyncController {

	private final SteamIntegrationService steamIntegrationService;

	public SteamAchievementSyncController(SteamIntegrationService steamIntegrationService) {
		this.steamIntegrationService = steamIntegrationService;
	}

	@RequestMapping(value = "/{appId}/achievements/sync", method = {RequestMethod.GET, RequestMethod.POST})
	public Map<String, Object> sync(@PathVariable Integer appId) {
		try {
			int count = steamIntegrationService.syncAchievements(appId);
			return Map.of("status", "SYNCED", "appId", appId, "achievements", count);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
		}
	}
}
