package com.example.prfc.AccountActivity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.prfc.R;

public class BoardPostActivity extends AppCompatActivity {
    TextInputEditText title;
    TextInputEditText content;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_board);
//        title = (TextInputEditText) findViewById(R.layout);
//        content = (TextInputEditText) findViewById(R.layout.);


    }
}
