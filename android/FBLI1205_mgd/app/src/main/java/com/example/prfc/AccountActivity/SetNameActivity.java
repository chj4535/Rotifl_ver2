package com.example.prfc.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.example.prfc.R;

public class SetNameActivity extends Activity {

    EditText textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_name);
        textView = (EditText)findViewById(R.id.nameText);
    }
    public void mOnClose(View V){
        Intent intent = new Intent();
        intent.putExtra("result", textView.getText().toString());
        if((textView.getText().toString() == null) || textView.getText().toString().equals("")){
            setResult(RESULT_CANCELED, intent);
        }else{
            setResult(RESULT_OK, intent);
        }
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

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}