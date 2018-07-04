package com.example.jaiba.asistencia.User;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlertReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper=new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("Asistencia","Recuerda es la hora de registrarse");
        notificationHelper.getManager().notify(1,nb.build());
    }
}
