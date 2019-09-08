package com.google.firebase.quickstart.database.java.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.java.models.Post;

import java.util.Random;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    Random randomGenerator = new Random();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                // [START create_database_reference]
                mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
                // [END create_database_reference]

                mDatabase.addChildEventListener(new ChildEventListener(){
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                        String title = (String) dataSnapshot.child("title").getValue();
                        LatLng position = generateRandomPosition();
                        mMap.addMarker(new MarkerOptions().position(position).title(title));
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(37.4629101, -122.2449094))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });
            }
        });
    }

    private LatLng generateRandomPosition() {
        double lat_eps = randomGenerator.nextDouble() / 10;
        double lng_eps = randomGenerator.nextDouble() / 10;
        return new LatLng(37.4629101 + lat_eps, -122.2449094 + lng_eps);
    }
}
