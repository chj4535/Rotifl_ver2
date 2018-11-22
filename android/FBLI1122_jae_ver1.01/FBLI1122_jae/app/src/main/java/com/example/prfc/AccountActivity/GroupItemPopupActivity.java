package com.example.prfc.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.prfc.R;

public class GroupItemPopupActivity extends Activity {

    Button deleteBtn;
    Button EditBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_item_popup);

        setTitle(" ");

        deleteBtn = (Button)findViewById(R.id.Delete_btn);
        EditBtn = (Button)findViewById(R.id.Edit_btn);

        final Intent intent = new Intent(GroupItemPopupActivity.this, GroupActivity.class);
        final Intent comingIntent = getIntent();


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position;
                position = comingIntent.getIntExtra("position",9999);
                System.out.println("***************************delete hi, position ="+position);
                intent.putExtra("which", 1);
                intent.putExtra("position", position);

                //System.out.println("**********************************************position :"+position);
                setResult(RESULT_OK,intent);

                finish();
            }
        });

        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position;
                position = comingIntent.getIntExtra("position",9999);

                intent.putExtra("which", 2);
                intent.putExtra("position", position);

                setResult(RESULT_OK,intent);

                finish();
            }
        });
    }
}
