package com.example.derek.finalproject.fragment;


import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.derek.finalproject.MapsApiUtils;
import com.example.derek.finalproject.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGoogleMap extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public FragmentGoogleMap() {
        // Required empty public constructor
    }
    public static FragmentGoogleMap newInstance(){
        FragmentGoogleMap fragment=new FragmentGoogleMap();
        return fragment;
    }

    /***define Parameters here****************************/
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mLastLocation;
    final MapsApiUtils mapsApiUtils = new MapsApiUtils();
    Firebase ref = new Firebase("https://finalproject-600.firebaseio.com/");
    static String curAddress = null;
    String address = "LifeSciencesComplexSyracuse,NY";
    HashMap<String, String> hs = new HashMap<>();
    Polyline polyline = null;
    /*****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(savedInstanceState==null){
            buildGoogleApiClient();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_google_map, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
        if(mMapFragment!=null){
            mMapFragment.getMapAsync(this);
        }
    }
    private void buildGoogleApiClient() {
        if(mGoogleApiClient==null){
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest mLocationRequest = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("onConnectionFailed ", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    /*
    * set location request: frequency, priority
    * */
    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    LatLng latLng_Prev = null;
    LatLng latLng_Now = null;
    @Override
    public void onLocationChanged(Location location) {
        //move camera when location changed
        latLng_Now = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng_Now)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if(latLng_Prev == null){
            latLng_Prev = latLng_Now;
        }
        //draw line between two locations:
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(latLng_Prev, latLng_Now)
                .width(5)
                .color(Color.RED));
        latLng_Prev=latLng_Now;




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng lat) {
                Toast.makeText(getContext(), "Latitude: " + lat.latitude + "\nLongitude: " + lat.longitude, Toast.LENGTH_SHORT).show();
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng lat) {
                final Marker marker = mMap.addMarker(new MarkerOptions()
                        .title("self defined marker")
                        .snippet("Hello!")
                        .position(lat).visible(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))//.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                );
            }
        });

        //BitmapDescriptorFactory.HUE_BLUE
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker){
                Toast.makeText(getContext(), marker.getTitle().toString(), Toast.LENGTH_SHORT).show();
                String[] strs = {address,(String)hs.get(marker.getTitle().toString())};
                new DownLoadRouteAsyncTask(mMap).execute(strs);

                return true;

            }
        });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<?, ?> tmp = (HashMap) dataSnapshot.getValue();
                //curAddress = (String) tmp.get("address");
                //Log.d(dataSnapshot.getKey()+"key",curAddress+"@@@@@");

                //Log.d("name", curAddress);
                DataSnapshot postSnapshot = dataSnapshot.child("User");
                HashMap<?, ?> hs = (HashMap) postSnapshot.getValue();

                for (Object s : hs.keySet()) {
                    Log.d("user:", (String) s);
                    if (hs.get((String) s).getClass().equals(String.class)) {
                        continue;
                    }
                    HashMap<?, ?> tmp2 = (HashMap) hs.get((String) s);

                    if (!tmp2.get("location").equals("")) {
                        Log.d("address", (String) tmp2.get("location"));
                        String[] info = {(String) tmp2.get("location"), "UserName: " + (String) tmp2.get("userName")};
                        new UserLocationAsyncTask(mMap).execute(info);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        new MyDownloadLocationAsyncTask(mMap).execute(address);
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition());
    }


    private class MyDownloadLocationAsyncTask extends AsyncTask<String, Void, LatLng> {
        private final WeakReference<GoogleMap> mapWeakReference;
        public MyDownloadLocationAsyncTask(GoogleMap map){
            mapWeakReference = new WeakReference<GoogleMap>(map);
        }
        @Override
        protected LatLng doInBackground(String ... urls){
            LatLng coordinate = (LatLng)mapsApiUtils.getLatlng(urls[0]);
            if(coordinate!=null)
                Log.d("getPlace?",String.valueOf(coordinate.latitude));
            return coordinate;
        }

        @Override
        protected void onPostExecute(LatLng location){
            Marker startPerc=null;
            if(mapWeakReference != null) {
                final GoogleMap map = mapWeakReference.get();
                if (map != null) {
                    startPerc = map.addMarker(new MarkerOptions()
                            .position(location)
                            .title("Current Location")
                            .icon(BitmapDescriptorFactory.defaultMarker()));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
                }
            }
        }
    }

    private class UserLocationAsyncTask extends AsyncTask<String, Void, List<Object>> {
        private final WeakReference<GoogleMap> mapWeakReference;
        public UserLocationAsyncTask(GoogleMap map){
            mapWeakReference = new WeakReference<GoogleMap>(map);
        }
        @Override
        protected List<Object> doInBackground(String ... urls){
            LatLng coordinate = (LatLng)mapsApiUtils.getLatlng(urls[0]);
            List<Object> tmpList = new ArrayList<>();
            tmpList.add(coordinate);
            tmpList.add(urls[1]);
            hs.put(urls[1],urls[0]);
            if(coordinate!=null)
                Log.d("getPlace?",String.valueOf(coordinate.latitude));
            return tmpList;
        }

        @Override
        protected void onPostExecute(List<Object> tmpList){
            Marker startPerc=null;
            if(mapWeakReference != null) {
                final GoogleMap map = mapWeakReference.get();
                if (map != null) {
                    startPerc = map.addMarker(new MarkerOptions()
                            .position((LatLng)tmpList.get(0))
                            .title((String) tmpList.get(1))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }
        }
    }

    private class DownLoadRouteAsyncTask extends AsyncTask<String, Void, PolylineOptions> {
        private final WeakReference<GoogleMap> mapWeakReference;
        public DownLoadRouteAsyncTask(GoogleMap map){
            mapWeakReference = new WeakReference<GoogleMap>(map);
        }
        @Override
        protected PolylineOptions doInBackground(String ... strs){
            PolylineOptions poly = mapsApiUtils.getRoute(strs[0],strs[1]);
            return poly;
        }

        @Override
        protected void onPostExecute(PolylineOptions poly){
            Marker startPerc=null;
            if(mapWeakReference != null) {
                final GoogleMap map = mapWeakReference.get();
                if (map != null) {
                    poly.color(Color.BLUE).width(8);
                    if(polyline!=null) {
                        polyline.remove();
                    }
                    polyline = map.addPolyline(poly);
                }
            }
        }
    }
}
