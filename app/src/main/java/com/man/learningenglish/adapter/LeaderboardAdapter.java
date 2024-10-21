// LeaderboardAdapter.java
package com.man.learningenglish.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.man.learningenglish.R;
import com.man.learningenglish.models.Leaderboard;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<Leaderboard> usersList;

    public LeaderboardAdapter() {
        this.usersList = new ArrayList<>(); // Initialize with empty list
    }

    public void updateData(List<Leaderboard> newList) {
        this.usersList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        Leaderboard user = usersList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return usersList != null ? usersList.size() : 0;
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, bestTimeText, rankText;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username);
            bestTimeText = itemView.findViewById(R.id.best_time);
            rankText = itemView.findViewById(R.id.rank);
        }

        public void bind(Leaderboard user) {
            if (user != null) {
                usernameText.setText(user.getName());
                bestTimeText.setText(String.format("Time: %d:%02d",
                        user.getTime() / 60000,
                        (user.getTime() % 60000) / 1000));
                rankText.setText("Rank: " + user.getRank());
            }
        }
    }
}