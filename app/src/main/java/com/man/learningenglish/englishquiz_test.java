package com.man.learningenglish;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class questionnaire {
    public String ID;
    public String AnswerA, AnswerB, AnswerC, AnswerD, Answer;
}

public class englishquiz_test extends AppCompatActivity {
    TextView KetQua, CauHoi, ThoiGian;
    ImageView HinhAnh;

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
        A.setOnClickListener(view -> handleRadioButtonSelection(A));
        B.setOnClickListener(view -> handleRadioButtonSelection(B));
        C.setOnClickListener(view -> handleRadioButtonSelection(C));
        D.setOnClickListener(view -> handleRadioButtonSelection(D));
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
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                resetRadioButtonBackgrounds();
                pos++;
                if (pos >= list.size()) {
                    navigateToResult();
                } else {
                    Display(pos);
                }
            }, 2000);
        });
    }

    private void handleRadioButtonSelection(RadioButton selectedRadioButton) {

        A.setBackgroundResource(R.drawable.custom_radiogroup);
        B.setBackgroundResource(R.drawable.custom_radiogroup);
        C.setBackgroundResource(R.drawable.custom_radiogroup);
        D.setBackgroundResource(R.drawable.custom_radiogroup);

        selectedRadioButton.setBackgroundResource(R.drawable.custom_radiogroup_selected);

        A.setChecked(false);
        B.setChecked(false);
        C.setChecked(false);
        D.setChecked(false);
        selectedRadioButton.setChecked(true);
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
        A.setVisibility(View.VISIBLE);
        B.setVisibility(View.VISIBLE);
        C.setVisibility(View.VISIBLE);
        D.setVisibility(View.VISIBLE);
        A.setBackgroundResource(R.drawable.custom_radiogroup);
        B.setBackgroundResource(R.drawable.custom_radiogroup);
        C.setBackgroundResource(R.drawable.custom_radiogroup);
        D.setBackgroundResource(R.drawable.custom_radiogroup);
    }

    public void AddQuestionFromFileTXT() {
        try {
            InputStream in = getAssets().open("Question.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(",");
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

        List<Integer> resultQuesId = new ArrayList<>();

        questionnaire Q = new questionnaire();
        Random generator = new Random();
        // Generate four random answers ensuring they are unique
        Question questionA = PList.get(generator.nextInt(50));
        Q.AnswerA = questionA.getName();
        resultQuesId.add(questionA.getId());
        do {
            Question ques = PList.get(generator.nextInt(50));
            Q.AnswerB = ques.getName();
            resultQuesId.add(ques.getId());
        } while (Q.AnswerB.equals(Q.AnswerA));

        do {
            Question ques = PList.get(generator.nextInt(50));
            Q.AnswerC = ques.getName();
            resultQuesId.add(ques.getId());
        } while (Q.AnswerC.equals(Q.AnswerB) || Q.AnswerC.equals(Q.AnswerA));

        do {
            Question ques = PList.get(generator.nextInt(50));
            Q.AnswerD = ques.getName();
            resultQuesId.add(ques.getId());
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
        Q.ID = "a" + resultQuesId.get(value);
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
        int idCheck = -1;
        if (A.isChecked()) idCheck = R.id.RdbA;
        else if (B.isChecked()) idCheck = R.id.RdbB;
        else if (C.isChecked()) idCheck = R.id.RdbC;
        else if (D.isChecked()) idCheck = R.id.RdbD;

        highlightAnswers(idCheck, list.get(pos).Answer);

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
            default:
                break;
        }
    }

    // Logic for the help button (removes two incorrect answers)
    public void helpLogic(String correctAnswer) {
        switch (correctAnswer) {
            case "A":
                B.setVisibility(View.INVISIBLE);
                C.setVisibility(View.INVISIBLE);
                break;
            case "C":
                B.setVisibility(View.INVISIBLE);
                D.setVisibility(View.INVISIBLE);
                break;
            case "B":
                A.setVisibility(View.INVISIBLE);
                D.setVisibility(View.INVISIBLE);
                break;
            case "D":
                A.setVisibility(View.INVISIBLE);
                C.setVisibility(View.INVISIBLE);
                break;
        }
    }
    private void highlightAnswers(int selectedId, String correctAnswer) {
        resetRadioButtonBackgrounds();

        if ((selectedId == R.id.RdbA && correctAnswer.equals("A")) ||
                (selectedId == R.id.RdbB && correctAnswer.equals("B")) ||
                (selectedId == R.id.RdbC && correctAnswer.equals("C")) ||
                (selectedId == R.id.RdbD && correctAnswer.equals("D"))) {
            switch (selectedId) {
                case R.id.RdbA:
                    A.setBackgroundResource(R.drawable.custom_button_3);
                    break;
                case R.id.RdbB:
                    B.setBackgroundResource(R.drawable.custom_button_3);
                    break;
                case R.id.RdbC:
                    C.setBackgroundResource(R.drawable.custom_button_3);
                    break;
                case R.id.RdbD:
                    D.setBackgroundResource(R.drawable.custom_button_3);
                    break;
            }
        } else {
            switch (selectedId) {
                case R.id.RdbA:
                    A.setBackgroundResource(R.drawable.custom_button_4);
                    break;
                case R.id.RdbB:
                    B.setBackgroundResource(R.drawable.custom_button_4);
                    break;
                case R.id.RdbC:
                    C.setBackgroundResource(R.drawable.custom_button_4);
                    break;
                case R.id.RdbD:
                    D.setBackgroundResource(R.drawable.custom_button_4);
                    break;
            }
        }

        switch (correctAnswer) {
            case "A":
                A.setBackgroundResource(R.drawable.custom_button_3); // Tô màu xanh
                break;
            case "B":
                B.setBackgroundResource(R.drawable.custom_button_3);
                break;
            case "C":
                C.setBackgroundResource(R.drawable.custom_button_3);
                break;
            case "D":
                D.setBackgroundResource(R.drawable.custom_button_3);
                break;
        }


    }

    private void resetRadioButtonBackgrounds() {
        A.setBackgroundResource(R.drawable.custom_radiogroup);
        B.setBackgroundResource(R.drawable.custom_radiogroup);
        C.setBackgroundResource(R.drawable.custom_radiogroup);
        D.setBackgroundResource(R.drawable.custom_radiogroup);
    }

    private void insertDumpData() {
        PList.add(new Question("Apple", 1));
        PList.add(new Question("Book", 2));
        PList.add(new Question("Clock", 3));
        PList.add(new Question("Bicycle", 4));
        PList.add(new Question("Coin", 5));
        PList.add(new Question("Wardrobe", 6));
        PList.add(new Question("Television", 7));
        PList.add(new Question("Shoes", 8));
        PList.add(new Question("Washing Machine", 9));
        PList.add(new Question("Toothbrush", 10));
        PList.add(new Question("Razor Blade", 11));
        PList.add(new Question("Scissors", 12));
        PList.add(new Question("Air Conditioning", 13));
        PList.add(new Question("Curtain", 14));
        PList.add(new Question("Mirror", 15));
        PList.add(new Question("Switch", 16));
        PList.add(new Question("Comb", 17));
        PList.add(new Question("Eraser", 18));
        PList.add(new Question("Tape", 19));
        PList.add(new Question("Newspaper", 20));
        PList.add(new Question("Straw", 21));
        PList.add(new Question("Jigsaw", 22));
        PList.add(new Question("Bandage", 23));
        PList.add(new Question("Mask", 24));
        PList.add(new Question("Wheelchair", 25));
        PList.add(new Question("Syringe", 26));
        PList.add(new Question("Crutch", 27));
        PList.add(new Question("Scale", 28));
        PList.add(new Question("Saw", 29));
        PList.add(new Question("Drill", 30));
        PList.add(new Question("Screwdriver", 31));
        PList.add(new Question("Wrench", 32));
        PList.add(new Question("Hammer", 33));
        PList.add(new Question("Carpet", 34));
        PList.add(new Question("Wall Cabinet", 35));
        PList.add(new Question("Necklace", 36));
        PList.add(new Question("Tweezers", 37));
        PList.add(new Question("Dental Floss", 38));
        PList.add(new Question("Tissue", 39));
        PList.add(new Question("Makeup Kit", 40));
        PList.add(new Question("Handheld Calculator", 41));
        PList.add(new Question("Pen", 42));
        PList.add(new Question("Board", 43));
        PList.add(new Question("Remote Control", 44));
        PList.add(new Question("Paper Clip", 45));
        PList.add(new Question("Clothes Hanger", 46));
        PList.add(new Question("Clothes Iron", 47));
        PList.add(new Question("Wooden Blinds", 48));
        PList.add(new Question("Sink", 49));
        PList.add(new Question("Flower Vase", 50));

        for (Question question : PList) {
            System.out.println("Loaded Question: " + question.getName());
        }
    }
}
