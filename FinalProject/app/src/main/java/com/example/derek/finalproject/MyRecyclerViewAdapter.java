package com.example.derek.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.derek.finalproject.views.FontTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Created by bubbl on 2/18/2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Item> listItem;
    //private Context mContext;
    OnItemClickListener mItemClickListener;

    // construct method
    public MyRecyclerViewAdapter( List<Item> myDataset){
        listItem = myDataset;
       // mContext = myContext;
    }

    // interface
    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
        void onFavorClick(View v,int position);

    }

    // methods
    public void setOnItemClick(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Item movie = listItem.get(position);
        holder.bindMovieData(movie,position);
    }

    @Override
    public int getItemViewType(int position){
        if(listItem.size()!=position && listItem.get(position).getState().equals("SOLD OUT"))
            return 1;
        return 0;
    }

    @Override
    public int getItemCount() {return listItem.size();}

    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_sold, parent, false);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView moviePic;
        public TextView textTitle;
        public TextView textDescription;
        public TextView textRate;
        public RatingBar ratingBar;
        public ImageView favor;
        public FontTextView user;
        public FontTextView price;
        public ViewHolder(View v){
            super(v);
            moviePic = (ImageView)v.findViewById(R.id.moviePic);
            textTitle = (TextView)v.findViewById(R.id.textTitle);
            textDescription = (TextView)v.findViewById(R.id.textDescription);
            textRate = (TextView)v.findViewById(R.id.textRate);
            favor = (ImageView)v.findViewById(R.id.favorPic);
            user = (FontTextView) v.findViewById(R.id.ratingBar);
            price = (FontTextView) v.findViewById(R.id.price);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

            if(favor != null){
                favor.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(mItemClickListener != null)
                            mItemClickListener.onFavorClick(v, getAdapterPosition());
                    }
                });

            }

        }
        public void bindMovieData(Item movie, int position){
            textTitle.setText(movie.getName());
            StringBuilder sb = new StringBuilder();

            if(movie.getDescription().length()>160) {
                sb.append(movie.getDescription().substring(0, 160));
                sb.append("...");
            }
            else
                sb.append(movie.getDescription());

            textDescription.setText(sb.toString());
       
            String[] tmp= movie.getOwner().split("@");
            user.setText(tmp[0]);
            price.setText(movie.getPriceRange());

            if(favor!=null && listItem.get(position).isFavored()) {
                favor.setImageResource(R.drawable.ic_favorite_white_48dp);
                Log.d("favoredInmoviePic", movie.getName());
            }
    
            Bitmap bitmap = ZipUtility.decodeBase64(movie.getImage());
            moviePic.setImageBitmap(bitmap);
            moviePic.setTransitionName(movie.getName());
            Log.d("nameInadpter", movie.getName());
        

        }

    }

}