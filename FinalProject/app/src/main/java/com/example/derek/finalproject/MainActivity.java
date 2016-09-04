package com.example.derek.finalproject;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import com.example.derek.finalproject.fragment.FragmentGoogleMap;
import com.example.derek.finalproject.fragment.Fragment_CoordinatorLayout;
import com.example.derek.finalproject.fragment.Fragment_RecyclerView;
import com.example.derek.finalproject.fragment.LoopViewPagerFragment;
import com.example.derek.finalproject.loopViewPager.LoopViewPager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends CameraAndGallery  implements LoopViewPagerFragment.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        UserProfile.UpdateButtonClick,addItem_frament.OnPublishButtionClick
        ,Fragment_RecyclerView.OnItemClickListener{


    Fragment mContent;
    FragmentTransaction ft;
    Toolbar toolbar=null;
    DrawerLayout drawerLayout=null;
    BoomMenuButton boomMenuButton=null;
    CircularImageView LoginImageView=null;
    NavigationView navigationView=null;
    Firebase ref=null;
    private static final String FIREBASEREF = "https://finalproject-600.firebaseio.com/";
    String currentUser=null;
    TextView UserNameTextView=null;
    TextView EmailNameTextView=null;
    CameraAndGallery camera=null;
    List<Item> favItemList=null;
    HashMap<String,HashMap<String,String>> favOwnerList=null;
    HashMap<String,List<Item>> wholeList=null;
    HashMap<String,String> hs = new HashMap<>();
    HashMap<String,String> favorItemMap = new HashMap<>();
    public static String ARG_FAVORMAP = "favorItemMap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar=(Toolbar)findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
        ref=new Firebase(FIREBASEREF);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        LoginImageView=(CircularImageView)findViewById(R.id.UserLogoImageView);
        String user = getIntent().getStringExtra("CurrentUser");


        if(user!=null) {
            Log.d("CurrentUser", user);
            Firebase userRef = new Firebase("https://finalproject-600.firebaseio.com/User/" + user);
            userRef.child("FavoriteItem").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        HashMap<?, ?> hs = (HashMap) post.getValue();
                        Log.d("favorItem", (String) hs.get("key"));
                        favorItemMap.put((String) hs.get("key"), post.getKey());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }


        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.DrawerOpen,R.string.DrawerClose) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                LoginImageView=(CircularImageView)findViewById(R.id.UserLogoImageView);
                UserNameTextView=(TextView)findViewById(R.id.UserNameTextView);
                EmailNameTextView=(TextView)findViewById(R.id.EmailTextView);
                LoginImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(myIntent);
                    }
                });

                if(currentUser!=null) {
                    Firebase newref = ref.child("User").child(currentUser);
                    newref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String email=(String)dataSnapshot.child("email").getValue();
                            String UserName=(String)dataSnapshot.child("userName").getValue();
                            String bitmapcode = (String) dataSnapshot.child("avatar").getValue();
                            if (bitmapcode != null) {
                                Bitmap src = ZipUtility.decodeBase64(bitmapcode);
                                LoginImageView=(CircularImageView)findViewById(R.id.UserLogoImageView);
                                LoginImageView.setImageBitmap(src);
                            }
                            if(email!=null)
                            {
                                EmailNameTextView.setText(email);
                            }
                            if(UserName!=null)
                            {
                                UserNameTextView.setText(UserName);
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }


            }


        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        boomMenuButton=(BoomMenuButton)findViewById(R.id.boom);
        Firebase.setAndroidContext(this);
        mContent = LoopViewPagerFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mContent).commit();
        currentUser=this.getIntent().getStringExtra("CurrentUser");
        if(currentUser!=null) {
            favItemList=new ArrayList<>();
            favOwnerList=new HashMap<>();
            MyDownloadFavAsynTask ss = new MyDownloadFavAsynTask();
            Firebase newref = ref.child("User").child(currentUser).child("FavoriteItem");
            ss.execute(newref);
        }






    }
    @Override
    public void onWindowFocusChanged(boolean hasFoucs)
    {
        super.onWindowFocusChanged(hasFoucs);


        int[][] subButtonColors = new int[4][2];
        subButtonColors[0][1] = ContextCompat.getColor(this, R.color.pink);
        subButtonColors[0][0] = Util.getInstance().getPressedColor(subButtonColors[0][1]);
        subButtonColors[1][1] = ContextCompat.getColor(this, R.color.orange);
        subButtonColors[1][0] = Util.getInstance().getPressedColor(subButtonColors[1][1]);
        subButtonColors[2][1] = ContextCompat.getColor(this, R.color.purple);
        subButtonColors[2][0] = Util.getInstance().getPressedColor(subButtonColors[2][1]);


        Drawable homeNav=ContextCompat.getDrawable(this,R.drawable.homenav2);
        Drawable locaNav=ContextCompat.getDrawable(this,R.drawable.locationnav);
        Drawable recoNav=ContextCompat.getDrawable(this,R.drawable.cartnav);


        new BoomMenuButton.Builder()
                .addSubButton(homeNav, subButtonColors[0], "")
                .addSubButton(locaNav, subButtonColors[1], "")
                .addSubButton(recoNav, subButtonColors[2], "")
                .button(ButtonType.CIRCLE)
                .boom(BoomType.PARABOLA_2)
                .place(PlaceType.CIRCLE_3_3)
                .subButtonTextColor(ContextCompat.getColor(this, R.color.white))
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {
                        if (buttonIndex == 0) {
                            Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                            myIntent.putExtra("Uid",getIntent().getStringExtra("Uid"));
                            myIntent.putExtra("CurrentUser", currentUser);
                            MainActivity.this.startActivity(myIntent);
                           /* mContent = LoopViewPagerFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                    .replace(R.id.container, mContent).commit();*/

                            Log.d("ButtonIndex", "homeNav");
                        } else if (buttonIndex == 1) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.container, new FragmentGoogleMap().newInstance())
                                    .commit();
                            Log.d("ButtonIndex", "locaNav");
                        } else
                        {
                            mContent = addItem_frament.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mContent).addToBackStack(null).commit();
                        Log.d("ButtonIndex", "cartNav");
                         }

                    }

                })
                .init(boomMenuButton);
    }

    @Override
    public void OnItemClicked(int position){
        Toast.makeText(this, String.valueOf(position), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(String tag) {
        mContent = Fragment_RecyclerView.newInstance(tag,favorItemMap);
        Intent myIntent = new Intent(this, Activityfavorite.class);
        Log.d("beforeTranToRecy",String.valueOf(favorItemMap.size()));
        myIntent.putExtra(ARG_FAVORMAP,(Serializable)favorItemMap);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,mContent).addToBackStack(null).commit();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id= item.getItemId();
        switch (id)
        {
            case R.id.ProfileItem:
                ft=getSupportFragmentManager().beginTransaction();
                mContent=new UserProfile();
                ft.replace(R.id.container, mContent);
                ft.addToBackStack("mcontent");
                ft.commit();
                Log.d("abc","ace");
                break;
            case R.id.FavoriteItem:

                Intent myIntent = new Intent(MainActivity.this, Activityfavorite.class);
                myIntent.putExtra("CurrentUser", currentUser);

                myIntent.putExtra("favList", (Serializable) favItemList);
                myIntent.putExtra("favOwner",(Serializable)favOwnerList);
                MainActivity.this.startActivity(myIntent);
                break;
            case R.id.HistoryItem:
                 ref.unauth();
                Intent myIntent1 = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(myIntent1);
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void updateProfile(final HashMap<String,String> map) {
        if(currentUser==null)
        {
            Toast.makeText(MainActivity.this, "Please LogIn First", Toast.LENGTH_SHORT).show();
            return;
        }
        Runnable r=new Runnable() {
            @Override
            public void run() {
                Firebase UserRef = ref.child("User").child(currentUser);
                UpdateField(UserRef, "userName",map.get("userName"));
                UpdateField(UserRef, "email",map.get("email"));
                UpdateField(UserRef, "location",map.get("location"));
                UpdateField(UserRef, "phoneNumber",map.get("phoneNumber"));
                UpdateField(UserRef, "avatar", map.get("avatar"));

            }
        };
        r.run();
    }

    @Override
    public void uploadAvatar() {

        ImageView avatar=(ImageView)findViewById(R.id.AvatarImageView);
        setImageView(avatar);
        selectImage();
    }

    public void UpdateField(Firebase UserRef,String ref,String value)
    {

        UserRef.child(ref).setValue(value);
    }

    @Override
    public void onPublishClick(final HashMap<String,String> map) {
        Runnable r=new Runnable() {
            @Override
            public void run() {
                Firebase ItemRef=ref.child("Item");
                String time=getCurrentTime();
                String Itemkey=time+"-"+map.get("name")+"-"+currentUser;
                try {
                    ItemRef.child(map.get("tag")).child(Itemkey).setValue(new Item(map.get("name"), currentUser, map.get("priceRange"),
                            map.get("tag"), map.get("releaseDate"), "ON SALE", map.get("description"), map.get("image")),
                            new Firebase.CompletionListener(){
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if(firebaseError!=null)
                                    {
                                      Toast.makeText(getBaseContext(), firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getBaseContext(), "Sucessfully upload",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                catch (Exception e)
                {
                    Log.d("exception",e+"");
                }
            }
        };
        r.run();

    }

    @Override
    public void onGetItemImage() {
        ImageView itemImage=(ImageView)findViewById(R.id.ItemImageView);
        setImageView(itemImage);
        selectImage();
    }

    public String getCurrentTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void OnItemClicked(Item movie, View v,int position){
        mContent  = Fragment_CoordinatorLayout.newInstance(movie,position);


        mContent.setSharedElementEnterTransition(new DetailsTransition());
        mContent.setEnterTransition(new Fade());
        mContent.setExitTransition(new Fade());
        mContent.setSharedElementReturnTransition(new DetailsTransition());

        //Log.d("null", v.getTransitionName());
        getSupportFragmentManager().beginTransaction()
                .addSharedElement(v, v.getTransitionName())
                .replace(R.id.container, mContent).addToBackStack(null).commit();
        getSupportActionBar().hide();
    }


    public class MyDownloadFavAsynTask extends AsyncTask<Firebase,Void,Void>
    {

        public MyDownloadFavAsynTask()
        {

        }

        @Override
        protected Void doInBackground(Firebase...ref) {
            final Firebase newref = new Firebase(FIREBASEREF);
            for (final Firebase userRef : ref) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            HashMap<String, String> mapchild = (HashMap<String, String>) child.getValue();
                            String tag = mapchild.get("tag");
                            String key = mapchild.get("key");
                            Log.d("tag", tag+"????");
                            final Firebase itemref = newref.child("Item").child(tag).child(key);
                            itemref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final Item item = dataSnapshot.getValue(Item.class);
                                    favItemList.add(item);

                                    final Firebase itemref = newref.child("User").child(item.getOwner());
                                    itemref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            HashMap<String,String> mymap=(HashMap)dataSnapshot.getValue();
                                            if(!favOwnerList.containsKey(item.getOwner()))
                                            {
                                                favOwnerList.put(item.getOwner(), new HashMap<String, String>());
                                                favOwnerList.get(item.getOwner()).put("email", mymap.get("email"));
                                                favOwnerList.get(item.getOwner()).put("phoneNumber",mymap.get("phoneNumber"));
                                                favOwnerList.get(item.getOwner()).put("userName",mymap.get("userName"));
                                            }

                                        }
                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


            }

            return null;
        }
    }
    public class DetailsTransition extends TransitionSet {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform());
        }
    }

}