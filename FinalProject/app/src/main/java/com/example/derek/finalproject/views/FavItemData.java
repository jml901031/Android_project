package com.example.derek.finalproject.views;
import android.util.Log;

import com.example.derek.finalproject.Item;
import com.example.derek.finalproject.Activityfavorite.FriendsAdapter;
import com.example.derek.finalproject.fragment.Fragment_RecyclerView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Derek on 4/29/16.
 */
public class FavItemData {
    List<Item> itemList;
    Firebase ref;
    FriendsAdapter mRecyclerAdapter;
    String CurrentUser;

    public HashMap<String, HashMap<String, String>> getFavOwnerList() {
        return favOwnerList;
    }

    HashMap<String,HashMap<String,String>> favOwnerList;

    public FavItemData(String CurrentUser){
        this.CurrentUser=CurrentUser;
        itemList = new LinkedList<Item>();
        ref = new Firebase("https://finalproject-600.firebaseio.com/");
        favOwnerList=new HashMap<>();
        if(CurrentUser==null)
        {
            return;
        }
    }
    public List<Item> getItemList(){
        return itemList;
    }

    public Item getItem(int p){
        return itemList.get(p);
    }

    public Firebase getFirebaseRef(){
        return ref;
    }

    public int getSize(){return itemList.size();}
    public void setAdapter(FriendsAdapter m){
        this.mRecyclerAdapter = m;
    }

    public void initDataFromCloud(){
        if(CurrentUser==null)
        {
            return;
        }
        itemList.clear();
        final Firebase newref=ref.child("User").child(CurrentUser).child("FavoriteItem");
        if(newref==null)
        {
            return;
        }
        newref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Item addedItem = dataSnapshot.getValue(Item.class);
                Firebase refItem=ref.child("Item").child(addedItem.getTag()).child(addedItem.getKey());
                 refItem.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         final Item newItem = dataSnapshot.getValue(Item.class);

                         onItemAddedToCloud(newItem);
                         Log.v("newItem", itemList.size() + " ");
                         Firebase ownerref=ref.child("User").child(newItem.getOwner());
                         ownerref.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                 HashMap<String, String> mymap = (HashMap) dataSnapshot.getValue();
                                 if (!favOwnerList.containsKey(newItem.getOwner())) {
                                     favOwnerList.put(newItem.getOwner(), new HashMap<String, String>());
                                     favOwnerList.get(newItem.getOwner()).put("email", mymap.get("email"));
                                     favOwnerList.get(newItem.getOwner()).put("phoneNumber", mymap.get("phoneNumber"));
                                     favOwnerList.get(newItem.getOwner()).put("userName", mymap.get("userName"));
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

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("deletedHappened", dataSnapshot.getKey());
                Item deletedItem = dataSnapshot.getValue(Item.class);
                deletedItem.setKey(dataSnapshot.getKey());
                Log.d("deletedkey",deletedItem.getKey());
                onItemRemovedFromCloud(deletedItem);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void onItemRemovedFromCloud(Item deletedItem) {
        int position = -1;
        Log.d("deletedKeyHere",deletedItem.getKey());
        for(int i=0;i<itemList.size();i++){
            Log.d("keyInList",itemList.get(i).getKey());
            if(deletedItem.getKey().equals(itemList.get(i).getKey())){
                position = i;
                break;
            }
        }
        if(position!=-1){
            itemList.remove(position);
            Log.d("notifyremoved",deletedItem.getKey());
            if(mRecyclerAdapter!=null)
                mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void onItemAddedToCloud(Item addedItem) {
        itemList.add(addedItem);
        //Log.d("notifyadded",addedItem.getKey());
        if(mRecyclerAdapter != null)
            mRecyclerAdapter.notifyDataSetChanged();
    }

    public void onItemUpdated(Item item){
    }
}
