package com.example.prfc.PhotoShareActivity;

import android.content.Intent;
import android.media.Image;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DownloadPhotoActivity extends AppCompatActivity {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference strpath = ref.child("images").child("groupname").child("images");
    private ImageListAdapter mAdapter;
    private List<ImageList> mBoardList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_photo);

        ArrayList<String> path = new ArrayList<>();
        strpath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String pth = d.getKey();
                    Log.d("asdasd", "dbreadres = " + pth);
                    path.add(pth);
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
            return new GroupListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull GroupListViewHolder holder, int position) {
            ImageList data = mBoardList.get(position);
            //holder.mImageView.setImageURI(data.getImage());
            holder.mNameView.setText(data.getName());
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
        } // 어댑터를 통해서 무엇을 보낼건지

    }
}
