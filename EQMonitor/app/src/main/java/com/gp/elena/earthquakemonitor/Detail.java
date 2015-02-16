package com.gp.elena.earthquakemonitor;

//import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Detail extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);
        Bundle recovery = getIntent().getExtras();

        TextView tv_titleD;
        TextView tv_information;

        tv_titleD = (TextView) findViewById(R.id.tv_titleD);
        tv_information = (TextView) findViewById(R.id.tv_information);

        tv_titleD.setText(recovery.getString("title"));

        String information;
        information="";
        information+= "Magnitude: " + recovery.getDouble("magnitude") + "\n\n";
        information+= "Time: " + DateFormat(recovery.getLong("time")) + "\n\n";
        information+= "Longitude: " + recovery.getDouble("longitude") + "\n\n";
        information+= "Latitude: " + recovery.getDouble("latitude") + "\n\n";
        information+= "Depth: " + recovery.getDouble("depth") + "\n\n";
        tv_information.setText(information);

        mapPositioning(recovery.getDouble("latitude"), recovery.getDouble("longitude"),
                recovery.getString("localization"), recovery.getDouble("magnitude"));
    }

    public String DateFormat (Long var){
        Date conversor = new Date(var);
        DateFormat conversorFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        return conversorFormat.format(conversor);
    }

    public void mapPositioning(Double latitude, Double longitude, String localization, Double magnitude) {
        android.support.v4.app.FragmentManager fmanager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fmanager.findFragmentById(R.id.f_Map);
        SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;
        GoogleMap supportMap = supportmapfragment.getMap();
        supportMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(localization).snippet("Magnitude: " + magnitude.toString()));
        supportMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 6.0f));
    }
}
