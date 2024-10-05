package com.man.learningenglish;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class huongdanword extends Activity {
    ImageView Back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huongdanword);
        Back = (ImageView) findViewById(R.id.Back);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.buttonclick);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent = new Intent(huongdanword.this, correctword.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
