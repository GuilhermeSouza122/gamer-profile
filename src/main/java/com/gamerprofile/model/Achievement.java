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

@Entity
@Table(name = "achievements", uniqueConstraints = @UniqueConstraint(
		name = "uk_achievements_game_external_id",
		columnNames = {"game_id", "external_id"}))
public class Achievement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@Column(name = "external_id", nullable = false, length = 150)
	private String externalId;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private boolean achieved;

	@Column(name = "unlock_time")
	private Long unlockTime;

	@Column(name = "icon_url", length = 500)
	private String iconUrl;

	@Column(name = "icon_gray_url", length = 500)
	private String iconGrayUrl;

	protected Achievement() {
	}

	public Achievement(Game game, String externalId, String name) {
		this.game = game;
		this.externalId = externalId;
		this.name = name;
	}

	public Long getId() { return id; }
	public Game getGame() { return game; }
	public String getExternalId() { return externalId; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public boolean isAchieved() { return achieved; }
	public Long getUnlockTime() { return unlockTime; }
	public String getIconUrl() { return iconUrl; }
	public String getIconGrayUrl() { return iconGrayUrl; }
}
