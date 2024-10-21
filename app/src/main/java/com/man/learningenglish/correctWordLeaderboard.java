// correctWordLeaderboard.java
package com.man.learningenglish;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.man.learningenglish.adapter.LeaderboardAdapter;
import com.man.learningenglish.firebase.FirebaseHelper;
import com.man.learningenglish.models.Leaderboard;

public class correctWordLeaderboard extends AppCompatActivity implements FirebaseHelper.LeaderboardCallback {
    private TextView totalUsers, myImgText, myBestTime, myRank;
    private RecyclerView usersView;
    private LeaderboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word_leaderboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupClickListeners();

        // Initialize Firebase helper after setting up the adapter
        FirebaseHelper firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.getTopUsers();
        myBestTime.setText("Time: " + formatTime(FirebaseHelper.myLeaderboard.getTime()));
        myRank.setText("Rank: " + FirebaseHelper.myLeaderboard.getRank());
        updateUI();

    }

    private void initViews() {
        totalUsers = findViewById(R.id.total_users);
        myImgText = findViewById(R.id.img_text);
        myBestTime = findViewById(R.id.my_best_time);
        myRank = findViewById(R.id.my_rank);
        usersView = findViewById(R.id.leaderboardRecyclerView);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        usersView.setLayoutManager(layoutManager);
        adapter = new LeaderboardAdapter(); // Create adapter with empty list
        usersView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.buttonclick);
        findViewById(R.id.Back).setOnClickListener(v -> {
            mediaPlayer.start();
            startActivity(new Intent(correctWordLeaderboard.this, correctword.class));
            finish();
        });
    }

    private void updateUI() {
        if (FirebaseHelper.myLeaderboard != null) {
            String firstLetter = FirebaseAuth.getInstance().getCurrentUser() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, 1).toUpperCase() : "?";
            myImgText.setText(firstLetter);
            myBestTime.setText("Time: " + formatTime(FirebaseHelper.myLeaderboard.getTime()));
            myRank.setText("Rank: " + FirebaseHelper.myLeaderboard.getRank());
        }

        totalUsers.setText("Top Players");
        adapter.updateData(FirebaseHelper.leaderboardList);
    }

    private String formatTime(long timeInMillis) {
        int minutes = (int) (timeInMillis / 60000);
        int seconds = (int) ((timeInMillis % 60000) / 1000);
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onLeaderboardUpdated() {
        runOnUiThread(this::updateUI);
    }

    @Override
    public void onError(Exception e) {
        runOnUiThread(() ->
                Toast.makeText(this, "Error loading leaderboard", Toast.LENGTH_SHORT).show()
        );
    }
}
