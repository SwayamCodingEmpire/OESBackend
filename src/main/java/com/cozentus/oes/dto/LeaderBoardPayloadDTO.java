package com.cozentus.oes.dto;

public record LeaderBoardPayloadDTO(
		String name,
		Double score,
		String time
		)
{
    public LeaderBoardPayloadDTO(LeaderboardDTO leaderboardDTO) {
        this(
            leaderboardDTO.name(),
            leaderboardDTO.score(),
            formatTime(leaderboardDTO.time())
        );
    }
	
	private static String formatTime(Long timeInSeconds) {
        if (timeInSeconds == null) return "0m 0s";
        long minutes = timeInSeconds / 60;
        long seconds = timeInSeconds % 60;
        return minutes + "m " + seconds + "s";
    }

}
