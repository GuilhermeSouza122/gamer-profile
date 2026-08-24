package com.gamerprofile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "app_users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String username;

	@Column(name = "display_name", nullable = false, length = 120)
	private String displayName;

	@Column(name = "avatar_key", nullable = false, length = 40)
	private String avatarKey;

	@Column(name = "avatar_url", length = 500)
	private String avatarUrl;

	@Column(name = "xp_points", nullable = false)
	private Integer xpPoints;

	@Column(nullable = false)
	private Integer level;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected User() {
	}

	public User(String username, String displayName) {
		this.username = username;
		this.displayName = displayName;
		this.avatarKey = "cyberpunk";
		this.xpPoints = 0;
		this.level = 1;
		this.createdAt = Instant.now();
	}

	public Long getId() { return id; }
	public String getUsername() { return username; }
	public String getDisplayName() { return displayName; }
	public String getAvatarKey() { return avatarKey; }
	public String getAvatarUrl() { return avatarUrl; }
	public Integer getXpPoints() { return xpPoints; }
	public Integer getLevel() { return level; }
	public Instant getCreatedAt() { return createdAt; }
	public void changeAvatar(String avatarKey) { this.avatarKey = avatarKey; }
	public void updateSteamProfile(String displayName, String avatarUrl) {
		this.displayName = displayName;
		this.avatarUrl = avatarUrl;
	}

	public void addExperience(int points) {
		this.xpPoints += points;
		this.level = 1 + (this.xpPoints / 1000);
	}

	public void ensureExperience(int points) {
		if (this.xpPoints < points) {
			this.xpPoints = points;
			this.level = 1 + (this.xpPoints / 1000);
		}
	}
}
