package com.example.johnathan.vigilate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.example.johnathan.vigilate.Firebase.FirebaseRefencesRTDB;
import com.example.johnathan.vigilate.Firebase.New_Help;
import com.example.johnathan.vigilate.PreferenceReferences.ReferencesSettings;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker myPosition;
    private Marker alertPosition;
    double lat = 0.0;
    double lng = 0.0;
    private FirebaseDatabase database;
    private DatabaseReference newHelpUserId;
    private SharedPreferences setting;


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

        //se obtiene la ubicación del dispositivo actual
        //y se marca en el mapa
        findLocation();

        //la notificación me da el key del usuario que envió la alarma
        SharedPreferences setting = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, MODE_PRIVATE);
        String idUser = setting.getString(ReferencesSettings.ID_USER,"");

        if (!idUser.equals("")){
            //Descargo información desde firebase
            database = FirebaseDatabase.getInstance();
            newHelpUserId = database.getReference(FirebaseRefencesRTDB.NEW_HELP+"/"+idUser);
            newHelpUserId.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        double latUser = Double.parseDouble(dataSnapshot.child(New_Help.FIELD_LAT).getValue().toString());
                        double longUser = Double.parseDouble(dataSnapshot.child(New_Help.FIELD_LONG).getValue().toString());
                        addMarker(latUser, longUser);

                    }catch (Exception e){
                        Log.i("Take location", e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Al parecer ya se solucionó el problema",
                                Toast.LENGTH_LONG)
                                .show();
                        alertPosition.remove();
                        //sleep(5);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //Método para agregar markador en el mapa
    public void addMarker(double lat, double lng) {
        LatLng coordinates = new LatLng(lat, lng);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordinates,17);
        //Sí el marcador ya existe, se elimina
        if (alertPosition != null) {
            mMap.addMarker(new MarkerOptions()
                    .anchor(0.0f,1.0f)
                    .position(alertPosition.getPosition())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_point_mini)));
            alertPosition.remove();
        }
        //recordar cambiar el icono de este marcador
        alertPosition = mMap.addMarker(new MarkerOptions()
                .anchor(0.0f,1.0f)
                .position(coordinates)
                .title("Ayuda!!!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_logo)));

        //cambia la posición de la camara a la ubicación entrada por parametro
        mMap.animateCamera(myLocation);

        mMap.setMaxZoomPreference(100);
    }

    private void updateLocation(Location location) {
        //En caso que la location sea null, no se cerrará acutomáticamente la aplicación
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            addMarkerActualMe(lat, lng);
        }
    }

    private void addMarkerActualMe(double lat, double lng) {
        LatLng coordinates = new LatLng(lat, lng);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordinates,17);
        //Sí el marcador ya existe, se elimina
        if (myPosition != null) {
            myPosition.remove();
        }
        //recordar cambiar el icono de este marcador
        myPosition = mMap.addMarker(new MarkerOptions()
                .position(coordinates)
                .title("Este soy yo"));

        //cambia la posición de la camara a la ubicación entrada por parametro
        mMap.animateCamera(myLocation);

        mMap.setMaxZoomPreference(100);
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    //Método para encontrar la ubicación actual del usuario
    private void findLocation() {

        //Se agregan permisos para poder tomar la posición actual
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //tomamos la ubicación actual
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //la actualizamos por medio del método hecho anteriormete
        updateLocation(location);
        //indicamos que actualice la ubicación actual cada 15 segundos
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locListener);
    }

    private void sleep (int n){
        try {
            Thread.sleep(1000*n);
        }catch (Exception e){

        }
    }

}
