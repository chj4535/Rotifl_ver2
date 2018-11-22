package com.example.prfc.AccountActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.prfc.R;
import java.security.acl.Group;
import java.util.HashMap;
import java.util.Map;

public class GroupMenuActivity extends AppCompatActivity {

    private Button BudgetManage, Chatting;

    String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);

        groupid = getIntent().getStringExtra("groupid");

        BudgetManage = (Button)findViewById(R.id.budget_manage);
        Chatting = (Button)findViewById(R.id.chatting);

        BudgetManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupMenuActivity.this, BudgetManageActivity.class));
            }
        });

        Chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMenuActivity.this, MessengerActivity.class);
                intent.putExtra("groupid", groupid);
                startActivity(intent);
            }
        });
    }

}
