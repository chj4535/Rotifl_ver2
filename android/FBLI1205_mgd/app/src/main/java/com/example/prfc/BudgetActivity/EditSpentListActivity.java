package com.example.prfc.BudgetActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.prfc.R;

public class EditSpentListActivity extends Activity {

    private TextView itemtext, pricetext;
    private int pos;
    private String itemoriginal, priceoriginal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_spent_list);
        String position = getIntent().getStringExtra("position");
        itemoriginal = getIntent().getStringExtra("item");
        priceoriginal = getIntent().getStringExtra("price");
        pos = Integer.valueOf(position);
        itemtext = (TextView)findViewById(R.id.itemtext2);
        pricetext = (TextView)findViewById(R.id.pricetext2);
    }

    public void mOnClose(View V){
        Intent intent = new Intent();
        if(itemtext.getText().toString().equals("")){
            intent.putExtra("item", itemoriginal);
        }else{
            intent.putExtra("item", itemtext.getText().toString());
        }
        if(pricetext.getText().toString().equals("")){
            intent.putExtra("price", priceoriginal);
        }else{
            intent.putExtra("price", pricetext.getText().toString());
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
