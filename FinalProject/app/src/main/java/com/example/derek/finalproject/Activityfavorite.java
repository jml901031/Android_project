package com.example.derek.finalproject;
import android.app.LauncherActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.derek.finalproject.views.FavItemData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.utilities.Utilities;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Activityfavorite extends AppCompatActivity {

    private static final String FIREBASEREF = "https://finalproject-600.firebaseio.com/";
     static  final Firebase ref=new Firebase(FIREBASEREF);;
    List<Item> favItemList=null;
    private static int[] colors={R.color.pink,R.color.saffron,R.color.sienna,R.color.green,R.color.purple,R.color.orange};
    ListView friends=null;
    HashMap<String,HashMap<String,String>> ownermap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String tempuser=getIntent().getStringExtra("CurrentUser");
        ref.child("User");
        favItemList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityfavorite);
        List<Item> arr=(List<Item>)getIntent().getSerializableExtra("favList");
        ownermap=new HashMap<>();
        HashMap map=(HashMap < String, HashMap < String, String >>) getIntent().getSerializableExtra("favOwner");
        if(map!=null)
        ownermap.putAll(map);
        if(arr!=null)
        favItemList.addAll(arr);
        friends = (ListView) findViewById(R.id.friends);
        FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
        FriendsAdapter adapter=new FriendsAdapter(this, favItemList, settings);
        friends.setAdapter(adapter);

        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item f = (Item) friends.getAdapter().getItem(position);

                Toast.makeText(Activityfavorite.this, f.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class FriendsAdapter extends BaseFlipAdapter<Item> {

        private final int PAGES = 3;
        private int[] IDS_INTEREST = {R.id.interest_1, R.id.interest_2, R.id.interest_3, R.id.interest_4, R.id.interest_5};

        List<String> list=new ArrayList<String>();
        public FriendsAdapter(Context context, List<Item> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int position, View convertView, ViewGroup parent, Item item1, Item item2) {
            final FriendsHolder holder;

            if (convertView == null) {
                holder = new FriendsHolder();
                convertView = getLayoutInflater().inflate(R.layout.favoriteitemmerge, parent, false);
                holder.leftAvatar = (ImageView) convertView.findViewById(R.id.first);
                holder.rightAvatar = (ImageView) convertView.findViewById(R.id.second);
                holder.infoPage = getLayoutInflater().inflate(R.layout.item_info, parent, false);
                holder.nickName = (TextView) holder.infoPage.findViewById(R.id.nickname);
             

                for (int id : IDS_INTEREST)
                    holder.interests.add((TextView) holder.infoPage.findViewById(id));

                convertView.setTag(holder);
            } else {
                holder = (FriendsHolder) convertView.getTag();
            }
            switch (position) {
              
                case 1:
                    holder.leftAvatar.setImageBitmap(ZipUtility.decodeBase64(item1.getImage()));
                    if (item2 != null)
                        holder.rightAvatar.setImageBitmap(ZipUtility.decodeBase64(item2.getImage()));
                    break;
                default:

                    fillHolder(holder, position == 0 ? item1 : item2);
                    holder.infoPage.setTag(holder);
                    return holder.infoPage;
            }
            return convertView;
        }
        @Override
        public int getPagesCount() {
            return PAGES;
        }
        private void fillHolder(FriendsHolder holder, Item item)  {
            if (item == null)
                return;
            Iterator<TextView> iViews = holder.interests.iterator();
            list=new ArrayList<>();

           HashMap<String,String> ss=ownermap.get(item.getOwner());
            list.add(ss.get("userName"));
            list.add(ss.get("phoneNumber"));
            list.add(ss.get("email"));
            list.add(item.getTag());
            list.add(item.getPriceRange());
            Iterator<String> iInterests = list.iterator();

            while (iViews.hasNext() && iInterests.hasNext()) {


                iViews.next().setText(iInterests.next());
            }
            Random r=new Random();
            holder.infoPage.setBackgroundColor(getResources().getColor(colors[r.nextInt(colors.length)]));
            holder.nickName.setText(item.getName());
        }
        class FriendsHolder {
            ImageView leftAvatar;
            ImageView rightAvatar;
            View infoPage;

            List<TextView> interests = new ArrayList<>();
            TextView nickName;
        }
    }


}
