package com.gamerprofile.integration.steam;

import java.util.List;

public record SteamBulkSyncResult(
		int gamesProcessed,
		int achievementsSynced,
		List<String> failures
) {
}
