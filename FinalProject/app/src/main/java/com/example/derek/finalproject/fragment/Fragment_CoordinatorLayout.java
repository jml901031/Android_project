package com.example.derek.finalproject.fragment;


    import android.content.Context;
    import android.content.Intent;
    import android.content.res.Resources;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.Color;
    import android.graphics.drawable.BitmapDrawable;
    import android.graphics.drawable.Drawable;
    import android.graphics.drawable.LayerDrawable;
    import android.media.Image;
    import android.os.Bundle;
    import android.support.design.widget.CollapsingToolbarLayout;
    import android.support.v4.app.Fragment;
    import android.support.v4.media.session.IMediaControllerCallback;
    import android.support.v4.view.MenuItemCompat;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.Menu;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.ImageView;
    import android.widget.RatingBar;
    import android.support.v7.widget.ShareActionProvider;
    import android.widget.RelativeLayout;
    import android.widget.TextView;

    import com.example.derek.finalproject.Item;
    import com.example.derek.finalproject.R;
    import com.example.derek.finalproject.ZipUtility;
    import com.squareup.picasso.Picasso;

    import java.util.HashMap;
    import java.util.zip.Inflater;

/**
 * Created by bubbl on 2/10/2016.
 */
public class Fragment_CoordinatorLayout extends Fragment {

    private static final String ARG_ITEM="item";
    private static final String ARG_POSITION="position";
    private static Item item;
    private static int position;
    ShareActionProvider mShareActionProvider;
    OnItemClickListener mItemClickListener;


    @Override
    public void onDetach(){
        super.onDetach();
    }


    public Fragment_CoordinatorLayout(){

    }

    public interface OnItemClickListener{
        void onFavorClick(View v,int position);
    }


    public static Fragment_CoordinatorLayout newInstance(Item item,int position){
        Fragment_CoordinatorLayout fragment = new Fragment_CoordinatorLayout();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        args.putInt(ARG_POSITION,position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            item = (Item) getArguments().getSerializable(ARG_ITEM);
            position = getArguments().getInt(ARG_POSITION);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        final ImageView mImageView = (ImageView) rootView.findViewById(R.id.moviePic);
        toolbar.setTitle(item.getName());
        toolbar.setCollapsedTitleTextColor(Color.WHITE);
        String uri = "@drawable/frame";  
        int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
        mImageView.setImageBitmap(ZipUtility.decodeBase64(item.getImage()));
        mImageView.setTransitionName(item.getKey());
        Log.d("nameInAdapter", mImageView.getTransitionName())
        final TextView year = (TextView) rootView.findViewById(R.id.textYear);
        year.setText(item.getKey().substring(0,10));
        final TextView rate = (TextView) rootView.findViewById(R.id.textRate);
        rate.setText(item.getPriceRange());
        final TextView director = (TextView) rootView.findViewById(R.id.textDirector);
        String[] tmp= item.getOwner().split("@");
        director.setText(tmp[0]);
        final TextView star = (TextView) rootView.findViewById(R.id.textStar);
        star.setText(item.getState());
        final ImageView favorPic = (ImageView) rootView.findViewById(R.id.favorInFloating);
        final TextView description = (TextView) rootView.findViewById(R.id.textDescription);
        description.setText(item.getDescription());
        if(item.isFavored())
            favorPic.setImageResource(R.drawable.ic_favorite_white_48dp);

        favorPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
              
                String shareBody=null;
                if(item.isFavored()==true){
                    shareBody = "I saw an "+item.getName()+"in SUMarket"+ "which price is:"+ item.getPriceRange()+". I love it!";
                }
                else {
                    shareBody = "I saw an "+item.getName()+"in SUMarket"+ "which price is:"+ item.getPriceRange()+". I don't love it!";
                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }

    private void setTitleAlpha(View rootView){
        rootView.findViewById(R.id.titleDirector).setAlpha((float) 0.8);
        rootView.findViewById(R.id.titleStar).setAlpha((float)0.8);
        rootView.findViewById(R.id.titleYear).setAlpha((float)0.8);
        rootView.findViewById(R.id.titleRate).setAlpha((float)0.8);
        rootView.findViewById(R.id.titleDescription).setAlpha((float)0.8);
        rootView.findViewById(R.id.textDirector).setAlpha((float)0.5);
        rootView.findViewById(R.id.textStar).setAlpha((float)0.5);
        rootView.findViewById(R.id.textYear).setAlpha((float)0.5);
        rootView.findViewById(R.id.textRate).setAlpha((float) 0.5);
        rootView.findViewById(R.id.textDescription).setAlpha((float) 0.5);
    }
}
