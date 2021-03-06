package com.example.lgx.pypi;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AdminReceiver extends  DeviceAdminReceiver{
    public static class MissingManageFragment extends Fragment {
        CommunicationManager communicationManager = new CommunicationManager();

        private static DevicePolicyManager mDPM;       //디바이스 정책 관리자
        private static ComponentName mDeviceAdmin;    //컴포넌트 네임

        private ToggleButton initializeActivationToggleButton;
        private Button initializeButton;

        SharedPreferences sharedPreferences;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_missingmanage, null);

            // communication related
            communicationManager.bindService( getActivity() );
            communicationManager.connect();
            // communication related

            //디바이스 정책 관리자 구현
            mDPM = (DevicePolicyManager)getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);

            //컴포넌트 네임에 클래스 지정
            mDeviceAdmin = new ComponentName( this.getContext(), AdminReceiver.class );
            //setContentView(R.layout.fragment_missingmanage);

            initializeButton = (Button)view.findViewById(R.id.initializeButton);
            initializeButton.setOnClickListener(initializeListener);

            initializeActivationToggleButton = (ToggleButton)view.findViewById(R.id.initializeActivationToggleButton);
            initializeActivationToggleButton.setOnClickListener( initializeActivationListener );

            sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

            //생성된 View 객체를 리턴
            return view;
        }

        /*Toggle Button Listener*/
        private View.OnClickListener initializeActivationListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( initializeActivationToggleButton.isChecked() ){
                    // 기기 관리자 설정 액티비티 시작
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra( DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin );
                    startActivity(intent);

                }
                else
                    mDPM.removeActiveAdmin(mDeviceAdmin);


                updateButtonStates();
            }
        };

        /*Initialize Button Listener*/
        private View.OnClickListener initializeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("초기화 하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        boolean active = mDPM.isAdminActive(mDeviceAdmin);
                        if(active){
                            initializeDevice();
                        }
                    }
                });
                builder.setNegativeButton("아니오",null);
                builder.show();
            }
        };

        void updateButtonStates(){
            boolean active = mDPM.isAdminActive(mDeviceAdmin);

            if(active){
                initializeButton.setEnabled(true);
            }
            else{
                initializeButton.setEnabled(false);
            }
            sendState();
        }

        void sendState(){
            if ( initializeActivationToggleButton.isChecked() )
                communicationManager.send("4-1-1", getActivity().getApplicationContext() );
            else
                communicationManager.send("4-1-2", getActivity().getApplicationContext() );
        }

        @Override
        public void onResume() {
            super.onResume();

            // communication related
            communicationManager.bindService( getActivity() );
            communicationManager.connect();
            // communication related

            updateButtonStates();
            load();
        }

        public static void initializeDevice(){
            mDPM.wipeData(0);
        }

        /* 앱 종료 후 재실행 시 컴포넌트 상태 저장을 위한 부분*/
        public void onPause() {
            super.onPause();
            save();
        }

        public void save() {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("initializeActivationToggleState", initializeActivationToggleButton.isChecked());
            editor.commit();
        }

        public void load() {
            boolean isChecked = sharedPreferences.getBoolean( "initializeActivationToggleState", false );

            initializeActivationToggleButton. setChecked(isChecked);
            sendState();
        }
    }
}