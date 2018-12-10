package com.example.prfc.CommunityActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.DataOutputStream;
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

    String imgPath ="";
    String imgName="";

    int serverResponseCode = 0;
    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE=1112;
    final int REQ_CODE_SELECT_IMAGE=100;
    File imgFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);
        getSupportActionBar().setElevation(0);
        setTitle("글작성");
        title = (EditText) findViewById(R.id.titleInput);
        content = (EditText) findViewById(R.id.contentInput);
        imageview = (ImageView)findViewById(R.id.imageInput);
        Btn_select = (Button)findViewById(R.id.selectPhoto);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        user = FirebaseAuth.getInstance().getCurrentUser();

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostTask().execute();
//                base64_string = getStringFromBitmap(imageview.getDrawingCache());
                new Thread() {
                    public void run() {
                        doFileUpload();
                    }
                }.start();
                startActivity(new Intent(BoardPostActivity.this,CommunityActivity.class));

            }
        });
        Btn_select.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectGallery();

            }
        });
    }

    //Post 데이터
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
    //Post 이미지
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
            doFileUpload();
        }
    }

    //데이터 포맷만들어서 보내기
    public String connPOST(String serverURL, String... params){

        String baseURL = "http://13.209.15.179:50000/down/";
        JSONObject jsonObject = new JSONObject();
        int responseStatusCode;

        try {

            jsonObject.put("title",title.getText().toString());
            jsonObject.put("userid",user.getUid());
            jsonObject.put("content",content.getText().toString());
            jsonObject.put("image",baseURL+imgName);

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



    //갤러리 접근
    private void selectGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }
    @Override //갤러리 접근 후 동작
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();
        if(resultCode==Activity.RESULT_OK)
        {
            try {
                //Uri에서 이미지 이름을 얻어온다.
                String name_Str = getImageNameToUri(data.getData());
                //이미지 데이터를 비트맵으로 받아온다.
                Bitmap image_bitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
                //배치해놓은 ImageView에 set
                imageview.setImageBitmap(image_bitmap);
                Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                imgFile = new File(imgPath);
                if(!imgFile.isFile()){
                    System.out.println("****************************_파일없음");
                }
                else{
                    System.out.println("****************************_"+imgFile.getName());
                }
            }
                 catch (Exception e)
                {
                    e.printStackTrace();
                }
        }

    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);
        imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    public void doFileUpload() {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        System.out.println("doFileUpload");
            try {
                System.out.println("********************************1  "+imgPath+"엥");
                System.out.println("********************************2"+Environment.getExternalStorageDirectory().getAbsolutePath());
                FileInputStream mFileInputStream = new FileInputStream(imgFile);
                System.out.println("********************************2");
                URL connectUrl = new URL("http://13.209.15.179:50000/upload");
                Log.d("Test", "mFileInputStream  is " + mFileInputStream);

                // open connection
                HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                // write data
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\"" + imgName+"\"" + lineEnd);
                dos.writeBytes(lineEnd);

                int bytesAvailable = mFileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];
                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                Log.d("Test", "image byte is " + bytesRead);

                // read image
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = mFileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                }
                System.out.println("********************************2");
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                Log.e("Test" , "File is written");
                mFileInputStream.close();
                dos.flush(); // finish upload...
                System.out.println("********************************13");
                // get response
                int ch;
                InputStream is = conn.getInputStream();
                StringBuffer b =new StringBuffer();
                while( ( ch = is.read() ) != -1 ){
                    b.append( (char)ch );
                }
                String s=b.toString();
                Log.e("Test", "result = " + s);
                //mEdityEntry.setText(s);
                dos.close();

            } catch (FileNotFoundException e){
                System.out.println(e.getStackTrace());
                System.out.println("파일없어*********************************************");
            }catch (Exception e) {
                Log.d("Test", "exception " + e.getMessage());

                System.out.println(e.toString()+"다른거야*********************************************");
                // TODO: handle exception
            }

        }



}

