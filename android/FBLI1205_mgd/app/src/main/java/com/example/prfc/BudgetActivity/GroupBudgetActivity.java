package com.example.prfc.BudgetActivity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.prfc.Classes.Board;
import com.example.prfc.Classes.ExpenseList;
import com.example.prfc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupBudgetActivity extends AppCompatActivity {

    private RecyclerView mMainRecyclerView;
    private static String TAG = "show spend list";
    private Button GoDutch, SetBudget,ApplySpent, Calculate;
    private TextView Budget, remaining;
    private String userid, groupid;
    int res;
    ArrayList<HashMap<String, String>> parsedItems = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private ExpenseListAdapter mAdapter;
    private List<ExpenseList> mBoardList = new ArrayList<>();
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference amount;
    private String item, price, date;
    int pos = 0;
    private int samount = 0;
    private int oamount;
    private int temp;
    private Board group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_budget);
        getSupportActionBar().setElevation(0);
        setTitle("그룹 예산");
        RequestList connection = new RequestList();
        connection.execute();

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();;
        group = (Board) getIntent().getParcelableExtra("group");
        groupid = group.getId();
        amount = ref.child(groupid).child("amount");
        Budget = (TextView)findViewById(R.id.groupbudget);
        SetBudget = (Button)findViewById(R.id.setBudget2);
        GoDutch = (Button)findViewById(R.id.go_dutch);
        ApplySpent = (Button)findViewById(R.id.apply_spent2);
        Calculate = (Button)findViewById(R.id.btn_calculate);

        //서버에서 설정한 예산을 불러와야함


        //서버에서 사용한 금액을 불러와야함
        //remaining.setText(String.valueOf(res));

        //Budget.setText(amount); //서버에서 값 받아와서 잔액 표시해줘야함

        SetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupBudgetActivity.this, SetgBudgetActivity.class);
                intent.putExtra("group", group);
                startActivityForResult(intent, 2);
            }
        });

        ApplySpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupBudgetActivity.this, ApplygSpentActivity.class);
                startActivityForResult(intent, 3);
            }
        });

        //Toast.makeText(getApplicationContext(), "user id = " + currentid, Toast.LENGTH_SHORT).show();
        mMainRecyclerView = findViewById(R.id.gspent_recycler_view);

        amount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String amount = dataSnapshot.getValue(String.class);
                oamount = Integer.valueOf(amount);
                String res = String.valueOf(oamount - samount);
                Budget.setText(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == 1) {
            //데이터 받기

            //remaining.setText(String.valueOf(res));
        }else if(resultCode == 2) {
            String result = data.getStringExtra("result");
            if(result.equals("")){

            }else{
                oamount = Integer.valueOf(result);
                String res = String.valueOf(oamount - samount);
                Budget.setText(res);
            }

            //remaining.setText(String.valueOf(res));
        }else if(resultCode == 3){
            //db로 item, price 보내야함
            String item = data.getStringExtra("item");
            String price = data.getStringExtra("price");
            String time = data.getStringExtra("time");

            if(!price.equals("")){
                mBoardList.add(new ExpenseList(item, price, time));
                samount = samount + Integer.valueOf(price);
                String res = String.valueOf(oamount - samount);
                Budget.setText(res);
                mAdapter = new ExpenseListAdapter(mBoardList);
                mMainRecyclerView.setAdapter(mAdapter);
            }
            //remaining.setText(String.valueOf(res));
        }else if(resultCode == 5){
            String item = data.getStringExtra("item");
            String price = data.getStringExtra("price");
            String pos = data.getStringExtra("position");

            if(!price.equals("")){
                ExpenseList dlist = mBoardList.get(Integer.valueOf(pos));
                temp = Integer.valueOf(dlist.getPrice());
                dlist.setPrice(price);
                dlist.setItem(item);
                mAdapter.notifyItemChanged(Integer.valueOf(pos));
                samount = samount - temp + Integer.valueOf(dlist.getPrice());
                String res = String.valueOf(oamount - samount);
                Budget.setText(res);
            }
            RequestEdit connection = new RequestEdit();
            connection.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }

    public ArrayList<HashMap<String,String>> parsing(String receivedData) {
        String spentitem = "item";
        String spentprice = "price";
        String spenttime = "time";

        JSONObject sample;
        try {
            //JSONObject jsonObject = new JSONObject(receivedData);
            JSONArray jsonArray = new JSONArray(receivedData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                hashMap = new HashMap<>();//초기화

                hashMap.put(spentitem, item.getString(spentitem));
                hashMap.put(spentprice, item.getString(spentprice));
                hashMap.put(spenttime, item.getString(spenttime));

                parsedItems.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

        return parsedItems;
    }

    private class ExpenseListAdapter extends RecyclerView.Adapter <ExpenseListAdapter.GroupListViewHolder> {


        // 리스트 생성
        private List<ExpenseList> mBoardList;


        public ExpenseListAdapter(List<ExpenseList> mBoardList) {
            this.mBoardList = mBoardList;
        }

        public ExpenseListAdapter(){

        }
        @NonNull
        @Override
        public GroupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            // 버튼눌렀을때 추가 되는 박스
            return new GroupListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull GroupListViewHolder holder, int position) {
            ExpenseList data = mBoardList.get(position);
            holder.mItemTextView.setText(data.getItem());
            holder.mPriceTextView.setText(data.getPrice());
            holder.mTimeTextView.setText(data.getTime());
        }


        //리스트 생성갯수
        @Override
        public int getItemCount() {
            return mBoardList.size();
        }


        // 데이터를 어댑터를 통해서 recycler 뷰에 보여줌
        class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mItemTextView;
            private TextView mPriceTextView;
            private TextView mTimeTextView;

            public GroupListViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);
                mItemTextView = itemView.findViewById(R.id.itemview3);
                mPriceTextView  = itemView.findViewById(R.id.priceview3);
                mTimeTextView  = itemView.findViewById(R.id.dateview2);
            }


            @Override
            public void onClick(View v){ //클릭됐을때 수정하도록 함.
                Intent intent = new Intent(v.getContext(), EditSpentListActivity.class);
                ExpenseList dlist = mBoardList.get(getPosition());
                pos = getPosition();
                intent.putExtra("item", dlist.getItem());
                intent.putExtra("price", dlist.getPrice());
                intent.putExtra("position", String.valueOf(getPosition()));
                startActivityForResult(intent, 5);
            }
        } // 어댑터를 통해서 무엇을 보낼건지

    }

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
                mBoardList.add(new ExpenseList(item.get("item"), item.get("price"), item.get("time")));
                samount = samount + Integer.valueOf(item.get("price"));
                String res = String.valueOf(oamount - samount);
                Budget.setText(res);
            }

            mAdapter = new ExpenseListAdapter(mBoardList);
            mMainRecyclerView.setAdapter(mAdapter);


            //여기서 다음 엑티비티로 넘어간다.
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/" + userid + "/group/" + groupid + "/gbudget";

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

    //수정 요청
    class RequestEdit extends AsyncTask<String, Void, String> {

        int responseStatusCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Toast.makeText(GroupBudgetActivity.this, result, Toast.LENGTH_SHORT).show();

            //여기서 다음 엑티비티로 넘어간다.
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/" + userid + "/group/" + groupid + "/gbudget";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(makeJSONObject(pos).toString().getBytes("UTF-8"));

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

    public JSONObject makeJSONObject(int i) {
        ExpenseList dlist = mBoardList.get(pos);
        JSONObject jsonObject = new JSONObject();
        item = String.valueOf(dlist.getItem());
        price = String.valueOf(dlist.getPrice());
        date = String.valueOf(dlist.getTime());

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

}