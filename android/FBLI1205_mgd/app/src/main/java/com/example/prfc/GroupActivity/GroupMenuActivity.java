package com.example.prfc.GroupActivity;

import android.content.Intent;
import android.os.RemoteCallbackList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.prfc.CalendarActivity.CalendarActivity;
import com.example.prfc.ChattingActivity.MessengerActivity;
import com.example.prfc.BudgetActivity.PersonalBudgetActivity;
import com.example.prfc.Classes.Board;
import com.example.prfc.PhotoShareActivity.PhotoMainActivity;
import com.example.prfc.R;

import java.util.ArrayList;

public class GroupMenuActivity extends AppCompatActivity {

    private RelativeLayout BudgetManage, Chatting, PhotoShare, Schedule;

    String groupid;
    Board group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);
        setTitle("그룹 메뉴");
        //getActionBar().setTitle("그룹 메뉴");
        //groupid = getIntent().getStringExtra("groupid");
        group = (Board) getIntent().getParcelableExtra("group");
        groupid = group.getId();


        BudgetManage = (RelativeLayout)findViewById(R.id.budget_manage);
        Chatting = (RelativeLayout)findViewById(R.id.chatting);
        PhotoShare = (RelativeLayout)findViewById(R.id.photoshare);
        Schedule = (RelativeLayout)findViewById(R.id.schedule);

        BudgetManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupMenuActivity.this, CalendarActivity.class));
            }
        });

        BudgetManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupMenuActivity.this, PersonalBudgetActivity.class));
            }
        });

        Chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMenuActivity.this, MessengerActivity.class);

                intent.putExtra("group", group);

                startActivity(intent);
            }
        });

        PhotoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMenuActivity.this, PhotoMainActivity.class);
                intent.putExtra("groupid", groupid);
                startActivity(intent);
            }
        });
    }

}
