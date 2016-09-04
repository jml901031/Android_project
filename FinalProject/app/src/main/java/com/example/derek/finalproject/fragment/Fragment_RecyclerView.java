package com.example.derek.finalproject.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SyncRequest;
import android.media.Image;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.derek.finalproject.Item;
import com.example.derek.finalproject.ItemData;
import com.example.derek.finalproject.MainActivity;
import com.example.derek.finalproject.MyFirebaseRecylerAdapter;
import com.example.derek.finalproject.MyRecyclerViewAdapter;
import com.example.derek.finalproject.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * Created by bubbl on 2/18/2016.
 */
public class Fragment_RecyclerView extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    MyFirebaseRecylerAdapter myFirebaseRecylerAdapter;
    MyRecyclerViewAdapter mRecyclerAdapter;
    Firebase ref;
    Firebase userRef;
    ItemData itemData;
    OnItemClickListener mListener;
    HashMap<String,String> userFavor;

    //public static ArrayList<Item> listItem;
    String tag=null;
    Boolean mutex=false;

    private static final String ARG_LIST="movielist";
    static String ARG_TAG="TAG";
    static String ARG_FAVOR = "favor";

    public Fragment_RecyclerView(){

    }
/*
    @Override
    public void onFavorClick(View v, int position) {
        if(itemData.getItemList().get(position).isFavored()==true){
            itemData.getItemList().get(position).setFavored(false);
            ImageView tmp = (ImageView) v.findViewById(R.id.favorPic);
            tmp.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            userFavor.remove(itemData.getItemList().get(position).getKey());
            userRef.child("FavoriteItem").child(itemData.getItemList().get(position).getKeyInFavor()).removeValue();
        }
        else{
            Log.d("callIndetail","??????");
            Item tmp = itemData.getItemList().get(position);
            if(userRef!=null) {
                tmp.setFavored(true);
                String tmpKey = getCurrentTime()+tmp.getKey();
                userRef.child("FavoriteItem").child(tmpKey).child("key").setValue(tmp.getKey());
                String[] arr = tmp.getTag().split(",");
                userRef.child("FavoriteItem").child(tmpKey).child("tag").setValue(arr[0]);
                tmp.setKeyInFavor(tmpKey);
                itemData.getItemList().add(position, tmp);
                userFavor.put(tmp.getKey(), tmp.getKeyInFavor());
                Log.d("tmpKey", tmpKey);
                ImageView tmpImg = (ImageView) v.findViewById(R.id.favorPic);
                tmpImg.setImageResource(R.drawable.ic_favorite_white_48dp);
            }
            else{
                Toast.makeText(getContext(),"Please Login To Mark Your favor Items",Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    public interface OnItemClickListener{
        public void OnItemClicked(Item movie,View view,int position);
    }

    private void itemAnimation(){
        LandingAnimator animator = new LandingAnimator();

        animator.setInterpolator(new OvershootInterpolator());
        animator.setAddDuration(300);
        animator.setRemoveDuration(500);

        mRecyclerView.setItemAnimator(animator);
    }

    public static Fragment_RecyclerView newInstance(String str,HashMap<String,String> favor){
        Fragment_RecyclerView fragment = new Fragment_RecyclerView();
        Bundle args = new Bundle();
        args.putString(ARG_TAG, str);
        args.putSerializable(ARG_FAVOR, favor);
        fragment.setArguments(args);

        return fragment;
    }



    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().show();
        //userFavor = new HashMap<>();

        final String user = getActivity().getIntent().getStringExtra("CurrentUser");
        Log.d("CurrentUser", user + ";aaa");

        if(user!=null) {
            Log.d("hasUser", "yes");
            userRef = new Firebase("https://finalproject-600.firebaseio.com/User/" + user);
        }

        if(userFavor!=null)
             Log.d("userFavorSize",String.valueOf(userFavor.size()));


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        itemData = new ItemData(tag);
        mRecyclerAdapter = new MyRecyclerViewAdapter(itemData.getItemList());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        if (itemData.getSize() == 0){
            itemData.setAdapter(mRecyclerAdapter);
            itemData.initDataFromCloud(userFavor);
        }

        mListener = (OnItemClickListener) getContext();

        mRecyclerAdapter.setOnItemClick(new MyRecyclerViewAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(final View view, int position) {
                Log.d("keyInOnCLick", itemData.getItem(position).getKey());
                /*itemData.getFirebaseRef().child(tag).child(itemData.getItem(position).getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Item item = dataSnapshot.getValue(Item.class);
                                item.setKey(dataSnapshot.getKey());
                                mListener.OnItemClicked(item,view);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });*/
                ImageView img = (ImageView)view.findViewById(R.id.moviePic);
                img.setTransitionName(itemData.getItem(position).getKey());
                mListener.OnItemClicked(itemData.getItem(position),img,position);
            }

            @Override
            public void onFavorClick(View v, int position) {
                if(itemData.getItemList().get(position).isFavored()==true){
                    itemData.getItemList().get(position).setFavored(false);
                    ImageView tmp = (ImageView) v.findViewById(R.id.favorPic);
                    tmp.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                    userFavor.remove(itemData.getItemList().get(position).getKey());
                    userRef.child("FavoriteItem").child(itemData.getItemList().get(position).getKeyInFavor()).removeValue();
                }
                else{
                    Item tmp = itemData.getItemList().get(position);
                    if(userRef!=null) {
                        tmp.setFavored(true);
                        String tmpKey = getCurrentTime()+tmp.getKey();
                        userRef.child("FavoriteItem").child(tmpKey).child("key").setValue(tmp.getKey());
                        String[] arr = tmp.getTag().split(",");
                        userRef.child("FavoriteItem").child(tmpKey).child("tag").setValue(arr[0]);
                        tmp.setKeyInFavor(tmpKey);
                        itemData.getItemList().add(position, tmp);
                        userFavor.put(tmp.getKey(), tmp.getKeyInFavor());
                        Log.d("tmpKey", tmpKey);
                        ImageView tmpImg = (ImageView) v.findViewById(R.id.favorPic);
                        tmpImg.setImageResource(R.drawable.ic_favorite_white_48dp);
                    }
                    else{
                        Toast.makeText(getContext(),"Please Login To Mark Your favor Items",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        itemAnimation();
        adapterAnimation();

        return rootView;
    }

    private void adapterAnimation(){
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mRecyclerAdapter);
        SlideInBottomAnimationAdapter slideAdapter = new SlideInBottomAnimationAdapter(alphaAdapter);
        mRecyclerView.setAdapter(slideAdapter);
    }

    public class ActionBarCallBack implements ActionMode.Callback{
        int position;
        public ActionBarCallBack(int position){this.position = position;}

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item){
            int id = item.getItemId();
/*            switch (id){
                case R.id.item_delete:
                    Movie cloud = myFirebaseRecylerAdapter.getItem(position);
                    ref.child(cloud.getId()).removeValue();
                    return true;

                case R.id.item_duplicate:
                    Movie cloudDuplicate = myFirebaseRecylerAdapter.getItem(position);
                    cloudDuplicate.setId(cloudDuplicate.getId() + "_new");
                    cloudDuplicate.setName(cloudDuplicate.getName() + "_new");
                    ref.child(cloudDuplicate.getId()).setValue(cloudDuplicate);
                    return true;
                default:
                    break;
            }*/
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu){
            //mode.getMenuInflater().inflate(R.menu.contextual_or_popup_menu, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode){
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu){
            return false;
        }


    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
        {
            tag=(String)getArguments().getString(ARG_TAG);
            userFavor = (HashMap) getArguments().getSerializable(ARG_FAVOR);
        }
    }

    public String getCurrentTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

/*


final String user = getActivity().getIntent().getStringExtra("CurrentUser");
Log.d("CurrentUser", user + ";aaa");
final Hashtable<String,String> favorItem = new Hashtable<String,String>();
        if(user!=null) {
        Log.d("hasUser","yes");
        userRef = new Firebase("https://finalproject-600.firebaseio.com/User/" + user);
        userRef.child("FavoriteItem").addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot post : dataSnapshot.getChildren()) {
        HashMap<?, ?> hs = (HashMap) post.getValue();
        Log.d("favorItem", (String) hs.get("key"));
        favorItem.put((String) hs.get("key"), post.getKey());

        }
        }

@Override
public void onCancelled(FirebaseError firebaseError) {

        }
        });
        }




        Firebase ElectronicRef = ref.child("Electronic");
        listItem = new ArrayList<>();
        ElectronicRef.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {
        int i = 0;
        Log.d("called", String.valueOf("i"));

        for (DataSnapshot post : dataSnapshot.getChildren()) {
        Item tmp = post.getValue(Item.class);

        if (favorItem.containsKey(post.getKey())) {
        tmp.setFavored(true);
        Log.d("favorrrrrrrrrrrr", post.getKey());
        tmp.setKeyInFavor(favorItem.get(post.getKey()));
        }
        tmp.setPosition(i);
        tmp.setKey(post.getKey());
        listItem.add(tmp);

        Log.d("aaa", String.valueOf(listItem.get(i).isFavored()));
        i++;
        }
        Log.d("aaaaa", String.valueOf(dataSnapshot.getChildrenCount()));
        }

@Override
public void onCancelled(FirebaseError firebaseError) {

        }
        });

        try {
            Thread.sleep(500);
        }
        catch (Exception ex){};
                myFirebaseRecylerAdapter.setOnItemClickListener(new MyFirebaseRecylerAdapter.OnItemClickListener() {
@Override
public void onItemClick(View v, int position) {
final OnItemClickListener iListener;
        try {
        iListener = (OnItemClickListener) getContext();

        } catch (ClassCastException e) {
        throw new ClassCastException("forget to implement");
        }
        // download movie data deta
        Item cloud = myFirebaseRecylerAdapter.getItem(position);
        ImageView img = (ImageView)v.findViewById(R.id.moviePic);
        img.setTransitionName(cloud.getName());
        Log.d("imgTramNameInRecy", img.getTransitionName());
        //Log.d("nameInRecyclerView",cloud.getName());
        iListener.OnItemClicked(cloud,img);
        }
@Override
public void onFavorClick(View v,int position){
        if(itemData.getItemList().get(position).isFavored()==true){
        itemData.getItemList().get(position).setFavored(false);
        ImageView tmp = (ImageView) v.findViewById(R.id.favorPic);
        tmp.setImageResource(R.drawable.ic_favorite_border_white_48dp);
        userRef.child("FavoriteItem").child(itemData.getItemList().get(position).getKeyInFavor()).removeValue();
        }
        else{
        Item tmp = itemData.getItemList().get(position);
        tmp.setFavored(true);
        String tmpKey = getCurrentTime()+tmp.getKey();
        userRef.child("FavoriteItem").child(tmpKey).child("key").setValue(tmp.getKey());
        String[] arr = tmp.getTag().split(",");
        userRef.child("FavoriteItem").child(tmpKey).child("tag").setValue(arr[0]);
        tmp.setKeyInFavor(tmpKey);
        itemData.getItemList().add(position, tmp);
        ImageView tmpImg = (ImageView) v.findViewById(R.id.favorPic);
        tmpImg.setImageResource(R.drawable.ic_favorite_white_48dp);
        }
        }

public String getCurrentTime()
        {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
        }

            /*@Override
            public void onOverflowMenuClick(View v, final int position) {
                PopupMenu popup = new PopupMenu(getActivity(), v);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.item_delete:
                                Movie cloud = myFirebaseRecylerAdapter.getItem(position);
                                ref.child(cloud.getId()).removeValue();
                                return true;

                            case R.id.item_duplicate:
                                Movie cloudDuplicate = myFirebaseRecylerAdapter.getItem(position);
                                cloudDuplicate.setId(cloudDuplicate.getId() + "_new");
                                cloudDuplicate.setName(cloudDuplicate.getName() + "_new");
                                ref.child(cloudDuplicate.getId()).setValue(cloudDuplicate);
                                return true;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.contextual_or_popup_menu, popup.getMenu());
                popup.show();
            }
        });
*/