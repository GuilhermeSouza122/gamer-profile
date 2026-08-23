package com.gamerprofile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "games", uniqueConstraints = @UniqueConstraint(name = "uk_games_platform_external_id", columnNames = {"platform", "external_id"}))
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 40)
	private String platform;

	@Column(name = "external_id", nullable = false, length = 100)
	private String externalId;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(name = "playtime_minutes")
	private Integer playtimeMinutes;

	@Column(name = "last_played_epoch_seconds")
	private Long lastPlayedEpochSeconds;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	protected Game() {
	}

	public Game(String platform, String externalId, String name) {
		this.platform = platform;
		this.externalId = externalId;
		this.name = name;
	}

	public Long getId() { return id; }
	public String getPlatform() { return platform; }
	public String getExternalId() { return externalId; }
	public String getName() { return name; }
	public Integer getPlaytimeMinutes() { return playtimeMinutes; }
	public Long getLastPlayedEpochSeconds() { return lastPlayedEpochSeconds; }
	public String getImageUrl() { return imageUrl; }
}
