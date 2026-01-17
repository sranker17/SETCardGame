package com.example.setcardgame.model;

import android.util.Log;

import com.example.setcardgame.exception.JsonParsingException;
import com.example.setcardgame.model.card.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MultiplayerGame {
    private int gameId;
    private String player1;
    private String player2;
    private List<Card> board = new ArrayList<>();
    private String winner;
    private String blockedBy;
    private List<Integer> selectedCardIndexes = new ArrayList<>();
    private Map<String, Integer> points = new HashMap<>();
    private List<Integer> nullCardIndexes = new ArrayList<>();
    private boolean playerLeft;

    private static final String TAG = "MultiplayerGame";

    public MultiplayerGame(JSONObject game) {
        createMultiplayerGame(game);
    }

    public void setNullCardIndexesString(String nullCardIndexesString) {
        if (!nullCardIndexesString.equals("[]")) {
            nullCardIndexes.clear();
            try {
                JSONArray jsonArray = new JSONArray(nullCardIndexesString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    nullCardIndexes.add(jsonArray.getInt(i));
                }
            } catch (JSONException e) {
                Log.e(TAG, "setNullCardIndexesString: " + e.getMessage());
                throw new JsonParsingException(e.getMessage());
            }
        }
    }

    public void clearSelectedCardIndexes() {
        selectedCardIndexes.clear();
    }

    public void setGameIdString(String gameId) {
        this.gameId = Integer.parseInt(gameId);
    }

    public void setBoardString(String boardString) {
        if (!boardString.equals("[]")) {
            board.clear();
            try {
                JSONArray jsonArray = new JSONArray(boardString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.isNull(i)) {
                        board.add(null);
                    } else {
                        JSONObject cardJson = jsonArray.getJSONObject(i);
                        String color = cardJson.getString("color");
                        String shape = cardJson.getString("shape");
                        String quantity = cardJson.getString("quantity");
                        Card newCard = new Card(color, shape, quantity);
                        board.add(newCard);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "setBoardString: " + e.getMessage());
                throw new JsonParsingException(e.getMessage());
            }
        }
    }

    public void setBlockedByString(String blockedBy) {
        this.blockedBy = blockedBy;
    }

    public void setSelectedCardIndexesString(String selectedCardIndexesString) {
        if (!selectedCardIndexesString.equals("[]")) {
            selectedCardIndexes.clear();
            try {
                JSONArray jsonArray = new JSONArray(selectedCardIndexesString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    selectedCardIndexes.add(jsonArray.getInt(i));
                }
            } catch (JSONException e) {
                Log.e(TAG, "setSelectedCardIndexesString: " + e.getMessage());
                throw new JsonParsingException(e.getMessage());
            }
        }
    }

    public boolean hasSamePoints(Map<String, Integer> otherPoints) {
        boolean same = Objects.equals(points.get(player1), otherPoints.get(player1));
        if (!Objects.equals(points.get(player2), otherPoints.get(player2))) {
            same = false;
        }
        return same;
    }

    public void setPointsString(String pointsString) {
        if (player1 != null && player2 != null && !pointsString.equals("{}")) {
            try {
                JSONObject jsonObject = new JSONObject(pointsString);
                points.clear();

                for (java.util.Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String key = it.next();
                    int value = jsonObject.getInt(key);
                    points.put(key, value);
                }
            } catch (JSONException e) {
                Log.e(TAG, "setPointsString: " + e.getMessage());
                throw new JsonParsingException(e.getMessage());
            }
        }
    }

    private void createMultiplayerGame(JSONObject game) {
        try {
            setGameIdString(game.getString("gameId"));
            if (!game.getString("playerLeft").equals("null")) {
                setPlayerLeft(game.getBoolean("playerLeft"));
            }
            if (!game.getString("player1").equals("null")) {
                this.player1 = game.getString("player1");
            }

            if (!game.getString("player2").equals("null")) {
                this.player2 = game.getString("player2");
            }
            if (!game.getString("blockedBy").equals("null")) {
                this.blockedBy = game.getString("blockedBy");
            } else {
                this.blockedBy = null;
            }
            if (!game.getString("winner").equals("null")) {
                this.winner = game.getString("winner");
            }
            setNullCardIndexesString(game.getString("nullCardIndexes"));
            setBoardString(game.getString("board"));
            setSelectedCardIndexesString(game.getString("selectedCardIndexes"));
            setPointsString(game.getString("points"));
        } catch (JSONException e) {
            Log.e(TAG, "createMultiplayerGame: " + e.getMessage());
            throw new JsonParsingException(e.getMessage());
        }
    }
}
