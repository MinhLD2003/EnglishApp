package com.man.learningenglish;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class correctWordResult extends AppCompatActivity {
    Button PlayAgain, TroVe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        PlayAgain = findViewById(R.id.PlayAgain);
        TroVe = findViewById(R.id.Back);
        TextView resultTextView = findViewById(R.id.resultTextView);
        TextView bestTimeTodayTextView = findViewById(R.id.bestTimeTodayTextView);
        TextView bestTimeAllTimeTextView = findViewById(R.id.bestTimeAllTimeTextView);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.buttonclick);

        TroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent = new Intent(correctWordResult.this, correctword.class);
                startActivity(intent);
                finish();
            } });

        PlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent1 = new Intent(correctWordResult.this, entername.class);
                startActivity(intent1);
                finish();
            }
        });

        // Retrieve the times from the Intent
        Intent intent = getIntent();
        long currentTime = intent.getLongExtra("currentTime", 0);
        long bestTimeToday = intent.getLongExtra("bestTimeToday", 0);
        long bestTimeAllTime = intent.getLongExtra("bestTimeAllTime", 0);

        // Format the times into a readable format (milliseconds to seconds or minutes)
        String formattedCurrentTime = formatTime(currentTime);
        String formattedBestTimeToday = formatTime(bestTimeToday);
        String formattedBestTimeAllTime = formatTime(bestTimeAllTime);

        // Set the text views
        resultTextView.setText("Time Taken: " + formattedCurrentTime);
        bestTimeTodayTextView.setText("Best Time Today: " + formattedBestTimeToday);
        bestTimeAllTimeTextView.setText("Best Time of All Time: " + formattedBestTimeAllTime);
    }

    private String formatTime(long timeInMillis) {
        long seconds = (timeInMillis / 1000) % 60;
        long minutes = (timeInMillis / 1000) / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}