package com.gamerprofile.integration;

import java.util.List;

/**
 * Contrato comum para integrações de plataformas gamer.
 * Cada plataforma deve adaptar sua API para este formato normalizado.
 */
public interface PlatformIntegration {

	String platform();

	PlatformProfile getProfile();

	List<PlatformGame> getGames();

	List<PlatformAchievement> getAchievements(String gameExternalId);
}
