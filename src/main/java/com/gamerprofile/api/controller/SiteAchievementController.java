package com.gamerprofile.api.controller;

import com.gamerprofile.service.CurrentUserService;
import com.gamerprofile.service.SiteAchievementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/site-achievements")
public class SiteAchievementController {

	private final SiteAchievementService service;
	private final CurrentUserService currentUserService;

	public SiteAchievementController(SiteAchievementService service, CurrentUserService currentUserService) {
		this.service = service;
		this.currentUserService = currentUserService;
	}

	@GetMapping
	public List<com.gamerprofile.api.dto.SiteAchievementDto> unlocked(HttpServletRequest request) {
		var user = currentUserService.requireUser(request);
		service.evaluate(user);
		return service.details(user.getId());
	}
}
