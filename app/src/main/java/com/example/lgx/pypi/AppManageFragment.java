package com.example.lgx.pypi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppManageFragment extends Fragment
{
    CommunicationManager communicationManager = new CommunicationManager();
    static ToggleButton toggleButton;
    Intent launcherIntent;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_appmanage, null );

        final TextView textView = (TextView)view.findViewById( R.id.appManageTextView );
        toggleButton = (ToggleButton)view.findViewById( R.id.appManageToggle );
        toggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged ( CompoundButton buttonView, boolean isChecked )
            {
                if ( isChecked ) // private
                {
                    textView.setText( "Private 모드 입니다." );
                    writeFile("private");
                }

                else // public
                {
                    textView.setText( "Public 모드 입니다." );
                    writeFile("public");
                }

                sendState();
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        return view;
    }

    void sendState()
    {
        if ( getModeActivationToggleState() )
            communicationManager.send("2-1-1", getActivity().getApplicationContext() );
        else
            communicationManager.send("2-1-2", getActivity().getApplicationContext() );
    }

    // 타이젠 통신 시 초기 데이터 설정 위해
    public static boolean getModeActivationToggleState(){
        return toggleButton.isChecked();
    }

    final static Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            toggleButton.setChecked( !toggleButton.isChecked() );
        }
    };
    public static void setModeActivationToggleState(boolean state){
        Log.i("AppManageActivity", "isChecked() = " + toggleButton.isChecked() + ", state = " + state);

        if ( toggleButton.isChecked() != state ){
            new Thread()
            {
                public void run()
                {
                    Message msg = handler.obtainMessage();

                    handler.sendMessage(msg);
                }
            }.start();
        }
    }

    /* 앱 종료 후 재실행 시 컴포넌트 상태 저장을 위한 부분*/
    public void onPause() {
        super.onPause();
        save();
    }

    @Override
    public void onResume() {
        super.onResume();

        // communication related
        communicationManager.bindService( getActivity() );
        communicationManager.connect();
        sendState();
        // communication related

        load();
    }

    public void save() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("modeActivationToggleState", toggleButton.isChecked());
        editor.commit();
    }

    public void load() {
        boolean isChecked = sharedPreferences.getBoolean( "modeActivationToggleState", false );

        toggleButton. setChecked(isChecked);
    }


    public void writeFile( String fileStr )
    {
        File file;
        String path = Environment.getExternalStorageDirectory()+"/pypimode";
        file = new File(path);

        if( !file.exists() ) // 원하는 경로에 폴더가 있는지 확인
            file.mkdirs();

        file = new File(path+"/mode.txt");

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileStr.getBytes());
            fos.close();
        }

        catch(IOException e) {}
    }

}
