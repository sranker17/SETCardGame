package com.example.setcardgame.listener;

import com.example.setcardgame.model.Error;
import com.example.setcardgame.model.scoreboard.TopScores;

public interface ScoreboardResponseListener extends BaseListener {
    void onError(Error message);

    void onResponse(TopScores topScores);
}
