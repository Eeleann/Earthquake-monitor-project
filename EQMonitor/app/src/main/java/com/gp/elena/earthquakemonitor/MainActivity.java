package com.gp.elena.earthquakemonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gp.elena.earthquakemonitor.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;



public class MainActivity extends ActionBarActivity implements ActionInterface {

    public String urlGeoJSON = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    private static String TAG = MainActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;

    TextView tv_Ptitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_Ptitle = (TextView) findViewById(R.id.tv_Ptitle);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        makeJsonObjectRequestEQ();
    }

    private void makeJsonObjectRequestEQ() {
        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlGeoJSON, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    ArrayList<AdapterDriver> ListEQ = new ArrayList<>();
                    int colors;
                    JSONObject metadata = response.getJSONObject("metadata");
                    String titleSum = metadata.getString("title");
                    JSONArray features =response.getJSONArray("features");
                    tv_Ptitle.setText(titleSum);
                    for (int i = 0; i < features.length(); i++){
                        JSONObject quake = (JSONObject) features.get(i);
                        JSONObject properties = quake.getJSONObject("properties");
                        Double mag = properties.getDouble("mag");
                        String place = properties.getString("place");
                        Long time = properties.getLong("time");
                        String title = properties.getString("title");
                        JSONObject geometry = quake.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        Double longitude = coordinates.getDouble(0);
                        Double latitude = coordinates.getDouble(1);
                        Double depth = coordinates.getDouble(2);

                        if(mag<1){
                            colors=R.color.green;
                        }
                        else if(1<=mag && mag<3){
                            colors=R.color.lightGreen;
                        }
                        else if(3<=mag && mag<5){
                            colors=R.color.yellow;
                        }
                        else if(5<=mag && mag<7){
                            colors=R.color.orange;
                        }
                        else if(7<=mag && mag<9){
                            colors=R.color.red;
                        }
                        else{
                            colors=R.color.darkRed;
                        }

                        ListEQ.add(new AdapterDriver(title, mag, place, time, longitude, latitude, depth, colors));

                        SummaryMap(latitude, longitude, place, mag);
                    }

                    showList(ListEQ);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error to recovery data",
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "No internet services");
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
        MapPosition();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            makeJsonObjectRequestEQ();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showList(ArrayList<AdapterDriver> List) {

        //Adapter
        ListView SummaryList = (ListView) findViewById(R.id.lv_EQsummary);
        SummaryList.setAdapter(new RowAdapter(this, R.layout.row_adapter, List){
            @Override
            public void onInput (Object input, View view) {
                TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_title.setText(((AdapterDriver) input).getTitle());

                TextView tv_magnitude = (TextView) view.findViewById(R.id.tv_magnitude);
                tv_magnitude.setText(((AdapterDriver) input).getMagnitud().toString());

                TextView tv_localization = (TextView) view.findViewById(R.id.tv_localization);
                tv_localization.setText(((AdapterDriver) input).getLocalization());

                LinearLayout background = (LinearLayout) view.findViewById(R.id.ll_adapter);
                background.setBackgroundResource(((AdapterDriver) input).getColor());
            }
        });

        SummaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AdapterDriver chosen = (AdapterDriver) parent.getItemAtPosition(position);

                Intent i = new Intent(MainActivity.this,Detail.class);
                i.putExtra("title",chosen.getTitle());
                i.putExtra("magnitude",chosen.getMagnitud());
                i.putExtra("localization",chosen.getLocalization());
                i.putExtra("time",chosen.getTime());
                i.putExtra("longitude",chosen.getLongitude());
                i.putExtra("latitude",chosen.getLatitude());
                i.putExtra("depth",chosen.getDepth());

                startActivity(i);

            }
        });
    }

    public void SummaryMap(Double latitude, Double longitude, String localization, Double magnitude) {
        android.support.v4.app.FragmentManager fmanager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fmanager.findFragmentById(R.id.f_MapSum);
        SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;
        GoogleMap supportMap = supportmapfragment.getMap();
        supportMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(localization).snippet("Magnitude: " + magnitude.toString()));

    }

    public void MapPosition() {
        android.support.v4.app.FragmentManager fmanager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fmanager.findFragmentById(R.id.f_MapSum);
        SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;
        GoogleMap supportMap = supportmapfragment.getMap();
        supportMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.6557815,-98.4888282), 3.0f));
    }
}
