package com.example.prfc.GroupActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prfc.Classes.Board;
import com.example.prfc.R;
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

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button enter;

    private RecyclerView mMainRecyclerView;
    private GroupListAdapter mAdapter;
    private List<Board> mBoardList = new ArrayList<>();

    private String username;

    Boolean isLookup;
    String userid;
    String groupid;

    ArrayList<HashMap<String, String>> parsedItems = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private static String TAG = "show Group list";
    int i = 0;

    public interface ListBtnClickListener {
        void onListBtnClick(int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setTitle("Group List");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        username = user.getDisplayName();//구글 로그인은 이렇게 유저 네임 받는거 가능, 이메일 로그인은 setname할떄 db에 저장한다음에 거기서 읽어와야됨
        userid = user.getUid();

        Connection connection = new Connection();
        connection.execute("RequestGroupList");

        mMainRecyclerView = findViewById(R.id.main_recycler_view);

        findViewById(R.id.main_write_button).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(GroupActivity.this, MakeGroupActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //onclicklistener 그룹 추가
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                mBoardList.add(new Board(intent.getStringExtra("groupid"), intent.getStringExtra("groupName"), intent.getStringExtra("location"), "testname"));
                mAdapter = new GroupListAdapter(mBoardList);
                mMainRecyclerView.setAdapter(mAdapter);

            }
        }
        //onlongclicklistener 그룹 탈퇴 및 변경
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                int position = intent.getIntExtra("position", 9999);
                if (position < 9999) {

                    int which = intent.getIntExtra("which", 9999);
                    //그룹 탈퇴
                    if (which == 1) {
                        groupid = mBoardList.get(position).getId();
                        System.out.println("***********************board hi delete, position = "+position);
                        Connection conn = new Connection();
                        conn.execute("RequestLeave", groupid, userid);
                        mBoardList.remove(position);
                        mAdapter = new GroupListAdapter(mBoardList);
                        mMainRecyclerView.setAdapter(mAdapter);
                    }
                    //그룹 변경
                    else if (which == 2) {
                        Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
                    }

                } else
                    Toast.makeText(GroupActivity.this, "position error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public ArrayList<HashMap<String, String>> parsing(String receivedData) {

        String groupid = "groupid";
        String groupname = "groupname";
        String grouparea = "grouparea";
        String groupstart = "groupstart";
        String groupend = "groupend";
        String groupuser = "groupuser";

        try {
            JSONArray jsonArray = new JSONArray(receivedData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                hashMap = new HashMap<>();

                hashMap.put(groupname, item.getString(groupname));
                hashMap.put(grouparea, item.getString(grouparea));
                hashMap.put(groupstart, item.getString(groupstart));
                hashMap.put(groupend, item.getString(groupend));
                hashMap.put(groupid, item.getString(groupid));

                parsedItems.add(hashMap);
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

        return parsedItems;
    }

    private class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder> {

        // 리스트 생성
        private List<Board> mBoardList;

        public GroupListAdapter(List<Board> mBoardList) {
            this.mBoardList = mBoardList;
        }

        @NonNull
        @Override
        public GroupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            // 버튼눌렀을때 추가 되는 박스
            return new GroupListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull GroupListViewHolder holder, int position) {
            Board data = mBoardList.get(position);
            holder.mTitleTextView.setText(data.getTitle());
            holder.mNameTextView.setText(data.getName());
        }

        @Override
        public int getItemCount() {
            return mBoardList.size();
        }

        class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mTitleTextView;
            private TextView mNameTextView;

            public GroupListViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent intent = new Intent(GroupActivity.this, GroupItemPopupActivity.class);
                        intent.putExtra("position", getPosition());
                        intent.putExtra("user_id", "");
                        intent.putExtra("group_id", "");
                        startActivityForResult(intent, 2);

                        return false;
                    }
                });

                mTitleTextView = itemView.findViewById(R.id.item_title_text);
                mNameTextView = itemView.findViewById(R.id.item_name_text);
            }


            @Override
            public void onClick(View v) {
                int position = getPosition();

                System.out.println("***************************To groupmenuactivity position = " + position);
                Intent intent = new Intent(v.getContext(), GroupMenuActivity.class);
                intent.putExtra("groupid", mBoardList.get(position).getId());
                v.getContext().startActivity(intent);
            }
        }

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
            //그룹 불러오기
            HashMap<String, String> item;

            //if(result == "ERROR")

            Toast.makeText(GroupActivity.this, result, Toast.LENGTH_SHORT).show();

            if(isLookup == true){
                parsedItems = parsing(result);
                System.out.println("***********************result" + result);

                for (int i = 0; i < parsedItems.size(); i++) {
                    item = parsedItems.get(i);
                    mBoardList.add(new Board(item.get("groupid"), item.get("groupname"), null, "testname"));
                }

                mAdapter = new GroupListAdapter(mBoardList);
                mMainRecyclerView.setAdapter(mAdapter);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "";

            if(params[0] == "RequestGroupList") {
                //Toast.makeText(GroupActivity.this, "list request", Toast.LENGTH_SHORT).show();
                isLookup = true;
                serverURL = "http://13.209.15.179:50000/user/" + userid;
                return connGET(serverURL);
            }

            else if(params[0] == "RequestLeave")
            {
                //Toast.makeText(GroupActivity.this, "delete request", Toast.LENGTH_SHORT).show();
                isLookup = false;
                groupid = params[1];
                userid = params[2];
                serverURL = "http://13.209.15.179:50000/user/"+ userid + "/group/"+ groupid;
                return connPOST(serverURL);
            }

            else
                return "ERROR";

        }

        //Delete
        public String connPOST(String serverURL, String... params){
            JSONObject jsonObject = new JSONObject();
            int responseStatusCode;

            /*try {
                jsonObject.put("groupid", params[0]);
                jsonObject.put("userid", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                responseStatusCode = httpURLConnection.getResponseCode();
                System.out.println("************************************** delete ResponseCode " + responseStatusCode);

                return "Delete(POST) Success";

            } catch (Exception e) {

                return new String("Delete Error: " + e.getMessage());
            }
        }

        //조회
        public String connGET(String serverURL) {
            int responseStatusCode;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                responseStatusCode = httpURLConnection.getResponseCode();
                System.out.println("**************************************look up ResponseCode " + responseStatusCode);

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

                return new String("Error: " + e.getMessage());
            }
        }

    }
}