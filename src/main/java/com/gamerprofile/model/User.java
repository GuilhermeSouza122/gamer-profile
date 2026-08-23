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

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected User() {
	}

	public User(String username, String displayName) {
		this.username = username;
		this.displayName = displayName;
		this.createdAt = Instant.now();
	}

	public Long getId() { return id; }
	public String getUsername() { return username; }
	public String getDisplayName() { return displayName; }
	public Instant getCreatedAt() { return createdAt; }
}
