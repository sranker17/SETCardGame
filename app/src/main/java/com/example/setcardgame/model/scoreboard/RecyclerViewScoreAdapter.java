package com.example.setcardgame.model.scoreboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setcardgame.R;

import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RecyclerViewScoreAdapter extends RecyclerView.Adapter<RecyclerViewScoreAdapter.ViewHolder> {

    private final Context context;
    private final List<Scoreboard> list;

    @NonNull
    @Override
    public RecyclerViewScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewScoreAdapter.ViewHolder holder, int position) {
        String placementContent = String.valueOf(position + 1);
        String pointsContent = String.format(Locale.US, "%s: %d", context.getString(R.string.pointsText), list.get(position).getScore());

        int time = list.get(position).getTime();
        int seconds = time % 60;
        int minutes = time / 60;
        String timeContent = String.format(Locale.US, "%d:%02d", minutes, seconds);

        boolean isOwnScore = Boolean.TRUE.equals(list.get(position).getUserScore());
        holder.setData(placementContent, pointsContent, timeContent, isOwnScore);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView placement;
        private final TextView points;
        private final TextView time;
        private final RelativeLayout scoreCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            placement = itemView.findViewById(R.id.placement);
            points = itemView.findViewById(R.id.pointsView);
            time = itemView.findViewById(R.id.timeView);
            scoreCard = itemView.findViewById(R.id.scoreCard);
        }

        public void setData(String placementContent, String pointsContent, String timeContent, boolean isOwnScore) {
            placement.setText(placementContent);
            points.setText(pointsContent);
            time.setText(timeContent);
            
            if (isOwnScore) {
                // Change background to highlight own score
                scoreCard.setBackgroundResource(R.drawable.own_score_background);
            } else {
                // Use normal background
                scoreCard.setBackgroundResource(R.drawable.normal_score_background);
            }
        }
    }
}
