package com.example.prfc.BudgetActivity;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prfc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplygSpentActivity extends Activity {

    EditText priceView;
    EditText itemView;
    String item, price, date, groupid, userid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_applyg_spent);

        priceView = (EditText)findViewById(R.id.pricetext);
        itemView = (EditText)findViewById(R.id.itemtext);
        groupid = getIntent().getStringExtra("groupid");
        userid = FirebaseAuth.getInstance().getUid();
    }

    public void mOnClose(View V){
        if(itemView.getText().toString().equals("") && priceView.getText().toString().equals("")){
            finish();
            return;
        }

        Date dt = new Date();
        SimpleDateFormat full_sdf = new SimpleDateFormat("yyyy-MM-dd, a hh:mm:ss");
        date = full_sdf.format(dt);

        Intent intent = new Intent();
        intent.putExtra("item", itemView.getText().toString());
        intent.putExtra("price", priceView.getText().toString());
        intent.putExtra("time", date);

        Connection connection = new Connection();
        connection.execute();
        setResult(3, intent);
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

    public JSONObject makeJSONObject() {

        JSONObject jsonObject = new JSONObject();
        item = String.valueOf(itemView.getText().toString());
        price = String.valueOf(priceView.getText().toString());

        try {
            jsonObject.put("item", item);
            jsonObject.put("price",price);
            jsonObject.put("time",date);
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

            Toast.makeText(ApplygSpentActivity.this, result, Toast.LENGTH_SHORT).show();

            //여기서 다음 엑티비티로 넘어간다.
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/" + userid + "/group/1/gbudget";

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