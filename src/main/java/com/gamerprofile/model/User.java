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

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected User() {
	}

	public User(String username, String displayName) {
		this.username = username;
		this.displayName = displayName;
		this.avatarKey = "cyberpunk";
		this.createdAt = Instant.now();
	}

	public Long getId() { return id; }
	public String getUsername() { return username; }
	public String getDisplayName() { return displayName; }
	public String getAvatarKey() { return avatarKey; }
	public Instant getCreatedAt() { return createdAt; }
	public void changeAvatar(String avatarKey) { this.avatarKey = avatarKey; }
}
