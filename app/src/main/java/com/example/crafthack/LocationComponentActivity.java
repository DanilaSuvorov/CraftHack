package com.example.crafthack;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;

/**
 * Use the LocationComponent to easily add a device location "puck" to a Mapbox map.
 */
public class LocationComponentActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private static final List<List<Point>> POINTS = new ArrayList<>();
    private static final List<Point> OUTER_POINTS = new ArrayList<>();
    private int start1 = 0;
    static {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, "pk.eyJ1Ijoic3RlcGFuNTAyNCIsImEiOiJjanFkc2R6cXY0OGptM3hvYjN4a3JnZmtiIn0.CnwlXeDFl8x0R0VVxVLQDw");
        setContentView(R.layout.activity_main);
// This contains the MapView in XML and needs to be called after the access token is configured.
        final Button start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    start.setVisibility(View.INVISIBLE);
                    start.setClickable(false);
                    mapView.setVisibility(View.VISIBLE);
                    start1 = 1;
            }
        });
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                LocationComponentActivity.this.mapboxMap = mapboxMap;
                Log.i("MapAsync", " is called");
                map = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                        style.addSource(new GeoJsonSource("source-id", Polygon.fromLngLats(POINTS)));
                        style.addLayerBelow(new FillLayer("layer-id", "source-id").withProperties(
                                fillColor(Color.parseColor("#3bb2d0"))), "settlement-label"
                        );

                    }
                });
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(55.0,37.0))
                        .title("My Location")
                        .snippet("Illinois")
                );
            }

        });
        new Thread(new Runnable() {
            public void run() {
                int hp = 100;
                while (true){
                    if (start1== 1){
                        hp -= 1;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ProgressBar pr = findViewById(R.id.progressBar2);
                        pr.setProgress(hp);
                        if(hp == 0){
                            LocationComponentActivity.this.runOnUiThread(new Runnable() { public void run() { Toast.makeText(getApplicationContext(), "Вы умерли от радиации", Toast.LENGTH_SHORT).show(); } });
                        }
                    }

                }
            }
    }).start();

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        LocationComponentActivity.this.mapboxMap = mapboxMap;
       // myLocation = locationEngine.getLastLocation();
        Log.i("MapAsync", " is called");
        map = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                style.addSource(new GeoJsonSource("source-id", Polygon.fromLngLats(POINTS)));
                style.addLayerBelow(new FillLayer("layer-id", "source-id").withProperties(
                        fillColor(Color.parseColor("#3bb2d0"))), "settlement-label"
                );

            }
        });
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(55.0,37.0))
                .title("My Location")
                .snippet("Illinois")
        );
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {


// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

// Retrieve UserLocation
            Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
            double shir = mapboxMap.getLocationComponent ().getLastKnownLocation ().getLatitude();
            double dolg = mapboxMap.getLocationComponent ().getLastKnownLocation ().getLongitude();
            OUTER_POINTS.add(Point.fromLngLat(dolg, shir));

            OUTER_POINTS.add(Point.fromLngLat(dolg+0.0002354631009876, shir +0.00046578679809654735));
            OUTER_POINTS.add(Point.fromLngLat(dolg, shir));
            OUTER_POINTS.add(Point.fromLngLat(dolg + 0.0002354631009876, shir + 0.000576890));
            OUTER_POINTS.add(Point.fromLngLat(dolg +0.0003354631009876, shir + 0.00046890));
            OUTER_POINTS.add(Point.fromLngLat(dolg +0.0004354631009876, shir + 0.00026890));

            OUTER_POINTS.add(Point.fromLngLat(dolg, shir));
            OUTER_POINTS.add(Point.fromLngLat(dolg + 0.0003354631009876, shir + 0.00046890));

            OUTER_POINTS.add(Point.fromLngLat(dolg + 0.0005354631009876, shir + 0.0001046890));

            OUTER_POINTS.add(Point.fromLngLat(dolg-0.0002354631009876, shir -0.00046578679809654735));
            OUTER_POINTS.add(Point.fromLngLat(dolg, shir));
            OUTER_POINTS.add(Point.fromLngLat(dolg  - 0.0002354631009876, shir - 0.000576890));
            OUTER_POINTS.add(Point.fromLngLat(dolg  - 0.0002454631009876, shir - 0.000676890));
            OUTER_POINTS.add(Point.fromLngLat(dolg -0.0003354631009876, shir - 0.00046890));
            OUTER_POINTS.add(Point.fromLngLat(dolg -0.0004354631009876, shir - 0.00026890));

            OUTER_POINTS.add(Point.fromLngLat(dolg, shir));

            POINTS.add(OUTER_POINTS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this,"необходим доступ к местоположению", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Нет доступа к местоположению", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}