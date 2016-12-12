package com.example.lgx.pypi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Jiwon on 2016-11-24.
 */
public class CommunicationManager {

    private boolean mIsBound = false;
    private CommunicationService communicationService = null;
    private Activity activity = null;

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
                Log.i("CommunicationManager",  "Connection disconnected.");
                //Toast.makeText(context, R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void send(String msg, Context context){
        if (mIsBound == true && communicationService != null){
            communicationService.sendData(msg);

        }else {
            Log.i("CommunicationManager", "Service Connection has already been disconnected");
            //Toast.makeText(context, R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
        }
    }

    public void send(byte[] msg, Context context){
        if (mIsBound == true && communicationService != null){

            communicationService.sendData(msg);

        }else {
            Log.i("CommunicationManager", "Service Connection has already been disconnected");
            //Toast.makeText(context, R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
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

    /* receive */
    public static void branchFromTizenMsg( String message ){
        String messages[] = null;
        Log.i("CommunityManager", message);
        if ( message.contains("-") )
            messages = message.split("-");

        if( messages == null )  return;

        switch( messages[0] ){
            // lock manage
            case "1": {
                if (messages[1].equals("1")) {
                    if (messages[2].equals("1")) // 1-1-1
                        LockManageFragment.setLockActivationToggleState(true);

                    else //1-1-2
                        LockManageFragment.setLockActivationToggleState(false);

                }
                else if (messages[1].equals("2")) // 1-2-1
                    LockManageFragment.setPassword(messages[2]);

                // android에서는 1-3-xxx data를 수신하지 않음

                break;
            }

            // app manage
            case "2": {
                if (messages[1].equals("1")) {
                    if (messages[2].equals("1")) // 2-1-1
                        AppManageFragment.setModeActivationToggleState( true );

                    else // 2-1-2
                        AppManageFragment.setModeActivationToggleState( false );
                }

                break;
            }

            // account manage ; gear to android 통신은 하지 않음
            case "3":
                break;

            // missing manage
            case "4":
                if (messages[1].equals("2")) {
                    if (messages[2].equals("2")) // 4-2-2
                        AdminReceiver.MissingManageFragment.initializeDevice();
                        //Log.i("Receive", "초기화!");
                }
                break;

            default:
                break;
        }

    }

    protected void destroy() {
        if ( activity == null )
            return;

        // Clean up connections
        if (mIsBound == true && communicationService != null) {
            if (communicationService.closeConnection() == false) {
                Log.i("CommunicationManager", "disconnected");
            }
        }
        // Un-bind service
        if (mIsBound) {
            activity.unbindService(mConnection);
            mIsBound = false;
        }
    }
}
