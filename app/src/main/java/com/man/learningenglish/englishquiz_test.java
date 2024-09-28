package com.man.learningenglish;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

class questionnaire {
    public String ID;
    public String AnswerA, AnswerB, AnswerC, AnswerD, Answer;
}

public class englishquiz_test extends AppCompatActivity {
    TextView KetQua, CauHoi, ThoiGian;
    ImageView HinhAnh;
    RadioGroup RG;
    Button TraLoi, TroGiup, BoQua, KetThuc;
    RadioButton A, B, C, D;
    int pos = 0;
    int kq = 0;
    CountDownTimer Time;
    public ArrayList<questionnaire> list = new ArrayList<>();
    public ArrayList<Question> PList = new ArrayList<>();

    public void countdown() {
        Time = new CountDownTimer(21000, 1000) {

            public void onTick(long millisUntilFinished) {
                ThoiGian.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                pos++;
                if (pos >= list.size()) {
                    navigateToResult();
                } else {
                    Display(pos);
                }
            }
        }.start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.englishquiz_test);

        CauHoi = findViewById(R.id.Question);
        KetQua = findViewById(R.id.Result);
        RG = findViewById(R.id.RadioGroup);
        A = findViewById(R.id.RdbA);
        B = findViewById(R.id.RdbB);
        C = findViewById(R.id.RdbC);
        D = findViewById(R.id.RdbD);
        TraLoi = findViewById(R.id.Answer);
        HinhAnh = findViewById(R.id.QuestionPicture);
        TroGiup = findViewById(R.id.Help);
        BoQua = findViewById(R.id.Skip);
        ThoiGian = findViewById(R.id.Time);
        KetThuc = findViewById(R.id.End);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.buttonclick);

        AddQuestionFromFileTXT();
        CreateQuestion();
        Display(pos);

        // End quiz and return to the main screen
        KetThuc.setOnClickListener(v -> {
            Intent intent = new Intent(englishquiz_test.this, englishquiz.class);
            startActivity(intent);
            finish();
        });

        // Help button logic
        TroGiup.setOnClickListener(v -> {
            mediaPlayer.start();
            String correctAnswer = list.get(pos).Answer;
            helpLogic(correctAnswer);
            TroGiup.setVisibility(View.INVISIBLE);
        });

        // Skip question
        BoQua.setOnClickListener(v -> {
            Time.cancel();
            mediaPlayer.start();
            pos++;
            if (pos < list.size()) {
                Display(pos);
                BoQua.setVisibility(View.INVISIBLE);
            } else {
                navigateToResult();
            }
        });

        // Submit answer logic
        TraLoi.setOnClickListener(v -> {
            Time.cancel();
            mediaPlayer.start();
            checkAnswer();
            pos++;
            if (pos >= list.size()) {
                navigateToResult();
            } else {
                Display(pos);
            }
        });
    }

    void Display(int i) {
        if (list.isEmpty()) {
            // Handle the case where the list is empty, e.g., show an error message
            Intent intent = new Intent(englishquiz_test.this, englishquiz.class);
            startActivity(intent);
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
            return;
        }
        countdown();
        int resID = getResources().getIdentifier(list.get(i).ID, "drawable", getPackageName());
        HinhAnh.setImageResource(resID);
        A.setText(list.get(i).AnswerA);
        B.setText(list.get(i).AnswerB);
        C.setText(list.get(i).AnswerC);
        D.setText(list.get(i).AnswerD);
        KetQua.setText("Correct answers: " + kq);
        RG.clearCheck();
        A.setVisibility(View.VISIBLE);
        B.setVisibility(View.VISIBLE);
        C.setVisibility(View.VISIBLE);
        D.setVisibility(View.VISIBLE);
    }

    public void AddQuestionFromFileTXT() {
        try {
            String splitBy = ",";
            FileInputStream in = this.openFileInput("Question.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(splitBy);
                PList.add(new Question(value[1], Integer.parseInt(value[0])));
                // Log questions being loaded
                System.out.println("Loaded Question: " + value[1]);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading questions: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void CreateQuestion() {
        Intent callerIntent = getIntent();
        Bundle packageFromCaller = callerIntent.getBundleExtra("bundle");
        if (packageFromCaller != null) {
            int number = packageFromCaller.getInt("number");
            for (int i = 0; i < number; i++) {
                questionnaire Q = generateRandomQuestion();
                list.add(Q);
            }
        } else {
            Toast.makeText(this, "Bundle is null", Toast.LENGTH_SHORT).show();
        }
    }

    public questionnaire generateRandomQuestion() {
        questionnaire Q = new questionnaire();
        Random generator = new Random();

        // Generate four random answers ensuring they are unique
        Q.AnswerA = PList.get(generator.nextInt(50)).getName();
        do {
            Q.AnswerB = PList.get(generator.nextInt(50)).getName();
        } while (Q.AnswerB.equals(Q.AnswerA));

        do {
            Q.AnswerC = PList.get(generator.nextInt(50)).getName();
        } while (Q.AnswerC.equals(Q.AnswerB) || Q.AnswerC.equals(Q.AnswerA));

        do {
            Q.AnswerD = PList.get(generator.nextInt(50)).getName();
        } while (Q.AnswerD.equals(Q.AnswerC) || Q.AnswerD.equals(Q.AnswerB) || Q.AnswerD.equals(Q.AnswerA));

        // Randomly pick a correct answer
        int value = generator.nextInt(4);
        switch (value) {
            case 0:
                Q.Answer = "A";
                break;
            case 1:
                Q.Answer = "B";
                break;
            case 2:
                Q.Answer = "C";
                break;
            case 3:
                Q.Answer = "D";
                break;
        }
        Q.ID = "a" + PList.get(generator.nextInt(50)).getId();
        return Q;
    }

    // Navigation to the result screen
    public void navigateToResult() {
        Intent callerIntent = getIntent();
        Bundle packageFromCaller = callerIntent.getBundleExtra("bundle");
        String name = packageFromCaller.getString("name");

        Intent intent = new Intent(englishquiz_test.this, englishquiz_result.class);
        Bundle bundle = new Bundle();
        bundle.putInt("kq", kq);
        bundle.putInt("num", pos);
        bundle.putString("name", name);
        intent.putExtra("package", bundle);
        startActivity(intent);
        finish();
    }

    public void checkAnswer() {
        int idCheck = RG.getCheckedRadioButtonId();
        switch (idCheck) {
            case R.id.RdbA:
                if (list.get(pos).Answer.equals("A")) kq++;
                break;
            case R.id.RdbB:
                if (list.get(pos).Answer.equals("B")) kq++;
                break;
            case R.id.RdbC:
                if (list.get(pos).Answer.equals("C")) kq++;
                break;
            case R.id.RdbD:
                if (list.get(pos).Answer.equals("D")) kq++;
                break;
        }
    }

    // Logic for the help button (removes two incorrect answers)
    public void helpLogic(String correctAnswer) {
        switch (correctAnswer) {
            case "A":
            case "C":
                B.setVisibility(View.INVISIBLE);
                D.setVisibility(View.INVISIBLE);
                break;
            case "B":
            case "D":
                A.setVisibility(View.INVISIBLE);
                C.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
