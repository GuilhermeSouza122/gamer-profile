package com.gamerprofile.api.controller;

import com.gamerprofile.api.dto.ProfileDto;
import com.gamerprofile.model.User;
import com.gamerprofile.repository.UserRepository;
import com.gamerprofile.integration.steam.SteamApiClient;
import com.gamerprofile.integration.steam.SteamProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

	private static final Set<String> AVATARS = Set.of("cyberpunk", "dragon", "wizard", "robot", "hunter", "ninja");
	private final UserRepository userRepository;
	private final SteamApiClient steamApiClient;

	public ProfileController(UserRepository userRepository, SteamApiClient steamApiClient) {
		this.userRepository = userRepository;
		this.steamApiClient = steamApiClient;
	}

	@GetMapping("/{userId}")
	public ProfileDto getProfile(@PathVariable Long userId) {
		User user = findUser(userId);
		if (user.getAvatarUrl() == null && user.getUsername().startsWith("steam:")) {
			try {
				SteamProfileResponse summary = steamApiClient.getPlayerSummary(user.getUsername().substring(6));
				if (summary != null && summary.response() != null && summary.response().players() != null
						&& !summary.response().players().isEmpty()) {
					SteamProfileResponse.SteamPlayer player = summary.response().players().getFirst();
					user.updateSteamProfile(player.personaname(), player.avatarfull());
					user = userRepository.save(user);
				}
			} catch (RuntimeException ignored) {
				// The profile can still be displayed with its stored local data.
			}
		}
		return toDto(user);
	}

	@PatchMapping("/{userId}/avatar")
	public ProfileDto updateAvatar(@PathVariable Long userId, @RequestBody Map<String, String> body) {
		String avatarKey = body.get("avatarKey");
		if (!AVATARS.contains(avatarKey)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avatar inválido");
		}
		User user = findUser(userId);
		user.changeAvatar(avatarKey);
		return toDto(userRepository.save(user));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
	}

	private ProfileDto toDto(User user) {
		return new ProfileDto(user.getId(), user.getUsername(), user.getDisplayName(), user.getAvatarKey(), user.getAvatarUrl());
	}
}
