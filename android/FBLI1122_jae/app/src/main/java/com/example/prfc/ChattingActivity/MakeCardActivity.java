package com.example.prfc.ChattingActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.prfc.R;

import org.w3c.dom.Text;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_card);
        setTitle("카드 생성");

        calendar = Calendar.getInstance();

        edName = (EditText)findViewById(R.id.make_card_name);
        edComment = (EditText)findViewById(R.id.make_card_comment);
        txtDay = (TextView)findViewById(R.id.make_card_day);
        txtStartTime = (TextView)findViewById(R.id.make_card_start_time);
        txtEndTime = (TextView)findViewById(R.id.make_card_end_time);
        addBtn = (Button)findViewById(R.id.make_card_add_btn);

        //edComment는 인텐트로 받아서 처리

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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
