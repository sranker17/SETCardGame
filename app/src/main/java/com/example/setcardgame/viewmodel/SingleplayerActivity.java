package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Difficulty;
import com.example.setcardgame.model.SingleplayerGame;
import com.example.setcardgame.model.card.Card;
import com.example.setcardgame.model.card.Color;
import com.example.setcardgame.model.card.Quantity;
import com.example.setcardgame.model.card.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SingleplayerActivity extends AppCompatActivity {

    private final List<ImageView> board = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private final List<Card> boardCards = new ArrayList<>();
    private final List<Card> selectedCards = new ArrayList<>();
    private final List<Integer> selectedCardIds = new ArrayList<>();
    private final Timer resetBackgroundTimer = new Timer();
    private TextView pointTextView;
    private TextView timerTextView;
    private Difficulty difficulty = Difficulty.NORMAL;
    private Timer timer;
    private TimerTask timerTask;
    private Double time = 0.0;
    private boolean stopUserInteractions = false;
    private final SingleplayerGame game = new SingleplayerGame();

    private static final String DIFF_MODE = "diffMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        Intent sp = getIntent();
        if (!Objects.requireNonNull(sp.getStringExtra(DIFF_MODE)).isEmpty()) {
            difficulty = Difficulty.getDifficultyFromString(Objects.requireNonNull(sp.getStringExtra(DIFF_MODE)));
        }

        startGame();
        timer = new Timer();
        startTimer();
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    time++;
                    int timeInInt = getTimer();
                    int seconds = timeInInt % 60;
                    int minutes = timeInInt / 60;
                    timerTextView.setText(String.format("%d:%02d", minutes, seconds));
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private int getTimer() {
        int rounded = (int) Math.round(time);

        return (rounded % 86400) % 3600;
    }

    private void startGame() {
        board.clear();
        boardCards.clear();
        cards.clear();
        selectedCards.clear();
        selectedCardIds.clear();

        board.add(findViewById(R.id.card0));
        board.add(findViewById(R.id.card1));
        board.add(findViewById(R.id.card2));
        board.add(findViewById(R.id.card3));
        board.add(findViewById(R.id.card4));
        board.add(findViewById(R.id.card5));
        board.add(findViewById(R.id.card6));
        board.add(findViewById(R.id.card7));
        board.add(findViewById(R.id.card8));


        if (difficulty == Difficulty.NORMAL) {
            TableLayout tableLayout = findViewById(R.id.gameTableLayout);
            TableRow lastTableRow = findViewById(R.id.tableRow3);
            tableLayout.removeView(lastTableRow);
        }

        if (difficulty == Difficulty.EASY) {
            board.add(findViewById(R.id.card9));
            board.add(findViewById(R.id.card10));
            board.add(findViewById(R.id.card11));

            if (getScreenSizeInInches() < 5.3) {
                ImageView card = findViewById(R.id.card8);
                ViewGroup.LayoutParams params = card.getLayoutParams();
                params.height *= 0.8;
                params.width *= 0.8;

                for (ImageView imageViewCards : board) {
                    imageViewCards.setLayoutParams(params);
                }
            }
        }

        do {
            cards.clear();
            for (Color color : Color.values()) {
                for (Shape shape : Shape.values()) {
                    for (Quantity quantity : Quantity.values()) {
                        cards.add(new Card(color, shape, quantity));
                    }
                }
            }

            Collections.shuffle(cards);

            for (int i = 0; board.size() > i; i++) {
                ImageView img = board.get(i);
                int resImage = getResources().getIdentifier(cards.get(0).toString(), "drawable", getPackageName());
                img.setImageResource(resImage);
                img.setContentDescription(cards.get(0).toString());
                boardCards.add(cards.get(0));
                cards.remove(0);
            }
        } while (!game.hasSet(boardCards, board));

        pointTextView = findViewById(R.id.opponentPointTextView);
        timerTextView = findViewById(R.id.timerTextView);
        pointTextView.setText("0");
        timerTextView.setText("0:00");
    }

    public void onCardClick(View view) {
        if (!selectedCardIds.contains(view.getId())) {
            boolean found = false;
            int counter = 0;
            while (!found && board.size() > counter) {
                if (board.get(counter).getId() == view.getId()) {
                    found = true;
                    view.setBackgroundResource(R.drawable.card_background_selected);
                    selectedCardIds.add(view.getId());
                    selectedCards.add(boardCards.get(counter));
                }
                counter++;
            }

            if (selectedCardIds.size() == 3) {
                if (game.hasSet(selectedCards, board)) {
                    for (int i = 0; board.size() > i; i++) {
                        if (board.get(i).getId() == selectedCardIds.get(0)
                                || board.get(i).getId() == selectedCardIds.get(1)
                                || board.get(i).getId() == selectedCardIds.get(2)) {
                            board.get(i).setBackgroundResource(R.drawable.card_background_right);
                        }
                    }
                    int point = Integer.parseInt((String) pointTextView.getText());
                    pointTextView.setText(String.valueOf(++point));
                    stopUserInteractions = true;
                    removeCardsFromBoard();

                    if (game.isGameOver(board) || !game.hasSet(boardCards, board)) {
                        timerTask.cancel();
                        endGame();
                    }

                } else {
                    for (int i = 0; board.size() > i; i++) {
                        if (board.get(i).getId() == selectedCardIds.get(0)
                                || board.get(i).getId() == selectedCardIds.get(1)
                                || board.get(i).getId() == selectedCardIds.get(2)) {
                            board.get(i).setBackgroundResource(R.drawable.card_background_wrong);
                        }
                    }
                    stopUserInteractions = true;
                }
                selectedCardIds.clear();
                selectedCards.clear();
                resetCardBackgrounds();
            }
        } else {
            for (int i = 0; board.size() > i; i++) {
                if (board.get(i).getId() == view.getId()) {
                    board.get(i).setBackgroundResource(R.color.trans);
                }
            }
            selectedCards.remove(selectedCardIds.indexOf(view.getId()));
            selectedCardIds.remove((Integer) view.getId());
        }

    }

    private void resetCardBackgrounds() {
        resetBackgroundTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; board.size() > i; i++) {
                    board.get(i).setBackgroundResource(R.color.trans);
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

    private void removeCardsFromBoard() {
        for (int i = 0; board.size() > i; i++) {
            ImageView img = board.get(i);
            if (img.getId() == selectedCardIds.get(0)
                    || img.getId() == selectedCardIds.get(1)
                    || img.getId() == selectedCardIds.get(2)) {
                if (!cards.isEmpty()) {
                    int resImage = getResources().getIdentifier(cards.get(0).toString(), "drawable", getPackageName());
                    img.setImageResource(resImage);
                    img.setContentDescription(cards.get(0).toString());
                    boardCards.set(i, cards.get(0));
                    cards.remove(0);
                } else {
                    img.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void endGame() {
        Intent egs = new Intent(this, EndGameScreenActivity.class);
        egs.putExtra("time", "" + getTimer());
        egs.putExtra("score", pointTextView.getText());
        egs.putExtra("diff", difficulty.toString());
        startActivity(egs);
    }

    private double getScreenSizeInInches() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double mWidthPixels = dm.widthPixels;
        double mHeightPixels = dm.heightPixels;
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }
}