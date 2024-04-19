package com.example.setcardgame.model;

import com.example.setcardgame.model.card.Card;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MultiplayerGame {

    private int gameId;
    private UUID player1;
    private UUID player2;
    private List<Card> board = new ArrayList<>();
    private UUID winner;
    private UUID blockedBy;
    private List<Integer> selectedCardIndexes = new ArrayList<>();
    private Map<UUID, Integer> points = new HashMap<>();
    private List<Integer> nullCardIndexes = new ArrayList<>();
    private boolean playerLeft;

    public MultiplayerGame(JSONObject game) {
        createMultiplayerGame(game);
    }

    public MultiplayerGame() {
    }

    public List<Integer> getNullCardIndexes() {
        return nullCardIndexes;
    }

    public void setNullCardIndexes(List<Integer> nullCardIndexes) {
        this.nullCardIndexes = nullCardIndexes;
    }

    public void setNullCardIndexesString(String nullCardIndexesString) {
        if (!nullCardIndexesString.equals("[]")) {
            nullCardIndexes.clear();
            nullCardIndexesString = nullCardIndexesString.replace('[', ' ');
            nullCardIndexesString = nullCardIndexesString.replace(']', ' ');
            nullCardIndexesString = nullCardIndexesString.trim();
            String[] nullCardIndexesStrings = nullCardIndexesString.split(",");
            for (String cardIndexesString : nullCardIndexesStrings) {
                nullCardIndexes.add(Integer.parseInt(cardIndexesString));
            }
        }
    }

    public void clearSelectedCardIndexes() {
        selectedCardIndexes.clear();
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setGameIdString(String gameId) {
        this.gameId = Integer.parseInt(gameId);
    }

    public UUID getPlayer1() {
        return player1;
    }

    public void setPlayer1(UUID player1) {
        this.player1 = player1;
    }

    public void setPlayer1String(String player1) {
        this.player1 = UUID.fromString(player1);
    }

    public void setPlayer2String(String player2) {
        this.player2 = UUID.fromString(player2);
    }

    public UUID getPlayer2() {
        return player2;
    }

    public void setPlayer2(UUID player2) {
        this.player2 = player2;
    }

    public List<Card> getBoard() {
        return board;
    }

    public void setBoard(List<Card> board) {
        this.board = board;
    }

    public boolean isPlayerLeft() {
        return playerLeft;
    }

    public void setPlayerLeft(boolean playerLeft) {
        this.playerLeft = playerLeft;
    }

    public void setBoardString(String boardString) {
        if (!boardString.equals("[]")) {
            board.clear();
            boardString = boardString.replace('"', ' ');
            boardString = boardString.replace('[', ' ');
            boardString = boardString.replace(']', ' ');
            boardString = boardString.replace('{', ' ');
            boardString = boardString.replace('}', ' ');
            boardString = boardString.replace(':', ' ');
            boardString = boardString.replace("color", "");
            boardString = boardString.replace("shape", "");
            boardString = boardString.replace("quantity", "");
            String[] words = boardString.split(",");

            for (int i = 0; words.length > i; i++) {
                words[i] = words[i].trim();
            }

            int i = 0;
            while (words.length > i) {
                if (!words[i].equals("null")) {
                    Card newCard = new Card(words[i++], words[i++], words[i++]);
                    board.add(newCard);
                } else {
                    board.add(null);
                }
            }
        }
    }

    public UUID getWinner() {
        return winner;
    }

    public void setWinner(UUID winner) {
        this.winner = winner;
    }

    public void setWinnerString(String winner) {
        this.winner = UUID.fromString(winner);
    }

    public UUID getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(UUID blockedBy) {
        this.blockedBy = blockedBy;
    }

    public void setBlockedByString(String blockedBy) {
        this.blockedBy = UUID.fromString(blockedBy);
    }

    public List<Integer> getSelectedCardIndexes() {
        return selectedCardIndexes;
    }

    public void setSelectedCardIndexes(List<Integer> selectedCardIndexes) {
        this.selectedCardIndexes = selectedCardIndexes;
    }

    public void setSelectedCardIndexesString(String selectedCardIndexesString) {
        if (!selectedCardIndexesString.equals("[]")) {
            selectedCardIndexes.clear();
            selectedCardIndexesString = selectedCardIndexesString.replace('[', ' ');
            selectedCardIndexesString = selectedCardIndexesString.replace(']', ' ');
            selectedCardIndexesString = selectedCardIndexesString.trim();
            String[] selectedCardIndexesStrings = selectedCardIndexesString.split(",");
            for (String cardIndexesString : selectedCardIndexesStrings) {
                selectedCardIndexes.add(Integer.parseInt(cardIndexesString));
            }
        }
    }

    public Map<UUID, Integer> getPoints() {
        return points;
    }

    public void setPoints(Map<UUID, Integer> points) {
        this.points = points;
    }

    public boolean hasSamePoints(Map<UUID, Integer> otherPoints) {
        boolean same = Objects.equals(points.get(player1), otherPoints.get(player1));
        if (!Objects.equals(points.get(player2), otherPoints.get(player2))) {
            same = false;
        }
        return same;
    }

    public void setPointsString(String pointsString) {
        if (player1 != null && player2 != null && !pointsString.equals("{}")) {

            pointsString = pointsString.replace('"', ' ');
            pointsString = pointsString.replace('{', ' ');
            pointsString = pointsString.replace('}', ' ');
            pointsString = pointsString.replace(':', ',');
            String[] pointWords = pointsString.split(",");

            for (int i = 0; pointWords.length > i; i++) {
                pointWords[i] = pointWords[i].trim();
            }

            int i = 0;
            while (pointWords.length > i) {
                points.put(UUID.fromString(pointWords[i++]), Integer.parseInt(pointWords[i++]));
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
                this.player1 = UUID.fromString(game.getString("player1"));
            }

            if (!game.getString("player2").equals("null")) {
                this.player2 = UUID.fromString(game.getString("player2"));
            }
            if (!game.getString("blockedBy").equals("null")) {
                this.blockedBy = UUID.fromString(game.getString("blockedBy"));
            } else {
                this.blockedBy = null;
            }
            if (!game.getString("winner").equals("null")) {
                this.winner = UUID.fromString(game.getString("winner"));
            }
            setNullCardIndexesString(game.getString("nullCardIndexes"));
            setBoardString(game.getString("board"));
            setSelectedCardIndexesString(game.getString("selectedCardIndexes"));
            setPointsString(game.getString("points"));
        } catch (JSONException e) {
            e.getMessage();
        }
    }
}
