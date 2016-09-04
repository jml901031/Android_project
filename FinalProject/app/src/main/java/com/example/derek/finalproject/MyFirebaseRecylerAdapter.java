package com.example.derek.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.derek.finalproject.Item;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyFirebaseRecylerAdapter extends FirebaseRecyclerAdapter<Item,MyFirebaseRecylerAdapter.MovieViewHolder> {

    private Context mContext;
    static OnItemClickListener mItemClickListener;
    private List<Item> listItem;


    public MyFirebaseRecylerAdapter(Class<Item> modelClass, int modelLayout,
                                    Class<MovieViewHolder> holder, Query ref,Context context,List<Item> listItem) {
        super(modelClass,modelLayout,holder,ref);
        this.mContext = context;
        this.listItem = new ArrayList<>();
        this.listItem = listItem;
        Log.d("adapter",String.valueOf(listItem.size()));
    }

    @Override
    public int getItemViewType(int position){
        if(listItem.size()!=position && listItem.get(position).getState().equals("SOLD OUT"))
            return 1;
        return 0;
    }

   /* @Override
    public Item getItem(int position){
        return listItem.get(position);
    }*/
    @Override
    protected void populateViewHolder(MovieViewHolder movieViewHolder, Item movie, int i) {

        //TODO: Populate viewHolder by setting the movie attributes to cardview fields
        movieViewHolder.textTitle.setText(movie.getName());
        StringBuilder sb = new StringBuilder();

        if(movie.getDescription().length()>140) {
            sb.append(movie.getDescription().substring(0, 140));
            sb.append("...");
        }
        else
            sb.append(movie.getDescription());

        movieViewHolder.textDescription.setText(sb.toString());
        if(i!=listItem.size()&&movieViewHolder.favor!=null && listItem.get(i).isFavored()) {
            movieViewHolder.favor.setImageResource(R.drawable.ic_favorite_white_48dp);
            Log.d("favoredInmoviePic",movie.getName());
        }
     
        Bitmap bitmap = ZipUtility.decodeBase64(movie.getImage());
        movieViewHolder.moviePic.setImageBitmap(bitmap);
        movieViewHolder.moviePic.setTransitionName(movie.getName());
        Log.d("nameInadpter",movie.getName());
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{
        public ImageView moviePic;
        public TextView textTitle;
        public TextView textDescription;
        public TextView textRate;
        public RatingBar ratingBar;
        public ImageView favor;

        public MovieViewHolder(View v) {
            super(v);
            moviePic = (ImageView)v.findViewById(R.id.moviePic);
            textTitle = (TextView)v.findViewById(R.id.textTitle);
            textDescription = (TextView)v.findViewById(R.id.textDescription);
            textRate = (TextView)v.findViewById(R.id.textRate);
            ratingBar = (RatingBar)v.findViewById(R.id.ratingBar);
            favor = (ImageView)v.findViewById(R.id.favorPic);
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
    }
    @Override
    public MyFirebaseRecylerAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
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

        return new MovieViewHolder(v);
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
        void onFavorClick(View v,int position);
    }

    public void setOnItemClickListener(final OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

}
