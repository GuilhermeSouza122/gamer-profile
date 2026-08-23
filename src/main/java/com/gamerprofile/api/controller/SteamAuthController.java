package com.gamerprofile.api.controller;

import com.gamerprofile.service.SteamAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
public class SteamAuthController {

	private final SteamAuthenticationService authenticationService;

	public SteamAuthController(SteamAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@GetMapping("/auth/steam")
	public RedirectView login() {
		return new RedirectView(authenticationService.buildLoginUrl());
	}

	@GetMapping("/auth/steam/callback")
	public Map<String, Object> callback(@RequestParam MultiValueMap<String, String> parameters) {
		try {
			return authenticationService.authenticate(parameters);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage());
		}
	}
}
