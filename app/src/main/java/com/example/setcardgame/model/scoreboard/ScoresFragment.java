package com.example.setcardgame.model.scoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setcardgame.R;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScoresFragment extends Fragment {

    private final List<Scoreboard> scoreList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerViewScoreAdapter adapter = new RecyclerViewScoreAdapter(getContext(), scoreList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}
