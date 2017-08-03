package com.example.brown.afinal;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    contactDatabase cdHelper;
    SQLiteDatabase db;
    Geocoder geocoder;
    Location currentlocation;
    LocationManager locationManager;
    StringBuffer str;
    String number,localityString;
    TextView locationText;
    double lat, lon;
    Address returnAddress;
    List<Address> addresses;
    Cursor cursor, c;

    private GoogleMap googleMap;

    private final int REQUEST_CODE=99;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private void openDB(){
        cdHelper = new contactDatabase(this);
        db = cdHelper.helper.getWritableDatabase();
        cdHelper.open();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDB();
        placeMessage();
        str = new StringBuffer();
        locationText = (TextView) findViewById(R.id.Location);
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
        currentlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(currentlocation != null){
            try{
                lat = currentlocation.getLatitude();
                lon = currentlocation.getLongitude();

                geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(lat,lon,1);

                returnAddress = addresses.get(0);
                localityString = returnAddress.getLocality();

                str.append(localityString + " ");

                locationText.setText(str);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void placeMessage(){
        if(cdHelper.checkMessage().getCount() <= 0){
            String msg = "Help! I am in danger and in need of assistance. Here's where you can find me: ";
            cdHelper.setMessage(msg);
        }
    }

    public void sendMessage(View view){

    }

    public void mapWindow(View view){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogmap);
        dialog.show();

        MapView mMapView = (MapView)dialog.findViewById(R.id.mapView);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng loc = new LatLng(lat,lon);
                googleMap.addMarker(new MarkerOptions().position(loc).title("You Are Here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,17));
            }
        });
    }

    public void addNumber(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public void contactList(View view){
        Intent i = new Intent(this,ContactList.class);
        Bundle transitionBundle = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
        startActivity(i,transitionBundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);

                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num;

                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String names = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                cdHelper.insertNumber(names,num);
                                Toast.makeText(MainActivity.this,"Name="+names, Toast.LENGTH_LONG).show();
                                Toast.makeText(MainActivity.this, "Number="+num, Toast.LENGTH_LONG).show();
                            }
                            numbers.close();
                        }
                    }
                    break;
                }
        }
    }
}
