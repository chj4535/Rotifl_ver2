package com.example.prfc.AccountActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.prfc.R;

public class SampleActivity extends AppCompatActivity {

    int position;
    TextView sampleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        sampleText = (TextView)findViewById(R.id.textView2);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        sampleText.setText("From List "+ position);

    }
}
