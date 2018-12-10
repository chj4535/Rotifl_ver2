package com.example.prfc.PhotoShareActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.prfc.Classes.Board;
import com.example.prfc.R;

public class PhotoMainActivity extends AppCompatActivity {

    private RelativeLayout tpupload, chupload, download;
    private String groupid;
    private Board group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_main);
        getSupportActionBar().setElevation(0);
        setTitle("사진 공유");
        group = (Board) getIntent().getParcelableExtra("group");
        groupid = group.getId();
        tpupload = (RelativeLayout)findViewById(R.id.btntpupload);
        chupload = (RelativeLayout)findViewById(R.id.btnchupload);
        download = (RelativeLayout)findViewById(R.id.btndownload);

        chupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMainActivity.this, PhotoShareActivity.class);
                intent.putExtra("group", group);
                startActivity(intent);
            }
        });

        tpupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMainActivity.this, PhotoCamShareActivity.class);
                intent.putExtra("group", group);
                startActivity(intent);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMainActivity.this, DownloadPhotoActivity.class);
                intent.putExtra("group", group);
                startActivity(intent);
            }
        });
    }
}
