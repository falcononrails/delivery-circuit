package com.deliverycircuit.ehtp.myapplication;

/*
 * Copyright (c) 2011-2019 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.ref.WeakReference;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.PositioningManager.*;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.mapping.MapMarker;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity {

    ArrayList<String> listarret;
    private ArrayList<localisationCor> cordinate;
    private ArrayList<localisationCor> ordredCordinate;
    OkHttpClient client;
    int indexRoute = 0;

    private static final String LOG_TAG = MapsActivity.class.getSimpleName();

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // map embedded in the map fragment
    private Map map = null;

    private boolean paused = false;

    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;

    // positioning manager instance
    private PositioningManager mPositioningManager;

    // Define positioning listener
    private OnPositionChangedListener positionListener = new
            OnPositionChangedListener() {

                public void onPositionUpdated(LocationMethod method,
                                              GeoPosition position, boolean isMapMatched) {
                    // set the center only when the app is in the foreground
                    // to reduce CPU consumption
                    if (!paused) {
                        /*map.setCenter(position.getCoordinate(),
                                Map.Animation.NONE);*/
                    }
                }

                public void onPositionFixChanged(LocationMethod method,
                                                 LocationStatus status) {
                }
            };

    private static MapRoute mapRoute = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        client = new OkHttpClient();
        cordinate = new ArrayList<localisationCor>();

        listarret = (ArrayList<String>) intent.getSerializableExtra("listArret");
        listarret.add(0, intent.getStringExtra("starting_place"));
        listarret.add(intent.getStringExtra("ending_place"));
        for (int i = 0; i < listarret.size(); i++) {
            getPosition(listarret.get(i));
        }
        checkPermissions();
    }

    private SupportMapFragment getSupportMapFragment() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initialize() {

        setContentView(R.layout.activity_maps);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = getSupportMapFragment();
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();

                    mPositioningManager = PositioningManager.getInstance();

                    mPositioningManager.addListener(new WeakReference<OnPositionChangedListener>(positionListener));

                    map.getPositionIndicator().setVisible(true);

                    map.setCenter(new GeoCoordinate(33.5687896, -7.6835203, 0.0),
                            Map.Animation.NONE);

                    if (mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK)) {
                        map.getPositionIndicator().setVisible(true);
                    } else {
                        Toast.makeText(MapsActivity.this, "PositioningManager.start: failed, exiting", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                }
            }
        });


    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
        if (mPositioningManager != null) {
            mPositioningManager.stop();
        }
    }

    @Override
    protected void onResume() {
        paused = false;
        super.onResume();
        if (mPositioningManager != null) {
            mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_routing, menu);
        return true;
    }

    // Functionality for taps of the "Get Directions" button
    public void getDirections(View view) {
        // 1. clear previous results
        if (map != null && mapRoute != null) {
            map.removeMapObject(mapRoute);
            mapRoute = null;
        }

        indexRoute = 0;

        ordredCordinate = new ArrayList<localisationCor>();


        for (int i = 0; i < cordinate.size(); i++) {
            for (int j = 0; j < cordinate.size(); j++) {
                if (listarret.get(i).equals(cordinate.get(j).getName())) {
                    ordredCordinate.add(cordinate.get(j));
                }
            }
        }
        for (int i = 0; i < ordredCordinate.size(); i++) {
            Image image = new Image();
            try {
                if (i == 0)
                    image.setImageResource(R.drawable.flag);
                else if (i == 1)
                    image.setImageResource(R.drawable.stop1);
                else if (i == 2)
                    image.setImageResource(R.drawable.stop2);
                else if (i == 3)
                    image.setImageResource(R.drawable.stop3);
                else if (i == 4)
                    image.setImageResource(R.drawable.stop4);
                else if (i == 5)
                    image.setImageResource(R.drawable.stop5);
                else if (i == 6)
                    image.setImageResource(R.drawable.stop6);
                if (i == ordredCordinate.size() - 1)
                    image.setImageResource(R.drawable.flag);
                MapMarker defaultMarker = new MapMarker();
                defaultMarker.setCoordinate(new GeoCoordinate(ordredCordinate.get(i).getPosX(), ordredCordinate.get(i).getPosY()));
                defaultMarker.setIcon(image);
                map.addMapObject(defaultMarker);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        map.getPositionIndicator().setVisible(true);
        RouteManager routeManager = new RouteManager();

        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);

        for (int i = 0; i < cordinate.size(); i++) {
            routePlan.addWaypoint(new GeoCoordinate(ordredCordinate.get(i).getPosX(), ordredCordinate.get(i).getPosY()));
        }


        // 5. Retrieve Routing information via RouteManagerEventListener
        RouteManager.Error error = routeManager.calculateRoute(routePlan, routeManagerListener);

        if (error != RouteManager.Error.NONE) {
            Toast.makeText(getApplicationContext(),
                    "Route calculation failed with: " + error.toString(), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private RouteManager.Listener routeManagerListener = new RouteManager.Listener() {
        public void onCalculateRouteFinished(RouteManager.Error errorCode,
                                             List<RouteResult> result) {

            if (errorCode == RouteManager.Error.NONE && result.get(0).getRoute() != null) {
                // create a map route object and place it on the map
                mapRoute = new MapRoute(result.get(0).getRoute());
                map.addMapObject(mapRoute);

                // Get the bounding box containing the route and zoom in (no animation)
                GeoBoundingBox gbb = result.get(0).getRoute().getBoundingBox();
                map.zoomTo(gbb, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);

            } else {

            }
        }

        public void onProgress(int percentage) {

        }
    };

    private void getPosition(final String stop) {
        String url = "https://places.cit.api.here.com/places/v1/autosuggest?" +
                "app_id=N8JglbCybLCFfme63WAE" +
                "&app_code=r4rXKbsVEFpODcigDYfY7g" +
                "&in=33.573109,-7.589843;r=40000" +
                "&q=" + stop +
                "&pretty";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String myResponse = response.body().string();
                        JSONObject Jobject = new JSONObject(myResponse);
                        JSONArray Jresults = Jobject.getJSONArray("results");

                        JSONObject object = Jresults.getJSONObject(0);

                        //position_parsing
                        String raw_position = object.getString("position");


                        String[] str_position = raw_position
                                .replace("[", "")
                                .replace("]", "")
                                .split(",");

                        cordinate.add(new localisationCor(stop, Double.parseDouble(str_position[0]), Double.parseDouble(str_position[1])));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });

    }

    public void nextStop(View v) {
        if (indexRoute < ordredCordinate.size() - 1) {
            // 1. clear previous results
            if (map != null && mapRoute != null) {
                map.removeMapObject(mapRoute);
                mapRoute = null;
            }

            for (int i = 0; i < ordredCordinate.size(); i++) {
                Image image = new Image();
                try {
                    if (i == 0)
                        image.setImageResource(R.drawable.flag);
                    else if (i == 1)
                        image.setImageResource(R.drawable.stop1);
                    else if (i == 2)
                        image.setImageResource(R.drawable.stop2);
                    else if (i == 3)
                        image.setImageResource(R.drawable.stop3);
                    else if (i == 4)
                        image.setImageResource(R.drawable.stop4);
                    else if (i == 5)
                        image.setImageResource(R.drawable.stop5);
                    else if (i == 6)
                        image.setImageResource(R.drawable.stop6);
                    if (i == ordredCordinate.size() - 1)
                        image.setImageResource(R.drawable.flag);
                    MapMarker defaultMarker = new MapMarker();
                    defaultMarker.setCoordinate(new GeoCoordinate(ordredCordinate.get(i).getPosX(), ordredCordinate.get(i).getPosY()));
                    defaultMarker.setIcon(image);
                    map.addMapObject(defaultMarker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            map.getPositionIndicator().setVisible(true);
            RouteManager routeManager = new RouteManager();

            RoutePlan routePlan = new RoutePlan();

            RouteOptions routeOptions = new RouteOptions();
            routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
            routeOptions.setRouteType(RouteOptions.Type.FASTEST);
            routePlan.setRouteOptions(routeOptions);


            routePlan.addWaypoint(new GeoCoordinate(ordredCordinate.get(indexRoute).getPosX(), ordredCordinate.get(indexRoute).getPosY()));
            routePlan.addWaypoint(new GeoCoordinate(ordredCordinate.get(indexRoute + 1).getPosX(), ordredCordinate.get(indexRoute + 1).getPosY()));

            // 5. Retrieve Routing information via RouteManagerEventListener
            RouteManager.Error error = routeManager.calculateRoute(routePlan, routeManagerListener);

            if (error != RouteManager.Error.NONE) {
                Toast.makeText(getApplicationContext(),
                        "Route calculation failed with: " + error.toString(), Toast.LENGTH_SHORT)
                        .show();
            }
            indexRoute++;
        } else {
            Toast.makeText(this.getApplicationContext(), "Ceci est votre arrêt!", Toast.LENGTH_LONG).show();
        }
    }

    public void previousStop(View view) {
        if (indexRoute > 1) {
            // 1. clear previous results
            if (map != null && mapRoute != null) {
                map.removeMapObject(mapRoute);
                mapRoute = null;
            }


            for (int i = 0; i < ordredCordinate.size(); i++) {
                Image image = new Image();
                try {
                    if (i == 0)
                        image.setImageResource(R.drawable.flag);
                    else if (i == 1)
                        image.setImageResource(R.drawable.stop1);
                    else if (i == 2)
                        image.setImageResource(R.drawable.stop2);
                    else if (i == 3)
                        image.setImageResource(R.drawable.stop3);
                    else if (i == 4)
                        image.setImageResource(R.drawable.stop4);
                    else if (i == 5)
                        image.setImageResource(R.drawable.stop5);
                    else if (i == 6)
                        image.setImageResource(R.drawable.stop6);
                    if (i == ordredCordinate.size() - 1)
                        image.setImageResource(R.drawable.flag);
                    MapMarker defaultMarker = new MapMarker();
                    defaultMarker.setCoordinate(new GeoCoordinate(ordredCordinate.get(i).getPosX(), ordredCordinate.get(i).getPosY()));
                    defaultMarker.setIcon(image);
                    map.addMapObject(defaultMarker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            map.getPositionIndicator().setVisible(true);
            RouteManager routeManager = new RouteManager();

            // 3. Select routing options
            RoutePlan routePlan = new RoutePlan();

            RouteOptions routeOptions = new RouteOptions();
            routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
            routeOptions.setRouteType(RouteOptions.Type.FASTEST);
            routePlan.setRouteOptions(routeOptions);

            routePlan.addWaypoint(new GeoCoordinate(ordredCordinate.get(indexRoute - 2).getPosX(), ordredCordinate.get(indexRoute - 2).getPosY()));
            routePlan.addWaypoint(new GeoCoordinate(ordredCordinate.get(indexRoute - 1).getPosX(), ordredCordinate.get(indexRoute - 1).getPosY()));

            RouteManager.Error error = routeManager.calculateRoute(routePlan, routeManagerListener);

            if (error != RouteManager.Error.NONE) {
                Toast.makeText(getApplicationContext(),
                        "Route calculation failed with: " + error.toString(), Toast.LENGTH_SHORT)
                        .show();
            }
            indexRoute--;
        } else {
            Toast.makeText(this.getApplicationContext(), "C'est votre premier arrêt!", Toast.LENGTH_LONG).show();
        }
    }

}
