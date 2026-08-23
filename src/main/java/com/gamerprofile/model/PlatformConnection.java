package com.gamerprofile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "platform_connections", uniqueConstraints = @UniqueConstraint(
		name = "uk_platform_connections_user_platform",
		columnNames = {"user_id", "platform"}))
public class PlatformConnection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 40)
	private String platform;

	@Column(name = "external_account_id", nullable = false, length = 150)
	private String externalAccountId;

	@Column(name = "access_token", length = 2000)
	private String accessToken;

	@Column(name = "refresh_token", length = 2000)
	private String refreshToken;

	@Column(name = "connected_at", nullable = false)
	private Instant connectedAt;

	protected PlatformConnection() {
	}

	public PlatformConnection(User user, String platform, String externalAccountId) {
		this.user = user;
		this.platform = platform;
		this.externalAccountId = externalAccountId;
		this.connectedAt = Instant.now();
	}

	public Long getId() { return id; }
	public User getUser() { return user; }
	public String getPlatform() { return platform; }
	public String getExternalAccountId() { return externalAccountId; }
	public Instant getConnectedAt() { return connectedAt; }
}
