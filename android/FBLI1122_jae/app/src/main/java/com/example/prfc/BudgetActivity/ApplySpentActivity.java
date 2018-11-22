package com.example.prfc.BudgetActivity;

import android.app.Activity;
import android.content.Intent;
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

public class ApplySpentActivity extends Activity {

    EditText priceView;
    EditText itemView;
    String item, price;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_apply_spent);

        priceView = (EditText)findViewById(R.id.pricetext);
        itemView = (EditText)findViewById(R.id.itemtext);

    }
    public void mOnClose(View V){
        Intent intent = new Intent();
        intent.putExtra("Item", itemView.getText().toString());
        intent.putExtra("Price", priceView.getText().toString());
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("************json test" + jsonObject.toString());

        return jsonObject;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
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

            Toast.makeText(ApplySpentActivity.this, result, Toast.LENGTH_SHORT).show();

            //여기서 다음 엑티비티로 넘어간다.
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/"+ user.getUid() +"/spend";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("Content-Type", "application/json");
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