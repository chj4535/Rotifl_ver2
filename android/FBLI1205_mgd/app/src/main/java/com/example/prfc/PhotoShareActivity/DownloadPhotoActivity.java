package com.example.prfc.PhotoShareActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prfc.Classes.ImageList;
import com.example.prfc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DownloadPhotoActivity extends AppCompatActivity {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference strpath = ref.child("images").child("groupname").child("images");
    private FirebaseStorage storage;
    private ImageListAdapter mAdapter;
    private List<ImageList> mBoardList = new ArrayList<>();
    private StorageReference storageReference;
    private RecyclerView mMainRecyclerView;
    //private ArrayList<String> path = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_photo);

        mMainRecyclerView = findViewById(R.id.image_recycler_view);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        strpath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String pth = d.getKey();
                    StorageReference ref = storageReference.child("images").child(pth);
                    Log.d("asdasd", "dbreadres = " + pth);

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            mBoardList.add(new ImageList(uri, pth));
                            mAdapter = new ImageListAdapter(mBoardList);
                            mMainRecyclerView.setAdapter(mAdapter);
                            Log.d("asdasd", "dbreadres = " + uri.toString());
                            Log.d("asdasd", "dbreadres size= " + mBoardList.size());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

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
            new Thread(){
                public void run(){
                    ImageList data = mBoardList.get(position);
                    try {
                        Log.d("vvvv", "inasdasd error = " + data.getUri());
                        URL  url = new URL(data.getUri().toString());
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                        holder.mImageView.setImageBitmap(bitmap);
                    }catch (Exception e){
                        Log.d("vvvv", "inasdasd error = " + e);
                    }
                    holder.mNameView.setText(data.getName());
                }
            }.start();
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
            private Button btnDownload;

            public GroupListViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(this);
                mImageView = itemView.findViewById(R.id.imageviewer);
                mNameView  = itemView.findViewById(R.id.textviewer);
                btnDownload  = itemView.findViewById(R.id.downloadbtn);

                //다운로드 버튼 눌렀을때 실행되게 할것.
                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent intent = new Intent(PhotoMainActivity.this, PhotoShareActivity.class);
                        //intent.putExtra("groupid", groupid);
                        //startActivity(intent);
                    }
                });
            }

            @Override
            public void onClick(View v){ //클릭됐을때 수정하도록 함.
            }
        }

    }
}
