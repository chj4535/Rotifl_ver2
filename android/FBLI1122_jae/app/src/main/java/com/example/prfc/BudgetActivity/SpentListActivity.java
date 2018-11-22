package com.example.prfc.BudgetActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prfc.Classes.ExpenseList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import com.example.prfc.R;

public class SpentListActivity extends AppCompatActivity {

    private RecyclerView mMainRecyclerView;
    private ExpenseListAdapter mAdapter;
    private List<ExpenseList> mBoardList = new ArrayList<>();
    private String userid;

    ArrayList<HashMap<String,String>> parsedItems = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private static String TAG = "show Expense List";
    int i = 0;

    public interface ListBtnClickListener {
        void onListBtnClick(int position) ;
    }

    //server에서 사용자의 그룹리스트를 불러와 리사이클러 뷰에 띄워주는 코드 필요

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spent_list);
        //Intent intent = getIntent();
        //user_id = intent.getExtras().getString("user_id", "test1");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getDisplayName();
        setTitle("Expense List");

        Connection connection = new Connection();
        connection.execute();

        mMainRecyclerView = findViewById(R.id.spent_recycler_view);

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
            return mBoardList.size();
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