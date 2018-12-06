package com.example.prfc.PhotoShareActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prfc.Classes.ImageList;
import com.example.prfc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DownloadPhotoActivity extends AppCompatActivity {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference strpath = ref.child("images").child("groupname").child("images");
    private FirebaseStorage storage;
    private ImageListAdapter mAdapter;
    private TextView ifnull, dirview;
    private List<ImageList> mBoardList = new ArrayList<>();
    private StorageReference storageReference;
    private RecyclerView mMainRecyclerView;
    private Bitmap bitmap;
    private String DIR;
    private String userid;;
    private ArrayList<String> path = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_photo);

        userid = FirebaseAuth.getInstance().getUid();
        ifnull = findViewById(R.id.showifnull);
        mMainRecyclerView = findViewById(R.id.image_recycler_view);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAdapter = new ImageListAdapter(mBoardList);
        mMainRecyclerView.setAdapter(mAdapter);
        dirview = (TextView)findViewById(R.id.dirviewer);
        DIR = Environment.getExternalStorageDirectory().toString();

        dirview.setText(DIR + "/" + "groupname");
        verifyStoragePermissions(DownloadPhotoActivity.this);

        strpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int i = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String pth = d.getKey();
                    StorageReference ref = storageReference.child("images").child(pth);
                    Log.d("asdasd", "dbreadres = " + pth);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            mBoardList.add(new ImageList(uri, pth));
                            mAdapter.notifyDataSetChanged();
                            Log.d("asdasd", "dbreadres = " + uri.toString());
                            Log.d("asdasd", "dbreadres size= " + mBoardList.size());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                    i++;
                }
                if(i==0){
                    ifnull.setText("사진이 없습니다");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class ImageListAdapter extends RecyclerView.Adapter <ImageListAdapter.GroupListViewHolder> {


        // 리스트 생성
        private List<ImageList> mBoardList;


        public ImageListAdapter(List<ImageList> mBoardList) {
            this.mBoardList = mBoardList;
        }

        public ImageListAdapter(){

        }
        @NonNull
        @Override
        public GroupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            // 버튼눌렀을때 추가 되는 박스
            return new GroupListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull GroupListViewHolder holder, int position) {

            new Thread(new Runnable(){
                @Override
                public void run(){
                    ImageList data = mBoardList.get(position);
                    holder.mNameView.setText(data.getName());
                    try{
                        URL  url = new URL(data.getUri().toString());
                        bitmap = BitmapFactory.decodeStream(url.openStream());
                    }catch (Exception e){
                        Log.d("vvvv", "inasdasd error = " + e);
                    }
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                Log.d("vvvv", "inasdasd error = " + data.getUri());

                                holder.mImageView.setImageBitmap(bitmap);

                                holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) { //다운로드 시작
                                        downloadImage(data);
                                    }
                                });
                                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        holder.btnDelete.setClickable(false);
                                        StorageReference storageRef = storage.getReference();
                                        strpath.child(data.getName()).removeValue();
                                        StorageReference deleteRef = storageRef.child("images").child(data.getName());
                                        deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mBoardList.remove(position);
                                                mAdapter.notifyDataSetChanged();
                                                if(mBoardList.size()==0){
                                                    ifnull.setText("사진이 없습니다");
                                                }
                                            }
                                        });
                                    }
                                });
                            }catch (Exception e){
                                Log.d("vvvv", "inasdasd error = " + e);
                            }
                        }
                    });
                }
            }).start();
        }


        //리스트 생성갯수
        @Override
        public int getItemCount() {
            return mBoardList.size();
        }


        // 데이터를 어댑터를 통해서 recycler 뷰에 보여줌
        class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView mImageView;
            private TextView mNameView;
            private Button btnDownload, btnDelete;

            public GroupListViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);
                mImageView = itemView.findViewById(R.id.imageviewer);
                mNameView  = itemView.findViewById(R.id.textviewer);
                btnDownload  = itemView.findViewById(R.id.downloadbtn);
                btnDelete = itemView.findViewById(R.id.deletebtn);
            }

            @Override
            public void onClick(View v){ //클릭됐을때 수정하도록 함.
            }
        }

    }

    private void downloadImage(ImageList data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading...");
        progressDialog.show();

        if(DIR.equals("")){
            Intent intent = new Intent(DownloadPhotoActivity.this, InformActivity.class);
            startActivity(intent);
        }else{
            StorageReference storageRef = storage.getReference();
            StorageReference downloadRef = storageRef.child("images").child(data.getName());
            //Log.d("vvvv", "inasdasd download ref = " + downloadRef.toString());
            try{
                File rootPath = new File(DIR, "groupname");
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                final File fileNameOnDevice = new File(rootPath,data.getName());

                Log.d("vvvv", "inasdasd rootpath = " + rootPath.getAbsolutePath());
                fileNameOnDevice.createNewFile();
                downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot task) {
                        Toast.makeText(getApplicationContext(), "File Downloaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("vvvv", "inasdasd error = dd");
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Downloaded "+(int)progress+"%");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.d("vvvv", "inasdasd error = ee");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "file download failed : " + e, Toast.LENGTH_SHORT).show();
                        //Log.d("asdasd", "inasdasd error = " + e.getMessage());
                        // ...
                    }
                });
            }catch(Exception e){
                Log.d("vvvv", "inasdasd error = " + e);
            }

        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
