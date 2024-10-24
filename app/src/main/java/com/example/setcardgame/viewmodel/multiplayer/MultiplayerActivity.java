package com.example.setcardgame.viewmodel.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.setcardgame.R;
import com.example.setcardgame.config.WebSocketClient;
import com.example.setcardgame.model.MultiplayerGame;
import com.example.setcardgame.model.Username;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;

public class MultiplayerActivity extends AppCompatActivity {

    private static final String TAG = "Multiplayer";
    private static final String PLAYER_ID = "playerId";
    private static final String GAME_ID = "gameId";
    private static final String SELECTED_CARD_INDEX = "selectedCardIndex";
    private static final String SELECT = "select";
    private final List<ImageView> boardIV = new ArrayList<>();
    private final List<Integer> selectedCardIds = new ArrayList<>();
    private TextView opponentPointTextView;
    private TextView ownPointTextView;
    private Button setBtn;
    private final String username = Username.getName();
    private int gameId;
    private MultiplayerGame game;
    private final Timer resetBackgroundTimer = new Timer();
    private Timer resetTimer;
    private final Timer punishTimer = new Timer();
    private boolean stopUserInteractions = false;
    private int resetInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        Intent mp = getIntent();
        gameId = Integer.parseInt(Objects.requireNonNull(mp.getStringExtra(GAME_ID)));
        setBtn = findViewById(R.id.callSETBtn);

        JSONObject jsonGameId = new JSONObject();
        try {
            jsonGameId.put(GAME_ID, gameId);
        } catch (JSONException e) {
            e.getMessage();
        }

        Disposable topic = WebSocketClient.mStompClient.topic("/topic/game-progress/" + gameId).subscribe(topicMessage -> {
            try {
                JSONObject msg = new JSONObject(topicMessage.getPayload());
                MultiplayerGame tempGame = new MultiplayerGame(msg);
                //player left
                if (tempGame.isPlayerLeft()) {
                    game = tempGame;
                    Log.d(TAG, "Other player left the game");
                    endGame();
                }
                Log.d(TAG, msg.toString());
                if (tempGame.getPlayer1() != null && tempGame.getPlayer2() != null) {
                    runOnUiThread(() -> {
                        //start game
                        if (game == null) {
                            game = new MultiplayerGame(msg);
                            startGame();
                            Log.d(TAG, "Game started with id: " + gameId);
                        } else {
                            //SET button press
                            if (tempGame.getBlockedBy() != null && tempGame.getBlockedBy().toString().equals(username) && tempGame.getSelectedCardIndexes().isEmpty()) {
                                Log.d(TAG, "my block");
                                try {
                                    game.setBlockedByString(msg.getString("blockedBy"));
                                    setBtn.setBackgroundTintList(ContextCompat.getColorStateList(MultiplayerActivity.this, R.color.green));
                                    switchBoardClicks(true);
                                } catch (JSONException e) {
                                    e.getMessage();
                                }
                            } else if (tempGame.getBlockedBy() != null && !tempGame.getBlockedBy().toString().equals(username) && tempGame.getSelectedCardIndexes().isEmpty()) {
                                Log.d(TAG, "opponent's block");
                                try {
                                    game.setBlockedByString(msg.getString("blockedBy"));
                                } catch (JSONException e) {
                                    e.getMessage();
                                }
                                setBtn.setEnabled(false);
                                setBtn.setBackgroundTintList(ContextCompat.getColorStateList(MultiplayerActivity.this, R.color.dark_red));
                            }

                            //opponent is selecting cards
                            if (tempGame.getBlockedBy() != null && !tempGame.getBlockedBy().toString().equals(username)) {
                                game.setSelectedCardIndexes(tempGame.getSelectedCardIndexes());
                                setSelectedCardsBackgroundForOpponent(game.getSelectedCardIndexes());
                            }

                            //3 cards have been selected
                            if (tempGame.getSelectedCardIndexes().size() == 3 && tempGame.getBlockedBy() == null) {
                                game.setSelectedCardIndexes(tempGame.getSelectedCardIndexes());
                                setSelectedCardsBackgroundForOpponent(game.getSelectedCardIndexes());

                                if (game.hasSamePoints(tempGame.getPoints())) {
                                    //wrong combo
                                    unCorrectSelectedCards(tempGame.getSelectedCardIndexes());

                                    game.clearSelectedCardIndexes();
                                    selectedCardIds.clear();
                                    resetCardBackgrounds();
                                    resetButtonAndCardClicks();

                                    if (game.getBlockedBy().toString().equals(username)) {
                                        resetButtonAndCardClicksOnError();
                                        punishPlayerError();
                                        Log.d(TAG, "set not found");
                                    } else {
                                        resetButtonAndCardClicks();
                                    }
                                    game.setBlockedBy(null);
                                } else {
                                    //right combo, board changed
                                    Log.d(TAG, "set found");
                                    correctSelectedCards(tempGame.getSelectedCardIndexes());
                                    game.setPoints(tempGame.getPoints());
                                    updatePointTextViews();
                                    game.setBoard(tempGame.getBoard());
                                    game.setNullCardIndexes(tempGame.getNullCardIndexes());

                                    for (int i = 0; game.getSelectedCardIndexes().size() > i; i++) {
                                        if (!game.getNullCardIndexes().isEmpty()) {
                                            boardIV.get(game.getSelectedCardIndexes().get(i)).setVisibility(View.INVISIBLE);
                                        } else {
                                            ImageView img = boardIV.get(game.getSelectedCardIndexes().get(i));
                                            int resImage = getResources().getIdentifier(game.getBoard().get(game.getSelectedCardIndexes().get(i)).toString(), "drawable", getPackageName());
                                            img.setImageResource(resImage);
                                            img.setContentDescription(game.getBoard().get(game.getSelectedCardIndexes().get(i)).toString());
                                        }
                                    }

                                    if (tempGame.getWinner() != null) {
                                        game.setWinner(tempGame.getWinner());
                                        Log.d(TAG, "Game ended with id: " + gameId);
                                        endGame();
                                    }

                                    resetButtonAndCardClicks();
                                    game.setBlockedBy(null);
                                    game.clearSelectedCardIndexes();
                                    selectedCardIds.clear();
                                    resetCardBackgrounds();
                                }
                            }
                            //time ran out. button has been reset
                            if (game.getBlockedBy() != null && tempGame.getSelectedCardIndexes().size() != 3 && tempGame.getBlockedBy() == null) {
                                game.clearSelectedCardIndexes();
                                selectedCardIds.clear();
                                resetCardBackgrounds();
                                if (game.getBlockedBy().toString().equals(username)) {
                                    resetButtonAndCardClicksOnError();
                                    punishPlayerError();
                                    Log.d(TAG, "punished");
                                } else {
                                    resetButtonAndCardClicks();
                                    Log.d(TAG, "not punished");
                                }
                                game.setBlockedBy(null);
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.getMessage();
            }
        }, throwable -> Log.d(TAG, "cannot create websocket"));
        WebSocketClient.compositeDisposable.add(topic);
        WebSocketClient.mStompClient.send("/app/start", jsonGameId.toString()).subscribe();
    }

    private void startGame() {
        boardIV.clear();
        selectedCardIds.clear();

        boardIV.add(findViewById(R.id.card0));
        boardIV.add(findViewById(R.id.card1));
        boardIV.add(findViewById(R.id.card2));
        boardIV.add(findViewById(R.id.card3));
        boardIV.add(findViewById(R.id.card4));
        boardIV.add(findViewById(R.id.card5));
        boardIV.add(findViewById(R.id.card6));
        boardIV.add(findViewById(R.id.card7));
        boardIV.add(findViewById(R.id.card8));

        for (int i = 0; boardIV.size() > i; i++) {
            ImageView img = boardIV.get(i);
            img.setEnabled(false);
            int resImage = getResources().getIdentifier(game.getBoard().get(i).toString(), "drawable", getPackageName());
            img.setImageResource(resImage);
            img.setContentDescription(game.getBoard().get(i).toString());
        }

        opponentPointTextView = findViewById(R.id.opponentPointTextView);
        opponentPointTextView.setText("0");
        ownPointTextView = findViewById(R.id.ownPointTextView);
        ownPointTextView.setText("0");
        setBtn = findViewById(R.id.callSETBtn);

        TableLayout tableLayout = findViewById(R.id.gameTableLayout);
        tableLayout.setVisibility(View.VISIBLE);
    }

    public void onSETBtnClick(View view) {

        JSONObject buttonPressJson = new JSONObject();
        try {
            buttonPressJson.put(GAME_ID, gameId);
            buttonPressJson.put(PLAYER_ID, username);
        } catch (JSONException e) {
            e.getMessage();
        }

        WebSocketClient.mStompClient.send("/app/gameplay/button", buttonPressJson.toString()).subscribe();

        //start timer for 3 sec, if it the player does not select 3 cards reset everything and punish them
        selectTimeCountDown(buttonPressJson);
        setBtn.setEnabled(false);
    }

    private void selectTimeCountDown(JSONObject buttonPressJson) {
        resetTimer = new Timer();
        resetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                WebSocketClient.mStompClient.send("/app/gameplay/button", buttonPressJson.toString()).subscribe();
            }
        }, 3000);
    }

    private void punishPlayerError() {
        punishTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (game.getBlockedBy() == null) {
                        resetButtonAndCardClicks();
                    }
                });
            }
        }, 5000);
    }

    private void resetButtonAndCardClicks() {
        switchBoardClicks(false);
        setBtn.setEnabled(true);
        setBtn.setBackgroundTintList(ContextCompat.getColorStateList(MultiplayerActivity.this, R.color.blue));
    }

    private void resetButtonAndCardClicksOnError() {
        switchBoardClicks(false);
        setBtn.setEnabled(false);
        setBtn.setBackgroundTintList(ContextCompat.getColorStateList(MultiplayerActivity.this, R.color.dark_red));
    }

    private void updatePointTextViews() {
        if (game.getPlayer1().toString().equals(username)) {
            ownPointTextView.setText(String.valueOf(game.getPoints().get(game.getPlayer1())));
            opponentPointTextView.setText(String.valueOf(game.getPoints().get(game.getPlayer2())));
        } else {
            ownPointTextView.setText(String.valueOf(game.getPoints().get(game.getPlayer2())));
            opponentPointTextView.setText(String.valueOf(game.getPoints().get(game.getPlayer1())));
        }
    }

    public void onCardClick(View view) {
        if (game.getBlockedBy().toString().equals(username) && selectedCardIds.size() < 3) {
            if (!selectedCardIds.contains(view.getId())) {
                resetInt++;
                if (resetInt == 3) {
                    resetTimer.cancel();
                    resetTimer.purge();
                    resetInt = 0;
                }
                boolean found = false;
                int counter = 0;
                while (!found && boardIV.size() > counter) {
                    if (boardIV.get(counter).getId() == view.getId()) {
                        found = true;
                        view.setBackgroundResource(R.drawable.card_background_selected);
                        selectedCardIds.add(view.getId());

                        JSONObject gameplayJson = new JSONObject();
                        try {
                            gameplayJson.put(GAME_ID, gameId);
                            gameplayJson.put(PLAYER_ID, username);
                            gameplayJson.put(SELECT, true);
                            gameplayJson.put(SELECTED_CARD_INDEX, counter);
                        } catch (JSONException e) {
                            e.getMessage();
                        }

                        WebSocketClient.mStompClient.send("/app/gameplay", gameplayJson.toString()).subscribe();
                    }
                    counter++;
                }
            } else {
                resetInt--;
                for (int i = 0; boardIV.size() > i; i++) {
                    if (boardIV.get(i).getId() == view.getId()) {
                        boardIV.get(i).setBackgroundResource(R.color.trans);

                        JSONObject gameplayJson = new JSONObject();
                        try {
                            gameplayJson.put(GAME_ID, gameId);
                            gameplayJson.put(PLAYER_ID, username);
                            gameplayJson.put(SELECT, false);
                            gameplayJson.put(SELECTED_CARD_INDEX, i);
                        } catch (JSONException e) {
                            e.getMessage();
                        }

                        WebSocketClient.mStompClient.send("/app/gameplay", gameplayJson.toString()).subscribe();
                    }
                }
                selectedCardIds.remove((Integer) view.getId());
            }
        }
    }

    private void correctSelectedCards(List<Integer> indexes) {
        for (int i = 0; indexes.size() > i; i++) {
            boardIV.get(indexes.get(i)).setBackgroundResource(R.drawable.card_background_right);
        }
        stopUserInteractions = true;
    }

    private void unCorrectSelectedCards(List<Integer> indexes) {
        for (int i = 0; indexes.size() > i; i++) {
            boardIV.get(indexes.get(i)).setBackgroundResource(R.drawable.card_background_wrong);
        }
        stopUserInteractions = true;
    }

    private void setSelectedCardsBackgroundForOpponent(List<Integer> indexes) {
        for (int i = 0; boardIV.size() > i; i++) {
            boardIV.get(i).setBackgroundResource(R.color.trans);
        }
        for (int i = 0; indexes.size() > i; i++) {
            boardIV.get(indexes.get(i)).setBackgroundResource(R.drawable.card_background_selected);
            selectedCardIds.add(boardIV.get(indexes.get(i)).getId());
        }
    }

    private void resetCardBackgrounds() {
        resetBackgroundTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; boardIV.size() > i; i++) {
                    boardIV.get(i).setBackgroundResource(R.color.trans);
                }
                stopUserInteractions = false;
            }
        }, 300);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (stopUserInteractions) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    private void switchBoardClicks(boolean on) {
        for (int i = 0; boardIV.size() > i; i++) {
            boardIV.get(i).setEnabled(on);
        }
    }

    private void endGame() {
        Intent mpes = new Intent(this, MultiplayerEndScreenActivity.class);
        mpes.putExtra("opponentScore", opponentPointTextView.getText());
        mpes.putExtra("ownScore", ownPointTextView.getText());
        mpes.putExtra("winner", game.getWinner().toString());
        if (game.getPlayer1() != null && game.getPlayer2() != null) {
            if (game.getPlayer1().toString().equals(username)) {
                mpes.putExtra("opponent", game.getPlayer2().toString());
            } else {
                mpes.putExtra("opponent", game.getPlayer1().toString());
            }
        }

        startActivity(mpes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketClient.disconnectWebSocket();
    }
}