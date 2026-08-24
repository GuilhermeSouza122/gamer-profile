package com.gamerprofile.service;

import com.gamerprofile.model.PlatformConnection;
import com.gamerprofile.model.User;
import com.gamerprofile.repository.PlatformConnectionRepository;
import com.gamerprofile.repository.UserRepository;
import com.gamerprofile.integration.steam.SteamApiClient;
import com.gamerprofile.integration.steam.SteamProfileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SteamAuthenticationService {

	private static final String STEAM_OPENID = "https://steamcommunity.com/openid/login";
	private static final Pattern STEAM_ID_PATTERN = Pattern.compile("/openid/id/(\\d+)");

	private final RestClient steamOpenIdClient;
	private final UserRepository userRepository;
	private final PlatformConnectionRepository connectionRepository;
	private final SteamApiClient steamApiClient;
	private final String returnUrl;
	private final String realm;

	public SteamAuthenticationService(
			RestClient steamOpenIdClient,
			UserRepository userRepository,
			PlatformConnectionRepository connectionRepository,
			SteamApiClient steamApiClient,
			@Value("${steam.auth.return-url}") String returnUrl,
			@Value("${steam.auth.realm}") String realm) {
		this.steamOpenIdClient = steamOpenIdClient;
		this.userRepository = userRepository;
		this.connectionRepository = connectionRepository;
		this.steamApiClient = steamApiClient;
		this.returnUrl = returnUrl;
		this.realm = realm;
	}

	public String buildLoginUrl() {
		return STEAM_OPENID + "?openid.ns=http://specs.openid.net/auth/2.0"
				+ "&openid.mode=checkid_setup"
				+ "&openid.return_to=" + encode(returnUrl)
				+ "&openid.realm=" + encode(realm)
				+ "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select"
				+ "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select";
	}

	public Map<String, Object> authenticate(MultiValueMap<String, String> parameters) {
		MultiValueMap<String, String> verification = new LinkedMultiValueMap<>();
		parameters.forEach((key, values) -> {
			if (!values.isEmpty()) verification.add("openid." + key.substring("openid.".length()), values.getFirst());
		});
		verification.set("openid.mode", "check_authentication");

		String response = steamOpenIdClient.post()
				.uri("/openid/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(verification)
				.retrieve()
				.body(String.class);

		if (response == null || !response.contains("is_valid:true")) {
			throw new IllegalArgumentException("Steam OpenID validation failed");
		}

		String claimedId = parameters.getFirst("openid.claimed_id");
		Matcher matcher = claimedId == null ? null : STEAM_ID_PATTERN.matcher(claimedId);
		if (matcher == null || !matcher.find()) {
			throw new IllegalArgumentException("SteamID64 not found in OpenID response");
		}

		String steamId = matcher.group(1);
		User user = userRepository.findByUsername("steam:" + steamId)
				.orElseGet(() -> userRepository.save(new User("steam:" + steamId, "Steam User")));
		try {
			SteamProfileResponse summary = steamApiClient.getPlayerSummary(steamId);
			if (summary != null && summary.response() != null && summary.response().players() != null
					&& !summary.response().players().isEmpty()) {
				SteamProfileResponse.SteamPlayer player = summary.response().players().getFirst();
				user.updateSteamProfile(player.personaname(), player.avatarfull());
				user = userRepository.save(user);
			}
		} catch (RuntimeException ignored) {
			// Login remains valid even if Steam's profile summary is temporarily unavailable.
		}
		User authenticatedUser = user;
		connectionRepository.findByUserIdAndPlatform(authenticatedUser.getId(), "STEAM")
				.orElseGet(() -> connectionRepository.save(new PlatformConnection(authenticatedUser, "STEAM", steamId)));

		return Map.of("status", "CONNECTED", "userId", authenticatedUser.getId(), "platform", "STEAM", "externalAccountId", steamId);
	}

	private String encode(String value) {
		return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
	}
}
