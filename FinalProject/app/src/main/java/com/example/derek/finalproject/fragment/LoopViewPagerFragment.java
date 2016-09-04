package com.example.derek.finalproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.derek.finalproject.R;
import com.example.derek.finalproject.SamplePagerAdapter;
import com.example.derek.finalproject.loopViewPager.LoopViewPager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class LoopViewPagerFragment extends Fragment {
    static String[] args = {"Book", "Electronic", "Album", "Home", "Furniture", "Pet", "Video Game", "Video", "Outdoor"};
    static int[] navbutton = {R.id.bookImageButton, R.id.ElectronicImageButton, R.id.AlbumImageButton, R.id.HomeImageButton, R.id.FurnitureImageButton,
            R.id.PetImageButton,R.id.VideoGameImageButton, R.id.TvImageButton, R.id.OutdoorImageButton};
    private OnItemClickListener mcontent;
    public static LoopViewPagerFragment newInstance() {
        LoopViewPagerFragment fragment = new LoopViewPagerFragment();

        return fragment;
    }
    public interface OnItemClickListener {
        public void OnItemClicked(int position);
        public void onClick(String tag);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loopviewpager, container, false);
        final List<CircularImageView> buttons = new ArrayList<>();
        mcontent=(OnItemClickListener)getContext();
        for (int i = 0; i < navbutton.length; i++)
        {
            buttons.add((CircularImageView)v.findViewById(navbutton[i]));
        }
        for(int j=0;j<navbutton.length;j++)
        {

           CircularImageView button=buttons.get(j);
           button.setOnClickListener(new NavOnClickListener(args[j]));
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final LoopViewPager viewpager = (LoopViewPager) view.findViewById(R.id.viewpager);
        viewpager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), String.valueOf(viewpager.getCurrentItem()), Toast.LENGTH_LONG).show();
            }
        });

        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);

        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter();
        viewpager.setAdapter(pagerAdapter);

        indicator.setViewPager(viewpager);

        pagerAdapter.setOnItemClick(new SamplePagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                final OnItemClickListener iListener;
                try {
                    iListener = (OnItemClickListener) getContext();
                } catch (ClassCastException e) {
                    throw new ClassCastException("forget to implement");
                }
                iListener.OnItemClicked(position);
                Log.d("fragment", String.valueOf(position));

            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        });

    }

    public class NavOnClickListener implements View.OnClickListener
    {
        String tag=null;
        NavOnClickListener(String tag)
        {
            this.tag=tag;
        }

        @Override
        public void onClick(View v) {
            Log.d("ts",tag);
            mcontent.onClick(tag);
        }
    }

}
