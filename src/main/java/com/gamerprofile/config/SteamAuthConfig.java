package com.gamerprofile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SteamAuthConfig {

	@Bean
	RestClient steamOpenIdClient() {
		return RestClient.builder()
				.baseUrl("https://steamcommunity.com")
				.build();
	}
}
