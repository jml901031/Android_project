package com.example.derek.finalproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Random;

public class SamplePagerAdapter extends PagerAdapter {
    Firebase ref;
    private final Random random = new Random();
    private int mSize;
    OnItemClickListener mItemClickListener;
    //static LruCache<String,> imgCache;

    public SamplePagerAdapter() {
        mSize = 4;
        ref = new Firebase("https://finalproject-600.firebaseio.com/");

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);

    }

    public void setOnItemClick(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    public SamplePagerAdapter(int count) {
        mSize = count;
    }

    @Override public int getCount() {
        return mSize;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override public Object instantiateItem(final ViewGroup view, final int position) {
        final String name="ad"+String.valueOf(position+1);
        final ImageView img = new ImageView(view.getContext());


        ref.child("ads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<?, ?> tmp = (HashMap) dataSnapshot.getValue();
                String ss = (String) tmp.get(name);
                Picasso.with(view.getContext()).load(ss).into(img);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });


        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v,position);
                    Log.d("setListener",String.valueOf(position));
                }
            }
        });

        view.addView(img, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return img;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public void addItem() {
        mSize++;
        notifyDataSetChanged();
    }

    public void removeItem() {
        mSize--;
        mSize = mSize < 0 ? 0 : mSize;

        notifyDataSetChanged();
    }

}