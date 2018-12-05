package com.example.prfc.BudgetActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.prfc.R;

public class EditDutchActivity extends Activity {

    private TextView amountText, nametext;
    private int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dutch);
        String position = getIntent().getStringExtra("position");
        String name = getIntent().getStringExtra("name");
        pos = Integer.valueOf(position);
        nametext = (TextView)findViewById(R.id.dnameview2);
        amountText = (TextView)findViewById(R.id.amountview2);

        nametext.setText(name);
    }

    public void mOnClose(View V){
        Intent intent = new Intent();
        if(amountText.getText().toString().equals("")){
            finish();
            return;
        }else{
            intent.putExtra("amount", amountText.getText().toString());
        }

        intent.putExtra("position", String.valueOf(pos));
        setResult(5, intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}