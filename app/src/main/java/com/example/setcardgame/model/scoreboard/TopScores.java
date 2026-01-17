package com.example.setcardgame.model.scoreboard;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TopScores {
    private List<Scoreboard> easyScores;
    private List<Scoreboard> normalScores;
}
