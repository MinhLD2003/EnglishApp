package com.man.learningenglish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Chronometer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.man.learningenglish.models.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class correctWordTest extends AppCompatActivity {
    TextView shuffledWordTextView;
    Button backButton, nextButton;
    EditText userAnswerEditText;
    List<Word> randomWords;

    private long startTime, totalTimeTaken;
    private int currentWordIndex = 0;
    private int correctAnswers = 0;

    private Chronometer chronometer;

    private static final String PREFS_NAME = "QuizPrefs";
    private static final String BEST_TIME_KEY = "BestTimeOfTheDay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_correct_word_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Calling Views
        shuffledWordTextView = findViewById(R.id.Question);
        chronometer = findViewById(R.id.chronometer);
        backButton = findViewById(R.id.Back);
        nextButton = findViewById(R.id.Answer);
        userAnswerEditText = findViewById(R.id.Answerbox);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.buttonclick);
        
        // Set Intent on Back Button
        backButton.setOnClickListener(v -> {
            mediaPlayer.start();
            Intent intent = new Intent(correctWordTest.this, correctword.class);
            startActivity(intent);
            finish();
        });

        //get 10 random words
        try {
            List<Word> wordsList = readWordsFromFile();
            randomWords = getRandomWords(wordsList);

            // Output or use the random words
            for (Word word : randomWords) {
                System.out.println("Original: " + word.getOriginal() + ", Shuffled: " + word.getShuffled());
            }
            startTimer();
            showNextShuffledWord();

        } catch (IOException e) {
            System.out.println("Error reading words from file: " + e.toString());
        }

        nextButton.setOnClickListener(v -> checkAnswer());

    }

    private void startTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime()); // Set the chronometer's base time
        chronometer.start();
        startTime = System.currentTimeMillis();
    }

    private void stopTimer() {
        chronometer.stop();
        totalTimeTaken = System.currentTimeMillis() - startTime; // Calculate the total time in milliseconds
    }

    private List<Word> readWordsFromFile() throws IOException {
        List<Word> words = new ArrayList<>();
        InputStream is = getResources().openRawResource(R.raw.question); // Place question.txt in res/raw folder
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", 2);
            if (parts.length == 2) {
                String originalWord = parts[1].trim();
                String shuffledWord = shuffleCharacters(originalWord);
                words.add(new Word(originalWord, shuffledWord));
            }
        }
        reader.close();
        return words;
    }

    // Get a random selection of words
    private List<Word> getRandomWords(List<Word> words) {
        Collections.shuffle(words);
        return words.subList(0, 10);
    }

    // Shuffle characters of a word and return it as a slash-separated string
    private String shuffleCharacters(String word) {
        List<Character> characters = new ArrayList<>();
        for (char c : word.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);

        StringBuilder shuffledWord = new StringBuilder();
        for (int i = 0; i < characters.size(); i++) {
            shuffledWord.append(characters.get(i));
            if (i < characters.size() - 1) {
                shuffledWord.append("/");
            }
        }
        return shuffledWord.toString();
    }

    private void checkAnswer() {
        String userAnswer = userAnswerEditText.getText().toString().trim();
        Word currentWord = randomWords.get(currentWordIndex);

        if (userAnswer.equalsIgnoreCase(currentWord.getOriginal())) {
            correctAnswers++;
            Toast.makeText(correctWordTest.this, "Correct!", Toast.LENGTH_SHORT).show();
            currentWordIndex++;
            userAnswerEditText.setText(""); // Clear the input field
            showNextShuffledWord();
        } else {
            Toast.makeText(correctWordTest.this, "Incorrect, try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNextShuffledWord() {
        if (currentWordIndex < randomWords.size()) {
            Word currentWord = randomWords.get(currentWordIndex);
            shuffledWordTextView.setText(currentWord.getShuffled());
        } else {
            // All words have been answered
            Toast.makeText(correctWordTest.this, "Quiz complete! You answered " + correctAnswers + " correctly.", Toast.LENGTH_LONG).show();
            stopTimer();
            checkBestTime(totalTimeTaken);

        }
    }

    private void checkBestTime(long currentTime) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long bestTime = prefs.getLong(BEST_TIME_KEY, Long.MAX_VALUE); // Retrieve the best time, default to a large value

        if (currentTime < bestTime) {
            // Save the new best time if the current time is faster
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(BEST_TIME_KEY, currentTime);
            editor.apply();

            Toast.makeText(this, "New Best Time: " + (currentTime / 1000) + " seconds!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Total Time: " + (currentTime / 1000) + " seconds.", Toast.LENGTH_LONG).show();
        }
    }
}