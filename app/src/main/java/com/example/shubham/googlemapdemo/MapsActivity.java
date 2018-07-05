package com.example.shubham.googlemapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    public static final int RequestPermissionCode = 999;
    public static final String TAG = "TEST";
    public static final String PRE_MARSHMALLOW = "PreMarshMallow";
    public static final String MARSHMALLOW = "Marshmallow";
    double latitude, longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;

    //Runtime Permission Steps
    /*
     * 1. Check the platform
     * 2. Check the Permission
     * 3. Explain the permission
     * 4. Request the permission
     * 5. Handle the response
     *
     *
     *
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Instantiating the GoogleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        String platform = checkPlatform();
        if (platform.equals("Marshmallow")) {
            Log.d(TAG, "runtime permission required");
            boolean permissionStatus = checkPermission();
            if (permissionStatus) {
                Log.d(TAG, "Permission already granted");
            } else {
                Log.d(TAG, "explain permission");
                explainPermission();
                Log.d(TAG, " request permission");
                requestPermission();


            }

        } else {
            Log.d(TAG, "runtime permission not required");
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    moveMap();

                } else {
                    Log.d(TAG, "onSuccess: location is null");
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClient.connect();

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    //Checking the Camera and Read_Contacts permissions
    public boolean checkPermission() {
        int fineLocationPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return fineLocationPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    //1.Check the platfrom
    public String checkPlatform() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return MARSHMALLOW;

        } else {
            return PRE_MARSHMALLOW;
        }
    }

    //3. Explain Permission required
    public void explainPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.d(TAG, "explainPermission:Access fine location Permission required ");
            Toast.makeText(this, "Access fine location Permission required", Toast.LENGTH_SHORT).show();
        }

    }

    //4. Requesting the Camera and Read_Contacts permissions
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,

        }, RequestPermissionCode);
    }

    //5. Handle the response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission was granted
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
        }
        return;
    }

    public void moveMap() {
        mMap.clear();

        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Current Location").draggable(true));

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}

