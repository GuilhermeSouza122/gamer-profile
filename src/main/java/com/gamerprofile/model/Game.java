package com.gamerprofile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "games", uniqueConstraints = @UniqueConstraint(name = "uk_games_user_platform_external_id", columnNames = {"user_id", "platform", "external_id"}))
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

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

	public Game(User user, String platform, String externalId, String name) {
		this.user = user;
		this.platform = platform;
		this.externalId = externalId;
		this.name = name;
	}

	public void updateDetails(String name, Integer playtimeMinutes, Long lastPlayedEpochSeconds, String imageUrl) {
		this.name = name;
		this.playtimeMinutes = playtimeMinutes;
		this.lastPlayedEpochSeconds = lastPlayedEpochSeconds;
		this.imageUrl = imageUrl;
	}

	public Long getId() { return id; }
	public User getUser() { return user; }
	public String getPlatform() { return platform; }
	public String getExternalId() { return externalId; }
	public String getName() { return name; }
	public Integer getPlaytimeMinutes() { return playtimeMinutes; }
	public Long getLastPlayedEpochSeconds() { return lastPlayedEpochSeconds; }
	public String getImageUrl() { return imageUrl; }
}
