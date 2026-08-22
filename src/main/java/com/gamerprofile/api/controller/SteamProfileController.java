package com.gamerprofile.api.controller;

import com.gamerprofile.integration.steam.SteamIntegrationService;
import com.gamerprofile.integration.steam.SteamProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
}
