package com.example.prfc.CommunityActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.prfc.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BoardPostActivity extends AppCompatActivity {
    EditText title;
    EditText content;
    ImageView imageview;
    Button btn_upload;
    private String base64_string;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);
        title = (EditText) findViewById(R.id.titleInput);
        content = (EditText) findViewById(R.id.contentInput);
        imageview = (ImageView)findViewById(R.id.imageInput);
        btn_upload = (Button) findViewById(R.id.Btn_upload);


        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base64_string = getStringFromBitmap(imageview.getDrawingCache());
                new PostTask().execute();
                startActivity(new Intent(BoardPostActivity.this, CommunityActivity.class));
            }
        });
    }


    public class PostTask extends AsyncTask<String, Void, String> {

        String clientKey = "#########################";;
        private String str, receiveMsg;
        private final String ID = "########";

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "";

            serverURL = "http://13.209.15.179:50000/user/test1/board";
            return connPOST(serverURL);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    //Delete
    public String connPOST(String serverURL, String... params){
        JSONObject jsonObject = new JSONObject();
        int responseStatusCode;

        try {

            jsonObject.put("_id","");
            jsonObject.put("boardid","");
            jsonObject.put("title",title.getText().toString());
            jsonObject.put("user","");
            jsonObject.put("content",content.getText().toString());
            jsonObject.put("comment","");
            jsonObject.put("Image",base64_string);

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            //httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();


            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes("UTF-8"));

            outputStream.flush();
            outputStream.close();

            responseStatusCode = httpURLConnection.getResponseCode();
            System.out.println("************************************** delete ResponseCode " + responseStatusCode);

            return "Delete(POST) Success";

        } catch (Exception e) {

            return new String("Delete Error: " + e.getMessage());
        }
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage= Base64.encodeToString(b,Base64.DEFAULT);
        return encodedImage;
    }
}
