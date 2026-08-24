package com.gamerprofile.service;

import com.gamerprofile.model.Game;
import com.gamerprofile.model.User;
import com.gamerprofile.model.UserSiteAchievement;
import com.gamerprofile.api.dto.SiteAchievementDto;
import com.gamerprofile.repository.AchievementRepository;
import com.gamerprofile.repository.GameRepository;
import com.gamerprofile.repository.PlatformConnectionRepository;
import com.gamerprofile.repository.UserSiteAchievementRepository;
import com.gamerprofile.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
public class SiteAchievementService {

	public static final List<String> CODES = List.of(
			"FIRST_CONNECTION", "FIRST_SYNC", "COLLECTOR", "BIG_LIBRARY", "TROPHY_HUNTER",
			"VETERAN_HUNTER", "COMPLETE_GAME", "MULTI_PLATFORM", "EXPLORER", "MARATHONER",
			"VETERAN", "PERFECTIONIST", "COMPLETE_PROFILE", "LIBRARIAN", "GAMING_EMPIRE",
			"MASTER_HUNTER", "TROPHY_LEGEND", "DEDICATED_GAMER", "COMPLETIONIST", "UNIVERSAL_CONNECTOR");

	private static final Map<String, Definition> DEFINITIONS = definitions();

	private final GameRepository gameRepository;
	private final AchievementRepository achievementRepository;
	private final PlatformConnectionRepository connectionRepository;
	private final UserSiteAchievementRepository siteAchievementRepository;
	private final UserRepository userRepository;

	public SiteAchievementService(GameRepository gameRepository, AchievementRepository achievementRepository,
			PlatformConnectionRepository connectionRepository, UserSiteAchievementRepository siteAchievementRepository,
			UserRepository userRepository) {
		this.gameRepository = gameRepository;
		this.achievementRepository = achievementRepository;
		this.connectionRepository = connectionRepository;
		this.siteAchievementRepository = siteAchievementRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void evaluate(User user) {
		user = userRepository.findById(user.getId()).orElseThrow();
		int alreadyEarnedXp = siteAchievementRepository.findAllByUserId(user.getId()).stream()
				.mapToInt(achievement -> xpFor(achievement.getAchievementCode())).sum();
		user.ensureExperience(alreadyEarnedXp);
		List<Game> games = gameRepository.findAllByUserId(user.getId());
		long totalAchievements = 0;
		long unlockedAchievements = 0;
		long completedGames = 0;
		long totalPlaytime = games.stream().mapToLong(game -> game.getPlaytimeMinutes() == null ? 0 : game.getPlaytimeMinutes()).sum();
		for (Game game : games) {
			var achievements = achievementRepository.findAllByGameId(game.getId());
			totalAchievements += achievements.size();
			unlockedAchievements += achievements.stream().filter(achievement -> achievement.isAchieved()).count();
			if (!achievements.isEmpty() && achievements.stream().allMatch(achievement -> achievement.isAchieved())) completedGames++;
		}
		long platforms = connectionRepository.findAllByUserId(user.getId()).stream().map(connection -> connection.getPlatform()).distinct().count();
		if (platforms >= 1) unlock(user, "FIRST_CONNECTION", 50);
		if (!games.isEmpty()) unlock(user, "FIRST_SYNC", 100);
		if (games.size() >= 10) unlock(user, "COLLECTOR", 250);
		if (games.size() >= 50) unlock(user, "BIG_LIBRARY", 500);
		if (unlockedAchievements >= 10) unlock(user, "TROPHY_HUNTER", 250);
		if (unlockedAchievements >= 100) unlock(user, "VETERAN_HUNTER", 1000);
		if (completedGames >= 1) unlock(user, "COMPLETE_GAME", 300);
		if (platforms >= 2) unlock(user, "MULTI_PLATFORM", 300);
		if (platforms >= 3) unlock(user, "EXPLORER", 750);
		if (totalPlaytime >= 6000) unlock(user, "MARATHONER", 500);
		if (totalPlaytime >= 30000) unlock(user, "VETERAN", 1500);
		if (completedGames >= 5) unlock(user, "PERFECTIONIST", 1000);
		if (user.getAvatarUrl() != null && platforms >= 1) unlock(user, "COMPLETE_PROFILE", 100);
		if (games.size() >= 100) unlock(user, "LIBRARIAN", 1000);
		if (games.size() >= 200) unlock(user, "GAMING_EMPIRE", 2000);
		if (unlockedAchievements >= 250) unlock(user, "MASTER_HUNTER", 1500);
		if (unlockedAchievements >= 500) unlock(user, "TROPHY_LEGEND", 2500);
		if (totalPlaytime >= 60000) unlock(user, "DEDICATED_GAMER", 2500);
		if (completedGames >= 10) unlock(user, "COMPLETIONIST", 2500);
		if (platforms >= 4) unlock(user, "UNIVERSAL_CONNECTOR", 2000);
		userRepository.save(user);
	}

	public List<String> unlockedCodes(Long userId) {
		return siteAchievementRepository.findAllByUserId(userId).stream().map(UserSiteAchievement::getAchievementCode).toList();
	}

	public List<SiteAchievementDto> details(Long userId) {
		Map<String, UserSiteAchievement> unlocked = siteAchievementRepository.findAllByUserId(userId).stream()
				.collect(java.util.stream.Collectors.toMap(UserSiteAchievement::getAchievementCode, achievement -> achievement));
		return CODES.stream().map(code -> {
			Definition definition = DEFINITIONS.get(code);
			UserSiteAchievement achievement = unlocked.get(code);
			return new SiteAchievementDto(code, definition.name(), definition.description(), definition.xp(), achievement != null,
					achievement == null ? null : achievement.getUnlockedAt());
		}).toList();
	}

	private void unlock(User user, String code, int xp) {
		if (siteAchievementRepository.findByUserIdAndAchievementCode(user.getId(), code).isEmpty()) {
			siteAchievementRepository.save(new UserSiteAchievement(user, code));
			user.addExperience(xp);
		}
	}

	private int xpFor(String code) {
		return DEFINITIONS.getOrDefault(code, new Definition(code, "", "", 0)).xp();
	}

	private static Map<String, Definition> definitions() {
		Map<String, Definition> definitions = new LinkedHashMap<>();
		add(definitions, "FIRST_CONNECTION", "Primeira conexão", "Conecte sua primeira plataforma.", 50);
		add(definitions, "FIRST_SYNC", "Primeira sincronização", "Importe sua primeira biblioteca.", 100);
		add(definitions, "COLLECTOR", "Colecionador", "Possua 10 jogos sincronizados.", 250);
		add(definitions, "BIG_LIBRARY", "Grande biblioteca", "Possua 50 jogos sincronizados.", 500);
		add(definitions, "TROPHY_HUNTER", "Caçador de troféus", "Desbloqueie 10 conquistas.", 250);
		add(definitions, "VETERAN_HUNTER", "Caçador veterano", "Desbloqueie 100 conquistas.", 1000);
		add(definitions, "COMPLETE_GAME", "Jogo completo", "Atinja 100% em um jogo.", 300);
		add(definitions, "MULTI_PLATFORM", "Multi-plataforma", "Conecte 2 plataformas.", 300);
		add(definitions, "EXPLORER", "Explorador", "Conecte 3 plataformas.", 750);
		add(definitions, "MARATHONER", "Maratonista", "Acumule 100 horas jogadas.", 500);
		add(definitions, "VETERAN", "Veterano", "Acumule 500 horas jogadas.", 1500);
		add(definitions, "PERFECTIONIST", "Em busca da perfeição", "Complete 5 jogos em 100%.", 1000);
		add(definitions, "COMPLETE_PROFILE", "Perfil completo", "Tenha avatar e plataforma conectados.", 100);
		add(definitions, "LIBRARIAN", "Bibliotecário", "Possua 100 jogos.", 1000);
		add(definitions, "GAMING_EMPIRE", "Império gamer", "Possua 200 jogos.", 2000);
		add(definitions, "MASTER_HUNTER", "Mestre caçador", "Desbloqueie 250 conquistas.", 1500);
		add(definitions, "TROPHY_LEGEND", "Lenda dos troféus", "Desbloqueie 500 conquistas.", 2500);
		add(definitions, "DEDICATED_GAMER", "Gamer dedicado", "Acumule 1.000 horas jogadas.", 2500);
		add(definitions, "COMPLETIONIST", "Completionist", "Complete 10 jogos em 100%.", 2500);
		add(definitions, "UNIVERSAL_CONNECTOR", "Conector universal", "Conecte 4 plataformas.", 2000);
		return Map.copyOf(definitions);
	}

	private static void add(Map<String, Definition> map, String code, String name, String description, int xp) {
		map.put(code, new Definition(code, name, description, xp));
	}

	private record Definition(String code, String name, String description, int xp) { }
}
