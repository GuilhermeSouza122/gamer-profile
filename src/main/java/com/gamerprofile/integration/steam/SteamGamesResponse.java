package com.gamerprofile.integration.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamGamesResponse(SteamGamesData response) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SteamGamesData(Integer gameCount, List<SteamGame> games) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SteamGame {

		@JsonProperty("appid")
		private Integer appid;
		@JsonProperty("name")
		private String name;
		@JsonProperty("playtime_forever")
		private Integer playtimeForever;
		@JsonProperty("playtime_2weeks")
		private Integer playtimeTwoWeeks;
		@JsonProperty("rtime_last_played")
		private Long rtimeLastPlayed;
		@JsonProperty("img_icon_url")
		private String imgIconUrl;
		@JsonProperty("img_logo_url")
		private String imgLogoUrl;

		public SteamGame(Integer appid, String name, Integer playtimeForever,
				Integer playtimeTwoWeeks, Long rtimeLastPlayed, String imgIconUrl, String imgLogoUrl) {
			this.appid = appid;
			this.name = name;
			this.playtimeForever = playtimeForever;
			this.playtimeTwoWeeks = playtimeTwoWeeks;
			this.rtimeLastPlayed = rtimeLastPlayed;
			this.imgIconUrl = imgIconUrl;
			this.imgLogoUrl = imgLogoUrl;
		}

		public Integer getAppid() { return appid; }
		public String getName() { return name; }
		public Integer getPlaytimeForever() { return playtimeForever; }
		public Integer getPlaytimeTwoWeeks() { return playtimeTwoWeeks; }
		public Long getRtimeLastPlayed() { return rtimeLastPlayed; }
		public String getImgIconUrl() { return imgIconUrl; }
		public String getImgLogoUrl() { return imgLogoUrl; }

		public void setPlaytime_forever(Integer value) { this.playtimeForever = value; }
		public void setPlaytime_2weeks(Integer value) { this.playtimeTwoWeeks = value; }
		public void setRtime_last_played(Long value) { this.rtimeLastPlayed = value; }
		public void setImg_icon_url(String value) { this.imgIconUrl = value; }
		public void setImg_logo_url(String value) { this.imgLogoUrl = value; }
	}
}
