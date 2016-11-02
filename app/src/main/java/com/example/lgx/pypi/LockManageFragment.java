package com.example.lgx.pypi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LockManageFragment extends Fragment
{
    static final String PASSWORD_FILE = "password.txt";
    TextView disPassword;

    ToggleButton lockActivationButton;
    SharedPreferences sharedPreferences;

    String password;
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

        disPassword = (TextView) view.findViewById( R.id.disPassword );

        loadPassword();

        lockActivationButton = (ToggleButton)view.findViewById(R.id.lockActivationButton);
        lockActivationButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v ){
                if( lockActivationButton.isChecked() ){
                    Intent intent = new Intent( getActivity(), ScreenService.class);
                    getActivity().startService(intent);
                }
                else {
                    Intent intent = new Intent( getActivity(), ScreenService.class);
                    getActivity().stopService(intent);
                }
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        //생성된 View 객체를 리턴
        return view;
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
        save( lockActivationButton.isChecked() );
    }

    @Override
    public void onResume() {
        super.onResume();
        lockActivationButton.setChecked(load());
    }

    private void save(final boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("check", isChecked);
        editor.commit();
    }

    private boolean load() {
        return sharedPreferences.getBoolean("check", false);
    }
}
