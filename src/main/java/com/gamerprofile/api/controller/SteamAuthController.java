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
	private final String frontendUrl;

	public SteamAuthController(SteamAuthenticationService authenticationService,
			@org.springframework.beans.factory.annotation.Value("${steam.auth.frontend-url}") String frontendUrl) {
		this.authenticationService = authenticationService;
		this.frontendUrl = frontendUrl;
	}

	@GetMapping("/auth/steam")
	public RedirectView login() {
		return new RedirectView(authenticationService.buildLoginUrl());
	}

	@GetMapping("/auth/steam/callback")
	public RedirectView callback(@RequestParam MultiValueMap<String, String> parameters) {
		try {
			Map<String, Object> result = authenticationService.authenticate(parameters);
			return new RedirectView(frontendUrl + "?connected=steam&userId=" + result.get("userId"));
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage());
		}
	}
}
