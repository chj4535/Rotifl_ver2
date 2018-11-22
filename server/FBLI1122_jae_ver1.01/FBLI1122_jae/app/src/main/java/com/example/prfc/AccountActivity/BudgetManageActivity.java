package com.example.prfc.AccountActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.prfc.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.*;

import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class BudgetManageActivity extends AppCompatActivity {

    private Button GoDutch, SetBudget,ApplySpent, SpentList;
    private TextView Budget, Spent, remaining;
    private String userid, usermail;
    int res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_manage);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();;
        usermail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Budget = (TextView)findViewById(R.id.budget_view);
        Spent = (TextView)findViewById(R.id.spent_view);
        remaining = (TextView)findViewById(R.id.money_remain);
        SetBudget = (Button)findViewById(R.id.set_budget);
        GoDutch = (Button)findViewById(R.id.manage_godutch);
        ApplySpent = (Button)findViewById(R.id.apply_spent);
        SpentList = (Button)findViewById(R.id.expense_list);

        //서버에서 설정한 예산을 불러와야함
        if(Budget.getText().toString() == null || Budget.getText().equals("")){
            Budget.setText("0");
        }

        //서버에서 사용한 금액을 불러와야함
        if(Spent.getText().toString() == null || Spent.getText().equals("")){
            Spent.setText("0");
        }
        res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
        remaining.setText(String.valueOf(res));

/*
        ValueEventListener postListener = new ValueEventListener() { //db에서 예산 읽어오는부분
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mPostReference.addValueEventListener(postListener);
*/
        //Budget.setText(amount); //서버에서 값 받아와서 잔액 표시해줘야함

        GoDutch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetManageActivity.this, GoDutchActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        SetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetManageActivity.this, SetBudgetActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        ApplySpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetManageActivity.this, ApplySpentActivity.class);
                startActivityForResult(intent, 3);
            }
        });

        SpentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BudgetManageActivity.this, SpentListActivity.class));
            }
        });

        //Toast.makeText(getApplicationContext(), "user id = " + currentid, Toast.LENGTH_SHORT).show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == 1) {
            //데이터 받기
            res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
            remaining.setText(String.valueOf(res));
        }else if(resultCode == 2) {
            String result = data.getStringExtra("result");
            Budget.setText(result);//서버로 설정된 예산을 보내야함
            res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
            remaining.setText(String.valueOf(res));
        }else if(resultCode == 3){
            //db로 item, price 보내야함
            String item = data.getStringExtra("Item");
            String price = data.getStringExtra("Price");
            Spent.setText(price);
            res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
            remaining.setText(String.valueOf(res));
        }
    }
}
