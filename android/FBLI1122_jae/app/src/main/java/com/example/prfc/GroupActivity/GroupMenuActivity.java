package com.example.prfc.GroupActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.prfc.ChattingActivity.MessengerActivity;
import com.example.prfc.BudgetActivity.PersonalBudgetActivity;
import com.example.prfc.R;

import java.util.ArrayList;

public class GroupMenuActivity extends AppCompatActivity {

    private Button BudgetManage, Chatting;

    String groupid;
    ArrayList invitedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        groupid = getIntent().getStringExtra("groupid");
        invitedUsers = getIntent().getStringArrayListExtra("invitedUsers");

        BudgetManage = (Button)findViewById(R.id.budget_manage);
        Chatting = (Button)findViewById(R.id.chatting);

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
                intent.putExtra("groupid", groupid);
                intent.putExtra("invitedUsers", invitedUsers);
                startActivity(intent);
            }
        });
    }

}
