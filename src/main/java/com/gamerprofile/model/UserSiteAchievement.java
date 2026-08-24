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
@Table(name = "user_site_achievements", uniqueConstraints = @UniqueConstraint(
		name = "uk_user_site_achievement", columnNames = {"user_id", "achievement_code"}))
public class UserSiteAchievement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "achievement_code", nullable = false, length = 80)
	private String achievementCode;

	@Column(name = "unlocked_at", nullable = false)
	private Instant unlockedAt;

	protected UserSiteAchievement() {
	}

	public UserSiteAchievement(User user, String achievementCode) {
		this.user = user;
		this.achievementCode = achievementCode;
		this.unlockedAt = Instant.now();
	}

	public Long getId() { return id; }
	public User getUser() { return user; }
	public String getAchievementCode() { return achievementCode; }
	public Instant getUnlockedAt() { return unlockedAt; }
}
