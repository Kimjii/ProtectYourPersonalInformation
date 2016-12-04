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

    public void send(byte[] msg, Context context){
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

    /* send */
    public void sendInitialize(){
        // gear와 통신해야하는 모든 정보 전송

        if (mIsBound == true && communicationService != null){
            // 1. 잠금관리 - 잠금 활성화 여부, 비밀번호
            communicationService.sendData( String.valueOf(LockManageFragment.getLockActivationToggleState()) );
            communicationService.sendData( String.valueOf(LockManageFragment.getPassword()) );

            // 2.

            // 3.

            // 4. 분실관리 - 초기화 활성화 여부

        }
    }

    /* receive */
    public static void branchFromTizenMsg( String message ){
        String messages[] = null;

        if ( message.contains("-") )
            messages = message.split("-");

        if( messages == null )  return;

        switch( messages[0] ){
            case "1": {
                if (messages[1].equals("1")) {
                    if (messages[2].equals("1"))
                        LockManageFragment.setLockActivationToggleState(true);
                    else
                        LockManageFragment.setLockActivationToggleState(false);
                } else if (messages[1].equals("2"))
                    LockManageFragment.setPassword(messages[2]);

                break;
            }

            case "2":
                break;

            case "3":
                break;

            case "4":
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
