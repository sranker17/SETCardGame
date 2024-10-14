package com.example.setcardgame.model.scoreboard;

import androidx.annotation.NonNull;

import com.example.setcardgame.model.Difficulty;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Scoreboard {

    private UUID playerId;
    private Difficulty difficulty;
    private int score;
    private int time;
    private int placement;
    private boolean myScore = false;

    public Scoreboard(String playerId, String difficulty, int score, int time) {
        this.playerId = UUID.fromString(playerId);
        this.difficulty = Difficulty.getDifficultyFromString(difficulty);
        this.score = score;
        this.time = time;
    }

    @NonNull
    @Override
    public String toString() {
        int minutes = time / 60;
        int seconds = time % 60;
        return difficulty + ", " + score + " points, " + String.format("%d:%02d", minutes, seconds);
    }
}
