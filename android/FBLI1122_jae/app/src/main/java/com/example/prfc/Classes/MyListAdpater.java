package com.example.prfc.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.prfc.R;

import java.util.ArrayList;

public class MyListAdpater extends BaseAdapter {
    Context context =null;
    TextView nickname_textView;
    TextView title_textView;
    TextView date_textView;
    TextView content_textView;
    ImageView profile_imageView;
    ArrayList<list_item> list_itemArrayList;
    RelativeLayout relativeLayout;

    public MyListAdpater(Context context, ArrayList<list_item> list_itemArrayList) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
    }

    @Override
    public int getCount() {
        return this.list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item,null);
            nickname_textView = (TextView)convertView.findViewById(R.id.nickname_textview);
            content_textView = (TextView)convertView.findViewById(R.id.content_textview);
            date_textView = (TextView)convertView.findViewById(R.id.date_textview);
            title_textView  =(TextView)convertView.findViewById(R.id.title_textview);
            profile_imageView = (ImageView)convertView.findViewById(R.id.imageView);
            relativeLayout = (RelativeLayout)convertView.findViewById(R.id.relativeLayout);
        }
        nickname_textView.setText(this.list_itemArrayList.get(position).get_id());
        title_textView.setText(this.list_itemArrayList.get(position).getTitle());
        content_textView.setText(this.list_itemArrayList.get(position).getContent());
        date_textView.setText(this.list_itemArrayList.get(position).getWrite_date().toString());
        profile_imageView.setImageResource(this.list_itemArrayList.get(position).getProfile_image());
        return convertView;
    }
}
