package com.frangrgec.memplaceappdemo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    //Updates the location
    public void updateLocation(Location lastLocation, String title)
    {
        LatLng userLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());


        if(title!="Your location")
        {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
    }


    //Gets your location  on GPS request permission granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateLocation(lastLocation,"Your location");
                }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();

        //Gets your location if there aren't any saved else goes to the saved location
        if (intent.getIntExtra("Position", 0) == 0) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateLocation(location, "Your location");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            //Checks for GPS permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocation(lastLocation, "Your location");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("Position", 0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("Position", 0)).longitude);
            updateLocation(placeLocation, MainActivity.places.get(intent.getIntExtra("Position", 0)));
        }


    }

    //Adds a new saved location
    @Override
    public void onMapLongClick(LatLng latLng)
    {
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

        try
        {
            List<Address> addressesList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addressesList!=null && !addressesList.isEmpty())
            {
                mMap.addMarker(new MarkerOptions().position(latLng).title(addressesList.get(0).getAddressLine(0)));

                MainActivity.places.add(addressesList.get(0).getAddressLine(0));
                MainActivity.locations.add(latLng);
                MainActivity.adapter.notifyDataSetChanged();

                SharedPreferences sharedPreferences=this.getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);

                sharedPreferences.edit().putString("Places",ObjectSerializer.serialize(MainActivity.places)).apply();

                ArrayList<String> latitudes=new ArrayList<>();
                ArrayList<String> longitudes=new ArrayList<>();

                for(LatLng cordinates:MainActivity.locations)
                {
                    latitudes.add(Double.toString(cordinates.latitude));
                    longitudes.add(Double.toString(cordinates.longitude));
                    sharedPreferences.edit().putString("Latitudes",ObjectSerializer.serialize(latitudes)).apply();
                    sharedPreferences.edit().putString("Longitudes",ObjectSerializer.serialize(longitudes)).apply();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
