package com.example.brown.afinal;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Brown on 7/27/2017.
 */

public class MessageService extends Service implements LocationListener {

    contactDatabase cdHelper;
    SQLiteDatabase db;

    LocationManager locationManager;
    Location currentLocation;
    Cursor cursor, mcursor;
    String message, number;
    double lat, lng;

    private void openDB() {
        cdHelper = new contactDatabase(this);
        db = cdHelper.helper.getWritableDatabase();
        cdHelper.open();
    }

    @Override
    public void onCreate() {
        openDB();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        cursor = cdHelper.getNumber();
        mcursor = cdHelper.getMessages();

        lat = currentLocation.getLatitude();
        lng = currentLocation.getLongitude();

        message = "Help! I am in danger and in need of assistance. Here's where you can find me: ";
        String smsBody = message + "http://www.maps.google.com/?q=" + lat + "," + lng;

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                try {
                    number = cursor.getString(cursor.getColumnIndex(cdHelper.helper.KEY_NUMBER));
                    if(number.equals("")){
                        Toast.makeText(this,"Contact List Empty",Toast.LENGTH_LONG).show();
                    }else {
                        SmsManager.getDefault().sendTextMessage(number,null,smsBody,null,null);
                    }
                }catch (Exception e){
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,10,MessageService.this);
            }
        }finally {
            cursor.close();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        cursor = cdHelper.getNumber();

        lat = currentLocation.getLatitude();
        lng = currentLocation.getLongitude();

        String smsBody = "http://www.maps.google.com/?q=" + lat + "," + lng;

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                SmsManager.getDefault().sendTextMessage(number,null,smsBody,null,null);
            }
        }finally {
            cursor.close();
        }

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
}
