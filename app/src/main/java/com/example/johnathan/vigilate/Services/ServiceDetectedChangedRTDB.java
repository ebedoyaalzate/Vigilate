package com.example.johnathan.vigilate.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.example.johnathan.vigilate.Broadcasts.BroadcastBoot;
import com.example.johnathan.vigilate.Firebase.FirebaseRTDB;
import com.example.johnathan.vigilate.Firebase.New_Help;
import com.example.johnathan.vigilate.MapsActivity;
import com.example.johnathan.vigilate.Notification.Notification;
import com.example.johnathan.vigilate.PreferenceReferences.ReferencesSettings;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by JohnathanMB on 31/10/2017.
 */

public class ServiceDetectedChangedRTDB extends Service {
    FirebaseRTDB firebaseRTDB = new FirebaseRTDB();
    Notification notification = new Notification();
    ServiceLocationGPS serviceLocationGPS;
    SharedPreferences setting;
    SharedPreferences.Editor editorSettings;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setting = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, MODE_PRIVATE);
        boolean notificationActived = setting.getBoolean(ReferencesSettings.NOTIFICATION_ACTIVED,true);
        if (notificationActived || !notificationActived){
            detectedNewHelp();
        }
        return START_STICKY;
    }

    public void detectedNewHelp(){
        DatabaseReference newHelp = firebaseRTDB.getNewHelp();



        newHelp.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //tomo el key del llamado de auxilio que llegan desde firebase
                String key = dataSnapshot.getKey();

                //se agrega el id al sharedPreference
                setting = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, MODE_PRIVATE);
                editorSettings = setting.edit();
                editorSettings.putString(ReferencesSettings.ID_USER,key);
                editorSettings.commit();

                //retomo el key que agregué al Shared
                //para verificar que si se haya agregado correctamente
                String userId = setting.getString(ReferencesSettings.ID_USER,"");
                String userIdLocal = setting.getString(ReferencesSettings.ID_USER_LOCAL,"");
                //para saber que no le llegue el mismo usuario que envía la notificación
                if(!userId.equals(userIdLocal)){
                    //para verificar que el key obtenido de firebase si haya guardado correctamente
                    if (!userId.equals("")){
                        //se envia notificación
                        notification.sendNotification(getApplicationContext());
                    }

                }

                /*
                double latFromHelp = Double.parseDouble(dataSnapshot.child(New_Help.FIELD_LAT).getValue().toString());
                //problema aquí, intenta leer el long antes de que se agregue la información
                double longiFromHelp = Double.parseDouble(dataSnapshot.child(New_Help.FIELD_LONG).getValue().toString());

                //tomo las cordenadas actuales del actual dispositivo
                serviceLocationGPS = new ServiceLocationGPS();
                double latForMe = serviceLocationGPS.getLat();
                double longForMe = serviceLocationGPS.getLongit();

                double distance = distanceCoord(latFromHelp, longiFromHelp, latForMe, longForMe);
                if(distance < 500 && distance > 1){
                    notification.sendNotification(getApplicationContext());
                }else{
                    Log.i("TAG","Distancia no debida, no se manda notificación "+distance);
                }
                */
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static double distanceCoord(double lat1, double lng1, double lat2, double lng2) {
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia;
    }

}
