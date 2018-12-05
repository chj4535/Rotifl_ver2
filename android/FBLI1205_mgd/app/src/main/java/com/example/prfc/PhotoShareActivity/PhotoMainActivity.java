package com.example.prfc.PhotoShareActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.prfc.R;

public class PhotoMainActivity extends AppCompatActivity {

    private Button tpupload, chupload, download;
    private String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_main);

        groupid = getIntent().getStringExtra("groupid");
        tpupload = (Button)findViewById(R.id.btntpupload);
        chupload = (Button)findViewById(R.id.btnchupload);
        download = (Button)findViewById(R.id.btndownload);

        chupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMainActivity.this, PhotoShareActivity.class);
                intent.putExtra("groupid", groupid);
                startActivity(intent);
            }
        });

        tpupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMainActivity.this, PhotoCamShareActivity.class);
                intent.putExtra("groupid", groupid);
                startActivity(intent);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoMainActivity.this, DownloadPhotoActivity.class);
                intent.putExtra("groupid", groupid);
                startActivity(intent);
            }
        });
    }
}
