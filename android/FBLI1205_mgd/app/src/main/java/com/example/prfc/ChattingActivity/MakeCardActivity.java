package com.example.prfc.ChattingActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;

public class MakeCardActivity extends Activity{

    EditText edName;
    EditText edComment;
    TextView txtDay;
    TextView txtStartTime;
    TextView txtEndTime;
    Button addBtn;
    Calendar calendar;

    int mYear;
    int mMonth;
    int mDate;
    int startHour = 12;
    int startMinute = 0;
    int endHour =12;
    int endMinute = 0;

    String groupid;
    String userid;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_card);
        setTitle("카드 생성");

        user = FirebaseAuth.getInstance().getCurrentUser();
        groupid = getIntent().getStringExtra("groupid");
        userid = user.getUid();
        calendar = Calendar.getInstance();

        edName = (EditText)findViewById(R.id.make_card_name);
        edComment = (EditText)findViewById(R.id.make_card_comment);
        txtDay = (TextView)findViewById(R.id.make_card_day);
        txtStartTime = (TextView)findViewById(R.id.make_card_start_time);
        txtEndTime = (TextView)findViewById(R.id.make_card_end_time);
        addBtn = (Button)findViewById(R.id.make_card_add_btn);

        //edComment는 인텐트로 받아서 처리
        edComment.setText(getIntent().getStringExtra("content"));

        txtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(MakeCardActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                mYear = year;
                                mMonth = month+1;
                                mDate = date;
                                txtDay.setText(mYear+"-"+String.format("%02d-%02d",mMonth,mDate));
                            }
                        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        });
        //
        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(MakeCardActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                startHour = hour;
                                startMinute = minute;
                                txtStartTime.setText("시작시간 "+String.format("%02d:%02d",startHour,startMinute));
                            }
                        }, startHour, startMinute, false);
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();

            }
        });

        txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(MakeCardActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                endHour = hour;
                                endMinute = minute;
                                txtEndTime.setText("끝나는 시간 "+String.format("%02d:%02d",endHour,endMinute));
                            }
                        }, endHour, endMinute, false);
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();

            }
        });

        //여기서 서버로 전송.
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection connection = new Connection();
                connection.execute();
                //System.out.println("********make card json test : "+ makeJSONObject().toString());
            }
        });
    }

    public JSONObject makeJSONObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", edName.getText().toString());
            jsonObject.put("Year", mYear);
            jsonObject.put("Month", mMonth);
            jsonObject.put("dayOfMonth", mDate);
            jsonObject.put("startTime", String.format("%02d:%02d",startHour,startMinute));
            jsonObject.put("endTime",String.format("%02d:%02d",endHour,endMinute));
            jsonObject.put("color","#87D288");
            jsonObject.put("comment",edComment.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("************Make Card request json test" + jsonObject.toString());

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

            Toast.makeText(MakeCardActivity.this, "make card result : " + result, Toast.LENGTH_SHORT);


            finish();

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/"+userid+"/group/"+groupid+"/card";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoOutput(true);

                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(makeJSONObject().toString().getBytes("UTF-8"));

                outputStream.flush();
                outputStream.close();

                responseStatusCode = httpURLConnection.getResponseCode();
                System.out.println("**************************************Make card ResponseCode " + responseStatusCode);

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
