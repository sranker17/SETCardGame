package com.example.setcardgame.model.scoreboard;

import androidx.annotation.NonNull;

import com.example.setcardgame.model.Difficulty;

import java.util.UUID;

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

    public boolean isMyScore() {
        return myScore;
    }

    public void setMyScore(boolean myScore) {
        this.myScore = myScore;
    }

    public int getPlacement() {
        return placement;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
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