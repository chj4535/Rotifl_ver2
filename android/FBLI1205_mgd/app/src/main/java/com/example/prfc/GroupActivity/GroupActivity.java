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
import com.google.gson.JsonObject;

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

    public static final int RequestGroupList = 100;
    public static final int RequestLeave = 101;

    private Button enter;

    private RecyclerView mMainRecyclerView;
    private GroupListAdapter mAdapter;
    public List<Board> mBoardList = new ArrayList<>();

    private String username;
    FirebaseUser user;

    Boolean isLookup;
    String userid;
    String groupid;

    ArrayList<HashMap<String, String>> parsedItems = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private static String TAG = "show Group list";
    int i = 0;
    boolean connectionFail = false;

    ArrayList<Mate> testList;

    public interface ListBtnClickListener {
        void onListBtnClick(int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportActionBar().setElevation(0);
        setTitle("그룹 리스트");

        user = FirebaseAuth.getInstance().getCurrentUser();
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
                /*
                mBoardList.add(new Board(intent.getStringExtra("groupid"), intent.getStringExtra("groupName"), intent.getStringExtra("location"), "testname", intent.getStringArrayListExtra("invitedUsers")));
                mAdapter = new GroupListAdapter(mBoardList);
                mMainRecyclerView.setAdapter(mAdapter);
                */
                if(connectionFail == true) {
                    mBoardList.add(new Board("2", "testname", "testarea", username, testList));
                    mAdapter = new GroupListAdapter(mBoardList);
                    mMainRecyclerView.setAdapter(mAdapter);
                }
                else{
                Connection connection = new Connection();
                connection.execute("RequestGroupList");
                }
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


    public ArrayList parsing(String receivedData) {

        String groupid = "groupid";
        String groupname = "groupname";
        String grouparea = "grouparea";
        String groupstart = "groupstart";
        String groupend = "groupend";

        ArrayList invitedUsers = new ArrayList();
        ArrayList invitedUserList = new ArrayList();
        ArrayList result = new ArrayList();
        parsedItems = new ArrayList();

        try {
            JSONArray jsonArray = new JSONArray(receivedData);
            System.out.println("*****************************Look up receivedData :" + receivedData);
            JSONObject jsonObject;
            JSONObject groupinfo;
            JSONArray mates;
            hashMap = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                groupinfo = jsonObject.getJSONObject("groupinfo");

                hashMap.put(groupname, groupinfo.get(groupname).toString());
                hashMap.put(grouparea, groupinfo.get(grouparea).toString());
                hashMap.put(groupstart, groupinfo.get(groupstart).toString());
                hashMap.put(groupend, groupinfo.get(groupend).toString());
                hashMap.put(groupid, groupinfo.get(groupid).toString());

                mates = jsonObject.getJSONArray("invitedUsers");

                JSONObject mate;
                for(int j = 0; j< mates.length(); j++){
                    mate = mates.getJSONObject(j);
                    if(!mate.getString("email").equals(user.getEmail()))
                        invitedUsers.add(new Mate(mate.getString("name"), mate.getString("email")));
                }

                parsedItems.add(hashMap);
                invitedUserList.add(invitedUsers);
                invitedUsers = new ArrayList();
                hashMap = new HashMap<>();
            }

            result.add(parsedItems);
            result.add(invitedUserList);
        }
        catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

        return result;
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
            holder.area.setText(data.getContent());
        }

        @Override
        public int getItemCount() {
            return mBoardList.size();
        }

        class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mTitleTextView;
            private TextView mNameTextView;
            private TextView area;

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
                area = itemView.findViewById(R.id.group_item_area);
            }


            @Override
            public void onClick(View v) {
                int position = getPosition();

                Intent intent = new Intent(v.getContext(), GroupMenuActivity.class);
                intent.putExtra("group", mBoardList.get(position));

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
            ArrayList parsed;
            ArrayList invitedUserList = new ArrayList();

            HashMap<String, String> item;

            //if(result == "ERROR")

            Toast.makeText(GroupActivity.this, result, Toast.LENGTH_SHORT).show();
            System.out.println("*************************Group lookup result");

            if(result.equals("Error: Failed to connect to /13.209.15.179:50000"))
            {
                connectionFail = true;
                testList = new ArrayList<>();
                testList.add(new Mate("임재원", "gift21cna@gmail.com"));
                testList.add(new Mate("KIL WOO GEUN", "rlfdnrms@gmail.com"));
            }

            else{
            if(isLookup == true){
                parsed = parsing(result);
                parsedItems = (ArrayList<HashMap<String, String>>)parsed.get(0);
                invitedUserList = (ArrayList)parsed.get(1);
                mBoardList = new ArrayList<>();
                for (int i = 0; i < parsedItems.size(); i++) {
                    item = parsedItems.get(i);
                    mBoardList.add(new Board(item.get("groupid"), item.get("groupname"), item.get("grouparea"), username, (ArrayList)invitedUserList.get(i)));//일단 유저 이름.

                }

                mAdapter = new GroupListAdapter(mBoardList);
                mMainRecyclerView.setAdapter(mAdapter);
            }
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
                return connDELETE(serverURL);
            }

            else
                return "ERROR";

        }

        //Delete
        public String connDELETE(String serverURL, String... params){

            int responseStatusCode;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                responseStatusCode = httpURLConnection.getResponseCode();
                System.out.println("************************************** delete ResponseCode " + responseStatusCode);

                return "Delete Success";

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
                httpURLConnection.setRequestProperty("Accept", "application/json");
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