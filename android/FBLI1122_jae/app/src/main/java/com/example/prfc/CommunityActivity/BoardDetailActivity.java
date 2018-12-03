//package com.example.prfc.CommunityActivity;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.example.prfc.R;
//
//public class BoardDetailActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_board_detail);
//    }
//}
package com.example.prfc.CommunityActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.prfc.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import  android.util.Base64;
import java.util.Date;

public class BoardDetailActivity extends AppCompatActivity {
    TextView BoardTitle;// = (TextView)findViewById(R.id.BoradTitle);
    TextView ath;//= (TextView)findViewById(R.id.Athor);
    TextView body;//= (TextView)findViewById(R.id.BoardBody);

    String BitmapData="";

    ImageView mImgTrans;
    Bitmap mBitmap;

    String URL = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        Intent intent =getIntent();
        BoardTitle = (TextView)findViewById(R.id.BoradTitle);
        ath = (TextView)findViewById(R.id.Athor);
        body = (TextView)findViewById(R.id.BoardBody);

        BoardTitle.setText("제목:"+intent.getStringExtra("title"));
        ath.setText("작성자 : "+intent.getStringExtra("user"));
        body.setText(intent.getStringExtra("content"));
        URL = intent.getStringExtra("image");
        System.out.println(URL+"*************************************************");
        mImgTrans = (ImageView) findViewById(R.id.imgTranslate);

        new LoadImage().execute(URL);
    }

    //사진다운로드
    public class  LoadImage extends AsyncTask<String,String, Bitmap> {
        ProgressDialog pDialog;

        @Override
        protected Bitmap doInBackground(String... args) {
            try{

                mBitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return mBitmap;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();;
            pDialog = new ProgressDialog(BoardDetailActivity.this);
            pDialog.setMessage("이미지 로딩중입니다..");
            pDialog.show();
        }
        @Override
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                mImgTrans.setImageBitmap(image);
                pDialog.dismiss();
            }
            else{
//                BitmapData = getStringFromBitmap(image);
                pDialog.dismiss();
                Toast.makeText(BoardDetailActivity.this, "이미지가 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }
//    public class PostTask extends AsyncTask<String, Void, String> {
//
//        String clientKey = "#########################";;
//        private String str, receiveMsg;
//        private final String ID = "########";
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            String serverURL = "";
//
//            serverURL = "http://13.209.15.179:50000/user/test1/board";
//            return connPOST(serverURL);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//        }
//
//    }
//
//
//    //Delete
//    public String connPOST(String serverURL, String... params){
//        JSONObject jsonObject = new JSONObject();
//        int responseStatusCode;
//
//        try {
//
//            URL url = new URL(serverURL);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//            httpURLConnection.setReadTimeout(5000);
//            httpURLConnection.setConnectTimeout(5000);
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.connect();
//
//            responseStatusCode = httpURLConnection.getResponseCode();
//            System.out.println("************************************** delete ResponseCode " + responseStatusCode);
//
//            jsonObject.put("_id","csp");
//            jsonObject.put("boardid","tqtq");
//            jsonObject.put("title","대아");
//            jsonObject.put("user","cs");
//            jsonObject.put("content","Content");
//            jsonObject.put("comment","comment");
//            jsonObject.put("Image",BitmapData);
//
//
//
//            return "Delete(POST) Success";
//
//        } catch (Exception e) {
//
//            return new String("Delete Error: " + e.getMessage());
//        }
//    }
//    private String getStringFromBitmap(Bitmap bitmapPicture) {
//        String encodedImage;
//        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
//        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
//        byte[] b = byteArrayBitmapStream.toByteArray();
//        encodedImage= Base64.encodeToString(b,Base64.DEFAULT);
//        return encodedImage;
//    }

}
