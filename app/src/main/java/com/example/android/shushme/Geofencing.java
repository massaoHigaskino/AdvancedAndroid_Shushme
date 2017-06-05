package com.example.android.shushme;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Geofencing {

    private Context mContext;
    private GoogleApiClient mClient;
    private ArrayList<Geofence> mGeofenceList;

    // TODO (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
    // initializes a private member ArrayList of Geofences called mGeofenceList
    public Geofencing(Context context, GoogleApiClient client) {
        this.mContext = context;
        this.mClient = client;
        mGeofenceList = new ArrayList<>();
    }

    // TODO (2) Inside Geofencing, implement a public method called updateGeofencesList that
    // given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
    // and add that Geofence to mGeofenceList
    public void updateGeofencesList(PlaceBuffer placeBuffer) {
        mGeofenceList.clear();
        for (int i = 0; i < placeBuffer.getCount(); i++) {
            Place place = placeBuffer.get(i);
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(place.getId())
                    .setCircularRegion(place.getLatLng().latitude, place.getLatLng().longitude, 10)
                    .setExpirationDuration(TimeUnit.HOURS.toMillis(24))
                    .build());
        }
    }

    // TODO (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
    // uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list
    private GeofencingRequest getGeofencingRequest() {
        return new GeofencingRequest.Builder().addGeofences(mGeofenceList).build();
    }

    // TODO (4) Create a GeofenceBroadcastReceiver class that extends BroadcastReceiver and override
    // onReceive() to simply log a message when called. Don't forget to add a receiver tag in the Manifest
    public class GeofenceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(getClass().getName(), "onReceive: Geofence triggered");
        }
    }

    // TODO (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
    // returns a PendingIntent for the GeofenceBroadcastReceiver class
    private PendingIntent getGeofencePendingIntent() {
        return PendingIntent.getBroadcast(mContext, 0,
                new Intent(mContext, GeofenceBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    // TODO (6) Inside Geofencing, implement a public method called registerAllGeofences that
    // registers the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
    // using the helper functions getGeofencingRequest() and getGeofencePendingIntent()
    public void registerAllGeofences() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(mClient, getGeofencingRequest(), getGeofencePendingIntent());
    }

    // TODO (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
    // unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
    // using the helper function getGeofencePendingIntent()
    public void unRegisterAllGeofences() {
        LocationServices.GeofencingApi.removeGeofences(mClient, getGeofencePendingIntent());
    }
}
