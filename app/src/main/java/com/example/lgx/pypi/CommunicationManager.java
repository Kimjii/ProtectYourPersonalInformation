package com.example.lgx.pypi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jiwon on 2016-11-24.
 */
public class CommunicationManager {

    private static CommunicationManager cm = null;
    private boolean mIsBound = false;
    private CommunicationService communicationService = null;

    private CommunicationManager(){}

    public static CommunicationManager getInstance(){
        if ( cm == null )
            cm = new CommunicationManager();
        return cm;
    }


    public void bindService( Activity activity ){
        mIsBound = activity.bindService( new Intent(activity, CommunicationService.class), mConnection, Context.BIND_AUTO_CREATE );
    }

    public void connect(){
        if( mIsBound == true && communicationService != null){
            communicationService.findPeers();
        }
    }

    public void disconnect(Context context){
        if (mIsBound == true && communicationService != null) {
            if (communicationService.closeConnection() == false) {
                Toast.makeText(context, R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void send(String msg, Context context){
        if (mIsBound == true && communicationService != null){
            communicationService.sendData(msg);

        }else {
            Toast.makeText(context, R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
        }
    }

    public void unbound(Activity activity) {
        // Un-bind service
        if (mIsBound) {
            activity.unbindService(mConnection);
            mIsBound = false;
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            communicationService = ((CommunicationService.LocalBinder) service).getService();
            //
            Log.i("Connector", "connect!");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            communicationService = null;
            mIsBound = false;
            //
            Log.i("Disconnector", "disconnect!");
        }
    };
}
