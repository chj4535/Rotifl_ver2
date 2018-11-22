package com.example.prfc.AccountActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prfc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CommunityActivity extends AppCompatActivity implements View.OnClickListener{

    ListView listView;
    MyListAdpater myListAdapter;
    ArrayList<list_item> list_itemArrayList;
    HashMap<String, String> item;
    ArrayList<HashMap<String, String>> MAP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        listView = (ListView) findViewById(R.id.list_view);

        list_itemArrayList = new ArrayList<list_item>();
        MAP = new ArrayList<HashMap<String, String>>();
        try{
            new Task().execute().get();
        }
        catch (Exception e){

        }

        //여기부터======================================================
//        list_itemArrayList.add(
//                new list_item(R.mipmap.ic_launcher,
//                        new Date(),
//                        "_id",
//                        "boardid",
//                        "title",
//                        "user",
//                        "content",
//                        "comment"));
//        list_itemArrayList.add(
//                new list_item(R.mipmap.ic_launcher,
//                        new Date(),
//                        "_id2",
//                        "boardid2",
//                        "title2",
//                        "user2",
//                        "content2",
//                        "comment2"));
//        list_itemArrayList.add(
//                new list_item(R.mipmap.ic_launcher,
//                        new Date(),
//                        "_id3",
//                        "boardid3",
//                        "title3",
//                        "user3",
//                        "content3",
//                        "comment3"));
//
//        myListAdapter = new MyListAdpater(MainActivity.this, list_itemArrayList);
//            listView.setAdapter(myListAdapter);

        //여기까지=====================================================

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(CommunityActivity.this,BoardDetailActivity.class);
                intent.putExtra("title", list_itemArrayList.get(position).getTitle());
                intent.putExtra("content", list_itemArrayList.get(position).getContent());
                intent.putExtra("user", list_itemArrayList.get(position).getUser());
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "터치인식", Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "터치인식", Toast.LENGTH_LONG).show();
    }

    public ArrayList<HashMap<String, String>> parsing(String receivedData) {
        String _id = null;
        String boardid = null;
        String title = null;
        String user = null;
        String content = null;
        String comment = null;

        HashMap<String, String> hashMap;
        ArrayList<HashMap<String, String>> parsedItems = new ArrayList<HashMap<String, String>>();
        JSONObject sample;
        try {
            //JSONObject jsonObject = new JSONObject(receivedData);
            JSONArray jsonArray = new JSONArray(receivedData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                hashMap = new HashMap<>();//초기화

                hashMap.put("_id", i+"번째유저");//item.getString("_id")
                hashMap.put("boardid", item.getString("boardid"));
                hashMap.put("title", item.getString("title"));
                hashMap.put("user", item.getString("user"));
                hashMap.put("content", item.getString("content"));
                hashMap.put("comment", item.getString("comment"));
                parsedItems.add(hashMap);
            }


        } catch (JSONException e) {

        }

        return parsedItems;
    }

    //비동기처리
    public class Task extends AsyncTask<String, Void, String> {

        String clientKey = "#########################";;
        private String str, receiveMsg;
        private final String ID = "########";

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("http://13.209.15.179:50000/user/test1/board");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                conn.setRequestProperty("x-waple-authorization", clientKey);

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("receiveMsg : ", receiveMsg);

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println(s+"*************");
            MAP = parsing(s);

            for (int k=0; k< MAP.size(); k++)
            {
                list_itemArrayList.add(
                        new list_item(R.mipmap.ic_launcher,
                                new Date(),
                                (String)MAP.get(k).get("_id"),
                                (String)MAP.get(k).get("boardid"),
                                (String)MAP.get(k).get("title"),
                                (String)MAP.get(k).get("user"),
                                (String)MAP.get(k).get("content"),
                                (String)MAP.get(k).get("comment")));
            }
            myListAdapter = new MyListAdpater(CommunityActivity.this, list_itemArrayList);
            listView.setAdapter(myListAdapter);

        }

    }
}