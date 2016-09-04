package com.example.derek.finalproject;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dd.processbutton.iml.GenerateProcessButton;


import com.example.derek.finalproject.loopViewPager.ProgressGenerator;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
] interface
 * to handle interaction events.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment implements ProgressGenerator.OnCompleteListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    Spinner StatePicker=null;
    GenerateProcessButton updateButton=null;
    UpdateButtonClick mListener;
    EditText nameText;
    EditText emailText;
    EditText PhoneText;
    EditText AddressLine1Text;
    EditText AddressLine2Text;
    Firebase ref=null;
    ImageView avatarView=null;

    private static final String FIREBASEREF = "https://finalproject-600.firebaseio.com/";

    public UserProfile() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserProfile newInstance() {
        UserProfile fragment = new UserProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view=inflater.inflate(R.layout.fragment_user_profile, container, false);
        StatePicker=(Spinner)view.findViewById(R.id.StatePicker);
        updateButton=(GenerateProcessButton)view.findViewById(R.id.UpdateButton);
        nameText=(EditText)view.findViewById(R.id.NameEditText);
        emailText=(EditText)view.findViewById(R.id.EmailEditText);
        PhoneText=(EditText)view.findViewById(R.id.PhoneNumEditText);
        AddressLine1Text=(EditText)view.findViewById(R.id.AddressLineEditText1);
        AddressLine2Text=(EditText)view.findViewById(R.id.AddressLineEditText2);
        avatarView=(ImageView)view.findViewById(R.id.AvatarImageView);
        ref=new Firebase(FIREBASEREF);
        Firebase.setAndroidContext(this.getContext());
        String CurrentUser=getActivity().getIntent().getStringExtra("CurrentUser");
        if(CurrentUser!=null)
        {
            Firebase userref=ref.child("User").child(CurrentUser);
            userref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String location = (String) dataSnapshot.child("location").getValue();
                    String email = (String) dataSnapshot.child("email").getValue();
                    String userName = (String) dataSnapshot.child("userName").getValue();
                    String phoneNumber = (String) dataSnapshot.child("phoneNumber").getValue();

                    String bitmapcode = (String) dataSnapshot.child("avatar").getValue();
                    Log.d("location", location);
                    if (location != null) {
                        String[] locations = location.split("\\+");
                        if (locations == null || locations == null) {
                            return;
                        }
                        EditText []temp={AddressLine1Text,AddressLine2Text};
                        for (int i = 0; i < locations.length && i < 2; i++) {
                           temp[i].setText(locations[i].replace("-", " "));

                        }
                        if (locations.length == 3) {

                            String[] states = getContext().getResources().getStringArray(R.array.country_arrays);
                            int count = 0;
                            for (String state : states) {
                                locations[2] = locations[2].replace("-", " ");
                                if (state.equals(locations[2])) {
                                    StatePicker.setSelection(count);
                                }
                                count++;
                            }
                        }
                    }
                    emailText.setText(email);
                    nameText.setText(userName);
                    PhoneText.setText(phoneNumber);
                    if (bitmapcode != null) {
                        Bitmap src = ZipUtility.decodeBase64(bitmapcode);
                        avatarView.setImageBitmap(src);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        StatePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });



        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable r=new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder locationbuilder = new StringBuilder();
                        locationbuilder.append(AddressLine1Text.getText().toString()).append("+");
                        locationbuilder.append(AddressLine2Text.getText().toString()).append("+");
                        locationbuilder.append(StatePicker.getSelectedItem().toString());
                        String location = locationbuilder.toString().replace(" ", "-");
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) avatarView.getDrawable();
                        Bitmap avatar = bitmapDrawable.getBitmap();
                        String encodeStr = ZipUtility.encodeToBase64(avatar, Bitmap.CompressFormat.JPEG, 50);
                        final HashMap<String, String> map = new HashMap<>();
                        map.put("location", location);
                        map.put("userName", nameText.getText().toString());
                        map.put("email", emailText.getText().toString());
                        map.put("phoneNumber", PhoneText.getText().toString());
                        map.put("avatar", encodeStr);
                        for(String key:map.keySet())
                        {
                            if(map.get(key).isEmpty())
                            {
                                Toast.makeText(getActivity(), "Please completed all the info",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        String uid=getActivity().getIntent().getStringExtra("Uid");
                        if(uid==null)
                        {
                            Toast.makeText(getActivity(), "Please Log in first",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ref.child("isAuth").child(uid).setValue(true);
                        Toast.makeText(getActivity(),"Successful update",Toast.LENGTH_SHORT).show();
                        onButtonPressed(map);
                        ProgressGenerator progressGenerator = getnewprogressGenerator();
                        progressGenerator.start(updateButton);
                        updateButton.setEnabled(false);
                    }
                };
                r.run();

            }
        });


        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.uploadAvatar();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(HashMap<String,String> map) {
       if (mListener != null) {
            mListener.updateProfile(map);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateButtonClick) {
            mListener = (UpdateButtonClick) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public ProgressGenerator getnewprogressGenerator()
    {
        return new ProgressGenerator(this);
    }
    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onComplete() {
        updateButton.setEnabled(true);
    }

    public interface UpdateButtonClick
    {
        public void updateProfile(HashMap<String,String> map);
        public void uploadAvatar();
    }
}
