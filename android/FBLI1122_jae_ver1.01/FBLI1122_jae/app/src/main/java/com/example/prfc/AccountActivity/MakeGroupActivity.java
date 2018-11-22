package com.example.prfc.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prfc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MakeGroupActivity extends Activity {

    Intent intent;

    Button add;
    Button invite;
    EditText groupName;
    EditText location;
    EditText startDate;
    EditText endDate;

    String groupid;
    String userid;
    String name;
    String location_str;
    String start;
    String end;
    String username;
    ArrayList<HashMap<String,String>> parsedItems = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private static String TAG = "make Group list";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        setTitle("그룹 생성");
        username = user.getDisplayName();
        groupName = (EditText)findViewById(R.id.group_name);
        location = (EditText)findViewById(R.id.location);
        startDate = (EditText)findViewById(R.id.Date_start);
        endDate = (EditText)findViewById(R.id.Date_end);

        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //groupName.setText("");
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //location.setText("");
            }
        });

        add = (Button)findViewById(R.id.group_add_button);




        //그룹 이름, 지역명만 이전 엑티비티에 넘어갑니다.
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = groupName.getText().toString();
                location_str = location.getText().toString();
                intent = new Intent(MakeGroupActivity.this, GroupActivity.class);

                intent.putExtra("groupName", name);
                intent.putExtra("location", location_str);

                Connection connection = new Connection();
                connection.execute();

                add.setEnabled(false);
            }
        });

    }

    public JSONObject makeJSONObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("groupname", name);
            jsonObject.put("grouparea", location_str);
            jsonObject.put("groupstart", "2018-12-05");
            jsonObject.put("groupend", "2018-12-10");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("************json test" + jsonObject.toString());

        return jsonObject;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
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

            Toast.makeText(MakeGroupActivity.this, result, Toast.LENGTH_SHORT).show();

            groupid = result;
            intent.putExtra("groupid", result);
            setResult(RESULT_OK,intent);
            finish();

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/"+userid+"/group";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(makeJSONObject().toString().getBytes("UTF-8"));

                outputStream.flush();
                outputStream.close();

                responseStatusCode = httpURLConnection.getResponseCode();
                System.out.println("**************************************Make group ResponseCode " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();



            } catch (Exception e) {

                // Log.d(TAG, "InsertData: Error ", e);

                return "fail";
            }

        }
    }


}