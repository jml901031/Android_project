package com.example.derek.finalproject;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bubbl on 4/29/2016.
 */
public class ItemData{
    List<Item> itemList;
    Firebase ref;
    MyRecyclerViewAdapter mRecyclerAdapter;
    String tag;
    public ItemData(String tag){
        this.tag=tag;
        itemList = new LinkedList<Item>();
        ref = new Firebase("https://finalproject-600.firebaseio.com/Item");
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
    public void setAdapter(MyRecyclerViewAdapter m){
        this.mRecyclerAdapter = m;
    }

    public void initDataFromCloud(final HashMap<String,String> favors){
        itemList.clear();
        ref.child(tag).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Item addedItem = dataSnapshot.getValue(Item.class);
                addedItem.setKey(dataSnapshot.getKey());
                if(favors!=null && favors.containsKey(addedItem.getKey())){
                    addedItem.setFavored(true);
                    addedItem.setKeyInFavor(favors.get(addedItem.getKey()));
                }
                Log.d("OnChildAdded", dataSnapshot.getKey());
                onItemAddedToCloud(addedItem);
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
                mRecyclerAdapter.notifyItemRemoved(position);
        }
    }

    private void onItemAddedToCloud(Item addedItem) {
        int pos = 0;
        itemList.add(pos, addedItem);
        Log.d("notifyadded",addedItem.getKey());
        if(mRecyclerAdapter != null)
            mRecyclerAdapter.notifyItemInserted(pos);
    }

    public void onItemUpdated(Item item){
    }

}