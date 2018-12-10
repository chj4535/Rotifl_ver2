package com.example.prfc.BudgetActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prfc.Classes.Board;
import com.example.prfc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetgBudgetActivity extends Activity {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText textView;
    private DatabaseReference mDatabase;
    private String groupid;
    private Board group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_budget);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        textView = (EditText)findViewById(R.id.txtText);
        group = (Board) getIntent().getParcelableExtra("group");
        groupid = group.getId();
        Toast.makeText(getApplicationContext(), mDatabase.toString(), Toast.LENGTH_SHORT).show();
    }
    public void mOnClose(View V){
        Intent intent = new Intent();
        intent.putExtra("result", textView.getText().toString());
        setResult(2, intent);
        if(textView.getText().toString().equals("")){
            finish();
            return;
        }
        //groupid로 바꿔야 함
        mDatabase.child(groupid).child("amount").setValue(textView.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Toast.makeText(getApplicationContext(), "dbupdate = " + "success", Toast.LENGTH_SHORT).show();
                finish();
                // ...
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getApplicationContext(), "dbupdate = " + "failed", Toast.LENGTH_SHORT).show();
                        finish();
                        // ...
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    public JSONObject makeJSONObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("mamount",textView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("************json test" + jsonObject.toString());

        return jsonObject;
    }

    class Connection extends AsyncTask<String, Void, String> {

        int responseStatusCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Toast.makeText(SetgBudgetActivity.this, result, Toast.LENGTH_SHORT).show();

            //여기서 다음 엑티비티로 넘어간다.
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/"+ user.getUid() +"/gbudget";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(makeJSONObject().toString().getBytes("UTF-8"));

                outputStream.flush();
                outputStream.close();


                responseStatusCode = httpURLConnection.getResponseCode();
                System.out.println("**************************************ResponseCode " + responseStatusCode);


                return "success";


            } catch (Exception e) {

                // Log.d(TAG, "InsertData: Error ", e);

                return "fail";
            }
        }
    }

}
