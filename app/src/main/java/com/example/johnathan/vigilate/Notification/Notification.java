package com.example.johnathan.vigilate.Notification;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.johnathan.vigilate.MapsActivity;
import com.example.johnathan.vigilate.R;

import java.util.Random;

/**
 * Created by JohnathanMB on 29/10/2017.
 */

public class Notification {

    private int NOTIFICATION_ID=1;

    @SuppressLint("ResourceAsColor")
    public void sendNotification(Context context){
        //construir la acción post press en notificación
        Intent intent = new Intent(context, MapsActivity.class);
        //intent.putExtra("idUser",userId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        //sound notification
        Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        //intentando que cibre
        long num1 = 100;
        long num2 = 200;
        long num3 = 300;
        long[] vib = {num1,num2,num3};

        //construir la notificación


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.png);
        builder.setContentIntent(pendingIntent);
        builder.setSound(soundNotification);
        builder.setVibrate(vib);
        builder.setAutoCancel(true);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.png));
        builder.setContentTitle("¡Sé Un Heroe!");
        builder.setContentText("Alguien Necesita Tu Ayuda");
        builder.setColor(R.color.colorAccent);

        //Enviar notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        notificationManager.notify(random.nextInt(), builder.build());

    }

}
