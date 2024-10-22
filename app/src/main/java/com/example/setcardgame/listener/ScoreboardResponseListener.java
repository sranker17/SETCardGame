package com.example.setcardgame.listener;

import com.example.setcardgame.model.scoreboard.TopScores;

public interface ScoreboardResponseListener {
    void onError(String message);

    void onResponse(TopScores topScores);
}
