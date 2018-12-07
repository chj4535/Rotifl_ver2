package com.example.prfc.ChattingActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.prfc.Classes.AppData;
import com.example.prfc.Classes.Board;
import com.example.prfc.Classes.MessageList;
import com.example.prfc.Classes.MyMessageStatusFormatter;
import com.example.prfc.Classes.User;
import com.example.prfc.GroupActivity.Mate;
import com.example.prfc.R;
import com.github.bassaer.chatmessageview.model.IChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.github.bassaer.chatmessageview.view.MessageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MessengerActivity extends Activity {

    @VisibleForTesting
    protected static final int RIGHT_BUBBLE_COLOR = R.color.colorPrimaryDark;
    @VisibleForTesting
    protected static final int LEFT_BUBBLE_COLOR = R.color.gray300;
    @VisibleForTesting
    protected static final int BACKGROUND_COLOR = R.color.blueGray400;
    @VisibleForTesting
    protected static final int SEND_BUTTON_COLOR = R.color.blueGray500;
    @VisibleForTesting
    protected static final int SEND_ICON = R.drawable.ic_action_send;
    @VisibleForTesting
    protected static final int OPTION_BUTTON_COLOR = R.color.teal500;
    @VisibleForTesting
    protected static final int RIGHT_MESSAGE_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int LEFT_MESSAGE_TEXT_COLOR = Color.BLACK;
    @VisibleForTesting
    protected static final int USERNAME_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int SEND_TIME_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int DATA_SEPARATOR_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int MESSAGE_STATUS_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final String INPUT_TEXT_HINT = "New message..";
    @VisibleForTesting
    protected static final int MESSAGE_MARGIN = 5;

    private ChatView mChatView;
    private MessageList mMessageList;
    private ArrayList<User> mUsers;
    Board group;
    AppData appData;
    String groupid;
    FirebaseUser user;
    ArrayList<Mate> invitedUsers;
    Socket socket;

    InputMethodManager inputMethodManager;

    private int mReplyDelay = -1;

    private static final int READ_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        group = getIntent().getParcelableExtra("group");
        groupid = group.getId();
        invitedUsers = group.getInvitedUsers();

        System.out.println("*************messengerActivity group id = "+ groupid);

        user = FirebaseAuth.getInstance().getCurrentUser();

        initUsers();

        Connection connection = new Connection();
        connection.execute();

        mChatView = findViewById(R.id.chat_view);
        if(groupid.equals("fail")){
            Toast.makeText(this, "인터넷 연결 안됌", Toast.LENGTH_SHORT).show();
        }

        //카드 생성
        mChatView.getMessageView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                    Message message = (Message)adapterView.getItemAtPosition(position);
                    Intent intent = new Intent(MessengerActivity.this, MakeCardActivity.class);
                    intent.putExtra("content", message.getText());
                    startActivity(intent);

                return false;
            }
        });

        //Load saved messages
        loadMessages();

        //Set UI parameters if you need
        mChatView.setRightBubbleColor(ContextCompat.getColor(this,RIGHT_BUBBLE_COLOR));
        mChatView.setLeftBubbleColor(ContextCompat.getColor(this, LEFT_BUBBLE_COLOR));
        mChatView.setBackgroundColor(ContextCompat.getColor(this, BACKGROUND_COLOR));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, SEND_BUTTON_COLOR));
        mChatView.setSendIcon(SEND_ICON);
        mChatView.setOptionIcon(R.drawable.ic_account_circle);
        mChatView.setOptionButtonColor(OPTION_BUTTON_COLOR);
        mChatView.setRightMessageTextColor(RIGHT_MESSAGE_TEXT_COLOR);
        mChatView.setLeftMessageTextColor(LEFT_MESSAGE_TEXT_COLOR);
        mChatView.setUsernameTextColor(USERNAME_TEXT_COLOR);
        mChatView.setSendTimeTextColor(SEND_TIME_TEXT_COLOR);
        mChatView.setDateSeparatorColor(DATA_SEPARATOR_COLOR);
        mChatView.setMessageStatusTextColor(MESSAGE_STATUS_TEXT_COLOR);
        mChatView.setInputTextHint(INPUT_TEXT_HINT);
        mChatView.setMessageMarginTop(MESSAGE_MARGIN);
        mChatView.setMessageMarginBottom(MESSAGE_MARGIN);
        mChatView.setMaxInputLine(5);
        mChatView.setUsernameFontSize(getResources().getDimension(R.dimen.font_small));
        mChatView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mChatView.setInputTextColor(ContextCompat.getColor(this, R.color.black));
        mChatView.setInputTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


        mChatView.setOnBubbleClickListener(new Message.OnBubbleClickListener() {
            @Override
            public void onClick(Message message) {
                mChatView.updateMessageStatus(message, MyMessageStatusFormatter.STATUS_SEEN);
                Toast.makeText(
                        MessengerActivity.this,
                        "click : " + message.getUser().getName() + " - " + message.getText(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        mChatView.setOnIconClickListener(new Message.OnIconClickListener() {
            @Override
            public void onIconClick(Message message) {
                Toast.makeText(
                        MessengerActivity.this,
                        "click : icon " + message.getUser().getName(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        mChatView.setOnIconLongClickListener(new Message.OnIconLongClickListener() {
            @Override
            public void onIconLongClick(Message message) {
                Toast.makeText(
                        MessengerActivity.this,
                        "Removed this message \n" + message.getText(),
                        Toast.LENGTH_SHORT
                ).show();
                mChatView.getMessageView().remove(message);
            }
        });

        //Click Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initUsers();

                //new message
                //Toast.makeText(MessengerActivity.this, mChatView.getInputText(), Toast.LENGTH_SHORT).show();

                JsonObject preJsonObject = new JsonObject();
                preJsonObject.addProperty("comment", mChatView.getInputText()+"");
                preJsonObject.addProperty("channel", groupid);
                preJsonObject.addProperty("email", user.getEmail());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(preJsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("*************************Messenger send json :" + jsonObject);
                socket.emit("send", jsonObject);

                Message message = new Message.Builder()
                        .setUser(mUsers.get(0))//유저 설정
                        .setRight(true)//오른쪽 방향에 뜸
                        .setText(mChatView.getInputText())
                        .hideIcon(true)
                        .setStatusIconFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                        .setStatusTextFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                        .setStatusStyle(Message.Companion.getSTATUS_ICON())
                        .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                        .build();


                //Set to chat view
                mChatView.send(message);
                //Add message list
                mMessageList.add(message);
                //Reset edit text
                mChatView.setInputText("");

                //receiveMessage("Text", "rlfdnrms@gmail.com");

            }

        });

        //Click option button
        mChatView.setOnClickOptionButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void hideKeyboard(){
        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mChatView.getMessageView().getWindowToken(), 2);
        mChatView.getMessageView().requestFocus();
    }


    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void receiveMessage(String Text, String email) {

        System.out.println("**********************sender email :" + findSender(email.replace("\"","")).getName());
        //Receive message
        final Message receivedMessage = new Message.Builder()
                .setUser(findSender(email.replace("\"","")))
                .setRight(false)
                .setText(Text)
                .setStatusIconFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                .setStatusTextFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                .setStatusStyle(Message.Companion.getSTATUS_ICON())
                .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                .build();

        mChatView.receive(receivedMessage);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != READ_REQUEST_CODE || resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri uri = data.getData();
        try {
            Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            Message message = new Message.Builder()
                    .setRight(true)
                    .setText(Message.Type.PICTURE.name())
                    .setUser(mUsers.get(0))
                    .hideIcon(true)
                    .setPicture(picture)
                    .setType(Message.Type.PICTURE)
                    .setStatusIconFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                    .setStatusStyle(Message.Companion.getSTATUS_ICON())
                    .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                    .build();
            mChatView.send(message);
            //Add message list
            mMessageList.add(message);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

    }

    private User findSender(String email){
        int i;

        for(i = 0; i<mUsers.size();i++){
            if(mUsers.get(i).getId().equals(email)){
                System.out.println("********find sender :"+mUsers.get(i).getId());
                break;
            }
        }
        //못 찾을 경우 마지막 동료가 반환됌.
        return mUsers.get(i);
    }

    private void initUsers() {
        mUsers = new ArrayList<>();

        String myId = user.getEmail();
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.default_person_icon);
        String myName = user.getDisplayName();

        final User me = new User(myId, myName, myIcon);
        mUsers.add(me);

        Bitmap DefaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.default_person_icon);

        for(int i = 0; i<invitedUsers.size();i++){
            if(!invitedUsers.get(i).getEmail().equals(myId))
                mUsers.add(new User(invitedUsers.get(i).getEmail(), invitedUsers.get(i).getName(), DefaultIcon));
        }
        for(int i = 0; i<mUsers.size();i++) {
            System.out.println("******************User :" + mUsers.get(i).getId());
        }
    }

    /**
     * Load saved messages
     */
    private void loadMessages() {
        List<Message> messages = new ArrayList<>();

        appData = new AppData(groupid.toString());
        mMessageList = appData.getMessageList(this);
        if (mMessageList == null) {
            mMessageList = new MessageList();
        } else {
            for (int i = 0; i < mMessageList.size(); i++) {
                Message message = mMessageList.get(i);
                //Set extra info because they were removed before save messages.
                for (IChatUser user : mUsers) {
                    if (message.getUser().getId().equals(user.getId())) {
                        message.getUser().setIcon(user.getIcon());
                    }
                }
                if (!message.isDateCell() && message.isRight()) {
                    message.hideIcon(true);

                }
                message.setStatusStyle(Message.Companion.getSTATUS_ICON_RIGHT_ONLY());
                message.setStatusIconFormatter(new MyMessageStatusFormatter(this));
                message.setStatus(MyMessageStatusFormatter.STATUS_DELIVERED);
                messages.add(message);
            }
        }
        MessageView messageView = mChatView.getMessageView();
        messageView.init(messages);
        messageView.setSelection(messageView.getCount() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        //initUsers();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Save message
        appData = new AppData(groupid.toString());
        mMessageList = new MessageList();
        mMessageList.setMessages(mChatView.getMessageView().getMessageList());
        appData.putMessageList(this, mMessageList);
    }

    @VisibleForTesting
    public ArrayList<User> getUsers() {
        return mUsers;
    }


    public void setReplyDelay(int replyDelay) {
        mReplyDelay = replyDelay;
    }

    private void showDialog() {
        final String[] items = {
                getString(R.string.send_picture),
                getString(R.string.clear_messages)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.options))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (position) {
                            case 0 :
                                openGallery();
                                break;
                            case 1:
                                mChatView.getMessageView().removeAll();
                                break;
                        }
                    }
                })
                .show();
    }

    class Connection extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);



        }


        @Override
        protected String doInBackground(String... params) {
            String result = " ";

            try {
                socket = IO.socket("http://13.209.15.179:6000");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            socket.on(Socket.EVENT_CONNECT, (Object... objects) -> {
                JsonObject preJsonObject = new JsonObject();
                preJsonObject.addProperty("channel", groupid);
                preJsonObject.addProperty("userid", user.getUid());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(preJsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("joinRoom",jsonObject);
            }).on("recMsg", (Object... objects) -> {
                JsonParser jsonParsers = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParsers.parse(objects[0] + "");
                System.out.println("*************message json : "+jsonObject.toString());
                JsonObject jsonObject2 = (JsonObject) jsonObject.getAsJsonObject("comment");
                //JsonObject jsonObject3 = (JsonObject) jsonObject.getAsJsonObject("userid");

                String email = jsonObject2.get("email").toString();
                email = email.replace("\"","");
                System.out.println("****************************Uid from server :" + email+"\n local user id :" + user.getEmail());
                if(!email.equals(user.getEmail())) {
                    String finalEmail = email;
                    runOnUiThread(() -> {
                        receiveMessage(jsonObject2.get("msg").toString().replace("\"",""), finalEmail);//이거 바꿔줘야한다. json 에서 email 뽑아서 넣어준다.
                    });
                }

            });
            socket.connect();

            return result;
        }
    }
}
