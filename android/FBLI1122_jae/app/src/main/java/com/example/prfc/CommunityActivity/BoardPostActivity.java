package com.example.prfc.CommunityActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.provider.MediaStore.Images;
import android.app.Activity;
import com.example.prfc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

public class BoardPostActivity extends AppCompatActivity{
    EditText title;
    EditText content;
    ImageView imageview;
    Button btn_upload;
    FirebaseUser user;
    Button Btn_select;

    //private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름

    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE=1112;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);
        title = (EditText) findViewById(R.id.titleInput);
        content = (EditText) findViewById(R.id.contentInput);
        imageview = (ImageView)findViewById(R.id.imageInput);
        Btn_select = (Button)findViewById(R.id.selectPhoto);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        user = FirebaseAuth.getInstance().getCurrentUser();
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                base64_string = getStringFromBitmap(imageview.getDrawingCache());
                new PostTask().execute();
                startActivity(new Intent(BoardPostActivity.this, CommunityActivity.class));
            }
        });
        Btn_select.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectGallery();
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
    public class ImagePost extends AsyncTask<String, Void, String> {

        String clientKey = "#########################";;
        private String str, receiveMsg;
        private final String ID = "########";

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "";

            serverURL = "http://13.209.15.179:50000/upload";
            return connPOST(serverURL);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //File image =
        }
    }

    //Delete
    public String connPOST(String serverURL, String... params){

        String baseURL = "http://13.209.15.179:50000/";
        JSONObject jsonObject = new JSONObject();
        int responseStatusCode;

        try {
//
//            jsonObject.put("_id","");
//            jsonObject.put("boardid","");
            jsonObject.put("title",title.getText().toString());
            jsonObject.put("userid",user.getUid());
            jsonObject.put("content",content.getText().toString());
//            jsonObject.put("comment","");
//            jsonObject.put("Image","");

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

    private void selectGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

//        if(requestCode == REQ_CODE_SELECT_IMAGE)
//        {
            if(resultCode==Activity.RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    String name_Str = getImageNameToUri(data.getData());

                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= Images.Media.getBitmap(getContentResolver(), data.getData());

                    //배치해놓은 ImageView에 set
                    imageview.setImageBitmap(image_bitmap);


                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();


                }
                 catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
//        }
    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

}
