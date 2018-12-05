package com.example.prfc.BudgetActivity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.prfc.Classes.ExpenseList;
import com.example.prfc.Classes.UserList;
import com.example.prfc.R;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GoDutchActivity extends AppCompatActivity {

    private RecyclerView mMainRecyclerView;
    private String email, groupid;
    private Button confirm;
    int res;
    ArrayList<HashMap<String, String>> parsedItems = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private UserListAdapter mAdapter;
    private List<UserList> mBoardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_dutch);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();;
        groupid = getIntent().getStringExtra("groupid");

        //서버에서 설정한 예산을 불러와야함

        //remaining.setText(String.valueOf(res));

        //Toast.makeText(getApplicationContext(), "user id = " + currentid, Toast.LENGTH_SHORT).show();
        mMainRecyclerView = findViewById(R.id.uRecyclerView);

        RequestList connection = new RequestList();
        connection.execute();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostResult connect = new PostResult();
                connect.execute();
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == 1) {
            //데이터 받기

            //remaining.setText(String.valueOf(res));
        }else if(resultCode == 5){
            String amount = data.getStringExtra("amount");
            String pos = data.getStringExtra("position");
            UserList dlist = mBoardList.get(Integer.valueOf(pos));;
            dlist.setAmount(amount);
            mAdapter.notifyItemChanged(Integer.valueOf(pos));
        }
    }

    public int getItemCount() {
        return mBoardList.size();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }

    //그룹 유저 요청
    public ArrayList<HashMap<String,String>> parsing(String receivedData) {
        String email = "email";
        String name = "name";

        JSONObject sample;
        try {
            //JSONObject jsonObject = new JSONObject(receivedData);
            JSONArray jsonArray = new JSONArray(receivedData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                hashMap = new HashMap<>();//초기화

                hashMap.put(email, item.getString(email));
                hashMap.put(name, item.getString(email));

                parsedItems.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d("", "showResult : ", e);
        }

        return parsedItems;
    }

    private class UserListAdapter extends RecyclerView.Adapter <UserListAdapter.GroupListViewHolder> {


        // 리스트 생성
        private List<UserList> mBoardList;


        public UserListAdapter(List<UserList> mBoardList) {
            this.mBoardList = mBoardList;
        }

        public UserListAdapter(){

        }
        @NonNull
        @Override
        public GroupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            // 버튼눌렀을때 추가 되는 박스
            return new GroupListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.guser_card,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull GroupListViewHolder holder, int position) {
            UserList data = mBoardList.get(position);
            holder.mNameTextView.setText(data.getName());
            holder.mAmountTextView.setText("0");
        }


        //리스트 생성갯수
        @Override
        public int getItemCount() {
            return mBoardList.size();
        }


        // 데이터를 어댑터를 통해서 recycler 뷰에 보여줌
        class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mNameTextView;
            private TextView mAmountTextView;

            public GroupListViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);
                mNameTextView = itemView.findViewById(R.id.nameview2);
                mAmountTextView  = itemView.findViewById(R.id.amountview2);
            }


            @Override
            public void onClick(View v){ //클릭됐을때 수정하도록 함.
                Intent intent = new Intent(v.getContext(), EditSpentListActivity.class);
                UserList ulist = mBoardList.get(getPosition());
                intent.putExtra("name", ulist.getName());
                intent.putExtra("position", String.valueOf(getPosition()));
                startActivityForResult(intent, 5);
            }
        } // 어댑터를 통해서 무엇을 보낼건지

    }

    //그룹 유저 요청
    class RequestList extends AsyncTask<String, Void, String> {

        int responseStatusCode;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            HashMap<String, String> item;

            //Toast.makeText(GroupActivity.this, result, Toast.LENGTH_SHORT).show();

            parsedItems = parsing(result);

            for(int i=0;i<parsedItems.size();i++){
                item = parsedItems.get(i);
                mBoardList.add(new UserList(item.get("name"), "0", item.get("email")));
            }

            mAdapter = new UserListAdapter(mBoardList);
            mMainRecyclerView.setAdapter(mAdapter);

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/" + groupid;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.connect();


                responseStatusCode = httpURLConnection.getResponseCode();
                System.out.println("**************************************ResponseCode " + responseStatusCode);

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

                return new String("Error: " + e.getMessage());
            }

        }
    }

    //결과 전송 개인 지출로
    public JSONObject makeJSONObject(int i) {
        Date dt = new Date();
        SimpleDateFormat full_sdf = new SimpleDateFormat("yyyy-MM-dd, a hh:mm:ss");
        String date = full_sdf.format(dt);

        UserList ulist = mBoardList.get(i);
        JSONObject jsonObject = new JSONObject();
        String name = String.valueOf(ulist.getName());
        String amount = String.valueOf(ulist.getAmount());

        try {
            jsonObject.put("item", name);
            jsonObject.put("price",amount);
            jsonObject.put("time",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("************json test" + jsonObject.toString());

        return jsonObject;
    }

    class PostResult extends AsyncTask<String, Void, String> {

        int responseStatusCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Toast.makeText(GoDutchActivity.this, result, Toast.LENGTH_SHORT).show();

            //여기서 다음 엑티비티로 넘어간다.
        }


        @Override
        protected String doInBackground(String... params) {

            int i = getItemCount();

            for(int j = 0; j < i; j++){
                try{
                    UserList ulist = mBoardList.get(j);
                    String serverURL = "http://13.209.15.179:50000/user/"+ groupid +"/pbudget" + ulist.getEmail();
                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.connect();


                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    outputStream.write(makeJSONObject(i).toString().getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    responseStatusCode = httpURLConnection.getResponseCode();
                    System.out.println("**************************************ResponseCode " + responseStatusCode);

                }catch(Exception e){
                    return "fail";
                }
            }
            return "success";
        }
    }
}
