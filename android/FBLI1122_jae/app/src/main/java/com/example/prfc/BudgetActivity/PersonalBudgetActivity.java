package com.example.prfc.BudgetActivity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.prfc.Classes.ExpenseList;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PersonalBudgetActivity extends AppCompatActivity {

    private RecyclerView mMainRecyclerView;
    private static String TAG = "show spend list";
    private Button GoDutch, SetBudget,ApplySpent, SpentList;
    private TextView Budget, Spent, remaining;
    private String userid;
    int res;
    ArrayList<HashMap<String, String>> parsedItems = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private ExpenseListAdapter mAdapter;
    private List<ExpenseList> mBoardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_budget);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();;

        Budget = (TextView)findViewById(R.id.budget_view);
        Spent = (TextView)findViewById(R.id.spent_view);
        remaining = (TextView)findViewById(R.id.money_remain);
        SetBudget = (Button)findViewById(R.id.set_budget);
        GoDutch = (Button)findViewById(R.id.manage_godutch);
        ApplySpent = (Button)findViewById(R.id.apply_spent);
        SpentList = (Button)findViewById(R.id.expense_list);

        //서버에서 설정한 예산을 불러와야함
        if(Budget.getText().toString() == null || Budget.getText().equals("")){
            Budget.setText("0");
        }

        //서버에서 사용한 금액을 불러와야함
        if(Spent.getText().toString() == null || Spent.getText().equals("")){
            Spent.setText("0");
        }
        res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
        remaining.setText(String.valueOf(res));

        //Budget.setText(amount); //서버에서 값 받아와서 잔액 표시해줘야함

        GoDutch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalBudgetActivity.this, GoDutchActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        SetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalBudgetActivity.this, SetBudgetActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        ApplySpent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalBudgetActivity.this, ApplySpentActivity.class);
                startActivityForResult(intent, 3);
            }
        });

        SpentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalBudgetActivity.this, SpentListActivity.class));
            }
        });

        //Toast.makeText(getApplicationContext(), "user id = " + currentid, Toast.LENGTH_SHORT).show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == 1) {
            //데이터 받기
            res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
            remaining.setText(String.valueOf(res));
        }else if(resultCode == 2) {
            String result = data.getStringExtra("result");
            Budget.setText(result);//서버로 설정된 예산을 보내야함
            res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
            remaining.setText(String.valueOf(res));
        }else if(resultCode == 3){
            //db로 item, price 보내야함
            String item = data.getStringExtra("Item");
            String price = data.getStringExtra("Price");
            Spent.setText(price);
            res = (Integer.valueOf(Budget.getText().toString()) - Integer.valueOf(Spent.getText().toString()));
            remaining.setText(String.valueOf(res));
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

        JSONObject sample;
        try {
            //JSONObject jsonObject = new JSONObject(receivedData);
            JSONArray jsonArray = new JSONArray(receivedData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                hashMap = new HashMap<>();//초기화

                hashMap.put(spentitem, item.getString(spentitem));
                hashMap.put(spentprice, item.getString(spentprice));


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
        }


        //리스트 생성갯수
        @Override
        public int getItemCount() {
            if(mBoardList.size() < 4){
                return mBoardList.size();
            }else{
                return 4;
            }
        }


        // 데이터를 어댑터를 통해서 recycler 뷰에 보여줌
        class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mItemTextView;
            private TextView mPriceTextView;

            public GroupListViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);
                mItemTextView = itemView.findViewById(R.id.itemview3);
                mPriceTextView  = itemView.findViewById(R.id.priceview3);
            }


            @Override
            public void onClick(View v){ //클릭됐을때 수정하도록 함.
                //Board data = mBoardList.get(position);
                //String groupname = data.getName();
                Intent intent = new Intent(v.getContext(), EditSpentListActivity.class);
                //intent.putExtra("groupid", "asd");//?
                v.getContext().startActivity(intent);
            }
        } // 어댑터를 통해서 무엇을 보낼건지

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
            HashMap<String, String> item;

            //Toast.makeText(GroupActivity.this, result, Toast.LENGTH_SHORT).show();

            parsedItems = parsing(result);

            for(int i=0;i<parsedItems.size();i++){
                item = parsedItems.get(i);
                mBoardList.add(new ExpenseList(item.get("item"), item.get("price")));
            }

            mAdapter = new ExpenseListAdapter(mBoardList);
            mMainRecyclerView.setAdapter(mAdapter);


            //여기서 다음 엑티비티로 넘어간다.
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://13.209.15.179:50000/user/" + userid;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
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

}
