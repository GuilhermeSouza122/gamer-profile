package com.gamerprofile.api.controller;

import com.gamerprofile.api.dto.AchievementDto;
import com.gamerprofile.api.dto.GameDto;
import com.gamerprofile.repository.AchievementRepository;
import com.gamerprofile.repository.GameRepository;
import com.gamerprofile.service.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

	private final GameRepository gameRepository;
	private final AchievementRepository achievementRepository;
	private final CurrentUserService currentUserService;

	public GameController(GameRepository gameRepository, AchievementRepository achievementRepository, CurrentUserService currentUserService) {
		this.gameRepository = gameRepository;
		this.achievementRepository = achievementRepository;
		this.currentUserService = currentUserService;
	}

	@GetMapping
	public List<GameDto> findAll(HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		return gameRepository.findAllByUserId(userId).stream().map(GameDto::from).toList();
	}

	@GetMapping("/{gameId}/achievements")
	public List<AchievementDto> findAchievements(@PathVariable Long gameId, HttpServletRequest request) {
		Long userId = currentUserService.requireUser(request).getId();
		if (!gameRepository.findAllByUserId(userId).stream().anyMatch(game -> game.getId().equals(gameId))) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found: " + gameId);
		}

		return achievementRepository.findAllByGameId(gameId).stream()
				.map(AchievementDto::from)
				.toList();
	}
}
