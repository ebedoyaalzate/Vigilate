package com.example.johnathan.vigilate.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.johnathan.vigilate.Services.ServiceDetectedChangedRTDB;

public class BroadcastBoot extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("TAG", "inicie el servicio");
        Intent service = new Intent(context,  ServiceDetectedChangedRTDB.class);
        context.startService(service);
    }

    public Context getContext(){
        return context;
    }
}
