package be.dylandeceulaer.snelheidscontrole3;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class googleMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private SupportMapFragment fragment;

    public googleMapFragment() {
        // Required empty public constructor
    }

    public void PanMap(float x, float y,String title,String Snippet){
        LatLng pos = Test.lambert72toWGS84(x,y);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17), 1000, null);
        googleMap.addMarker(new MarkerOptions().position(pos).title(title).snippet(Snippet));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFrag = (SupportMapFragment) ((AppCompatActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);

        mapFrag.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_google_map, container, false);


        return v;
    }


    @Override
    public void onMapReady(final GoogleMap map) {
        this.googleMap = map;
    }
}
