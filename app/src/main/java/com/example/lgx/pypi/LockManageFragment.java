package com.example.lgx.pypi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LockManageFragment extends Fragment
{
    //test
    CommunicationManager communicationManager = new CommunicationManager();

    static final String PASSWORD_FILE = "password.txt";
    TextView disPassword;

    static ToggleButton lockActivationToggleButton;
    SharedPreferences sharedPreferences;

    static String password;
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_lockmanage, null );

        final Button changePasswdBtn = (Button) view.findViewById(R.id.changePasswdButton) ;
        changePasswdBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : click event
                // 비밀번호 설정 액티비티로 이동
                Intent lockintent = new Intent( getActivity() , PasswordSetting.class );
                startActivity( lockintent );
            }
        });

        loadPassword();

        lockActivationToggleButton = (ToggleButton)view.findViewById(R.id.lockActivationButton);
        lockActivationToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){
                    Intent intent = new Intent( getActivity(), ScreenService.class);
                    getActivity().startService(intent);
                }
                else {
                    Intent intent = new Intent( getActivity(), ScreenService.class);
                    getActivity().stopService(intent);
                }
                sendState();
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        //생성된 View 객체를 리턴
        return view;
    }

    void sendState(){
        if ( getLockActivationToggleState() )
            communicationManager.send("1-1-1", getActivity().getApplicationContext() );
        else
            communicationManager.send("1-1-2", getActivity().getApplicationContext() );
    }

    void loadPassword()
    {   // 최초 실행 시 비밀번호 0000으로 설정 후 리턴; 다음 실행 부터는 저장된 값 읽어오기
        String path = new String( "/data/data/com.example.lgx.pypi/files/" + PASSWORD_FILE );// File Context.getFileStreamPath(String name) 파일 경로 얻는 api

        File files = new File( path );

        if ( !files.exists() )
            return;

        try
        {
            FileInputStream fin = getActivity().openFileInput(PASSWORD_FILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            password = br.readLine();
            Toast.makeText(getActivity().getApplicationContext(), "password = " + password, Toast.LENGTH_LONG).show();

        } catch( Exception e ) {
            e.printStackTrace();
        }

        disPassword.setText( disPassword.getText() + password );
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

        editor.putBoolean("lockActivationToggleState", lockActivationToggleButton.isChecked());
        editor.commit();
    }

    public void load() {
        boolean isChecked = sharedPreferences.getBoolean( "lockActivationToggleState", false );

        lockActivationToggleButton. setChecked(isChecked);
    }

    // communication unbind

    @Override
    public void onDestroy() {
        //communicationManager.destroy();
        super.onDestroy();
    }

    // 타이젠 통신 시 초기 데이터 설정 위해
    public static boolean getLockActivationToggleState(){
        return lockActivationToggleButton.isChecked();
    }

    final static Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            lockActivationToggleButton.setChecked( !lockActivationToggleButton.isChecked() );
        }
    };
    public static void setLockActivationToggleState(boolean state){
        Log.i("LockManageActivity", "isChecked() = " + lockActivationToggleButton.isChecked() + ", state = " + state);

        if ( lockActivationToggleButton.isChecked() != state ){
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

    public static String getPassword(){ return password; }

    public static void setPassword(String passwd){ password = passwd;  }

}