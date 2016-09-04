package com.example.derek.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link addItem_frament#newInstance} factory method to
 * create an instance of this fragment.
 */

public class addItem_frament extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBE
    ImageView ItemImageView=null;
    EditText ItemNameEditText=null;
    EditText LowestPriceEditText=null;
    EditText HighestPriceEditText=null;
    EditText DesEditText=null;
    Button publishButton=null;
    Spinner tagpicker=null;
    Boolean canbe=true;
    private OnPublishButtionClick mListener;
    private static final String FIREBASEREF = "https://finalproject-600.firebaseio.com/";

    public addItem_frament() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment addItem_frament.
     */
    // TODO: Rename and change types and number of parameters
    public static addItem_frament newInstance() {
        addItem_frament fragment = new addItem_frament();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_add_item_frament, container, false);
        ItemImageView=(ImageView)v.findViewById(R.id.ItemImageView);
        ItemNameEditText=(EditText)v.findViewById(R.id.NameItemEditText);
        HighestPriceEditText=(EditText)v.findViewById(R.id.HighestPriceEditText);
        LowestPriceEditText=(EditText)v.findViewById(R.id.LowestPriceEditText);
        DesEditText=(EditText)v.findViewById(R.id.DesEditTextArea);
        publishButton=(Button)v.findViewById(R.id.publishButton);
        tagpicker=(Spinner)v.findViewById(R.id.TagSelector);
        final Firebase ref=new Firebase(FIREBASEREF);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        String name=ItemNameEditText.getText().toString();
                        String HighestPrice=HighestPriceEditText.getText().toString();
                        String LowestPrice=LowestPriceEditText.getText().toString();
                        String Des=DesEditText.getText().toString();
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) ItemImageView.getDrawable();
                        Bitmap image = bitmapDrawable.getBitmap();
                        String encodeStr = ZipUtility.encodeToBase64(image, Bitmap.CompressFormat.JPEG, 80);
                        String tag=tagpicker.getSelectedItem().toString();

                        boolean stopUpload=name.equals("")||HighestPrice.equals("")||LowestPrice.equals("")||HighestPrice.equals("")||
                                Des.equals("")||encodeStr.equals("");
                        if(stopUpload==true)
                        {
                            Toast.makeText(getActivity(), "Please complete all the info", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String uid=getActivity().getIntent().getStringExtra("Uid");
                        if(uid==null)
                        {
                            Toast.makeText(getActivity(), "Please Log in first",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ref.child("isAuth").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("snapchotPUH",dataSnapshot.getKey());
                                if(dataSnapshot.getValue(Boolean.class)==false)
                                {

                                    canbe=false;
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                        HashMap<String,String> map=new HashMap<String, String>();
                        map.put("name",name);
                        map.put("priceRange",LowestPrice+"-"+HighestPrice);
                        map.put("description",Des);
                        map.put("image",encodeStr);
                        map.put("tag",tag);
                        mListener.onPublishClick(map);
            }
        });
        ItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onGetItemImage();
            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPublishButtionClick) {
            mListener = (OnPublishButtionClick) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPublishButtionClick {

        void onPublishClick(HashMap<String,String> map);
        void onGetItemImage();
    }
}
