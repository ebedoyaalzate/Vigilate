package com.example.johnathan.vigilate.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.johnathan.vigilate.Firebase.FirebaseRTDB;
import com.example.johnathan.vigilate.PreferenceReferences.ReferencesSettings;

public class ServiceLocationGPS extends Service{
    private FirebaseRTDB firebaseRTDB = new FirebaseRTDB();
    private double lat;
    private double longit;
    Context context;
    SharedPreferences setting;
    SharedPreferences.Editor editorSettings;

    /*
    public ServiceLocationGPS(Context context) {
        super();
        this.context = context;
        findLocation();
    }
    */


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        findLocation();
        //tomo el key del usuario que manda la alerta
        setting = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, MODE_PRIVATE);
        editorSettings = setting.edit();
        String idUserLocal = setting.getString(ReferencesSettings.ID_USER_LOCAL,"");
        String latString = ""+lat;
        String longString = ""+longit;

        if(!idUserLocal.equals("") && lat!= 0 && longit != 0){
            firebaseRTDB.addNewHelp(idUserLocal,lat,longit);
        }
        editorSettings.putString(ReferencesSettings.LAT_LOCAL,latString);
        editorSettings.putString(ReferencesSettings.LONG_LOCAL, longString);
        editorSettings.commit();
        return START_STICKY;
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            updateLocation(location);

            //hacer logica para actualizar ubiación gps
            setting = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, MODE_PRIVATE);
            boolean updateActived = setting.getBoolean(ReferencesSettings.UPDATE_LOCATION_ACTIVED,false);
            if (updateActived){
                Intent thisServiceAgain = new Intent(getApplicationContext(),ServiceLocationGPS.class);
                getApplicationContext().startService(thisServiceAgain);
            }

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

    private void updateLocation(Location location) {
        //En caso que la location sea null, no se cerrará acutomáticamente la aplicación
        if (location != null) {
            lat = location.getLatitude();
            longit = location.getLongitude();
        }
    }

    @SuppressLint("MissingPermission")
    private void findLocation() {
        Location location = null;
        LocationManager locationManager = null;
        try {
            //Se agregan permisos para poder tomar la posición actual
            if (ActivityCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission
                    (context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //tomamos la ubicación actual
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            Log.e("TAG",e.getMessage());
            Log.e("TAG","No se pudo realizar el servicio de gps");
        }

        if (location != null && locationManager != null){
            //la actualizamos por medio del método hecho anteriormete
            updateLocation(location);
            //indicamos que actualice la ubicación actual cada 10 segundos
            //o cada que la ubicación cambie 5 metros
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locListener);
        }

    }

    public double getLat(){
        return lat;
    }

    public double getLongit(){
        return longit;
    }
}
