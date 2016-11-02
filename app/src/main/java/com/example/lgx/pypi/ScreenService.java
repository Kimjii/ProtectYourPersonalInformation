package com.example.lgx.pypi;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

/**
 * Created by Jiwon on 2016-10-31.
 */
public class ScreenService extends Service {

    private ScreenReceiver receiver = null;
    private PackageReceiver pReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
        registerRestartAlarm(true);

        pReceiver = new PackageReceiver();
        IntentFilter pFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        pFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        pFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        pFilter.addDataScheme("package");
        registerReceiver(pReceiver, pFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent != null )
        {
            if(intent.getAction() == null )
            {
                if( receiver == null ){
                    receiver = new ScreenReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(receiver, filter);
                }
            }
        }
        Notification notification = new Notification(0, "서비스 실행됨", System.currentTimeMillis());
        //notification.setLatestEventInfo(getApplicationContext(), "Screen Service", "Foreground로 실행됨", null);
        startForeground(1, notification);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(receiver != null)
            unregisterReceiver(receiver);

        if(pReceiver != null)
            unregisterReceiver(pReceiver);

        registerRestartAlarm(false);
    }

    public void registerRestartAlarm(boolean isOn){
        Intent intent = new Intent( ScreenService.this, RestartReceiver.class);
        intent.setAction(RestartReceiver.ACTION_RESTART_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast( getApplicationContext(), 0, intent, 0);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        if(isOn){
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, 1800000, sender);
        }else{
            am.cancel(sender);
        }
    }
}
