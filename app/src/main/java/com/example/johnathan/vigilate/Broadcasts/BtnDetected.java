package com.example.johnathan.vigilate.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.johnathan.vigilate.Firebase.FirebaseRTDB;
import com.example.johnathan.vigilate.MainActivity;
import com.example.johnathan.vigilate.PreferenceReferences.ReferencesSettings;
import com.example.johnathan.vigilate.Services.ServiceLocationGPS;



/**
 * Created by JohnathanMB on 30/10/2017.
 */

public class BtnDetected extends BroadcastReceiver {
    private double lat;
    private double longit;
    ServiceLocationGPS serviceLocationGPS;
    FirebaseRTDB firebaseRTDB = new FirebaseRTDB();
    private boolean alarmSent = false;
    private SharedPreferences settings;


    public BtnDetected(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        settings = context.getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, Context.MODE_PRIVATE);

        //logicia sobre si está activo la función de detectar el botón o no
        boolean btnDectedActived = settings.getBoolean(ReferencesSettings.NAME_BTNDETECTED_ACTIVED,false);
        if(btnDectedActived){
            alarmSent = true;
            SharedPreferences.Editor editorSettings = settings.edit();
            //activ la funcionalidad del botón btnStopService
            editorSettings.putBoolean(ReferencesSettings.BTN_STOPSERVICE_ACTIVED,true);
            //activo la actualización de la ubicación exacta de quien manda la alarma
            editorSettings.putBoolean(ReferencesSettings.UPDATE_LOCATION_ACTIVED,true);
            editorSettings.putBoolean(ReferencesSettings.ALERT_SENT, true);
            editorSettings.commit();
            //falta lógica para secuencia de botones
            Intent serviceSendLocation = new Intent(context, ServiceLocationGPS.class);
            context.startService(serviceSendLocation);
        }
    }

}
