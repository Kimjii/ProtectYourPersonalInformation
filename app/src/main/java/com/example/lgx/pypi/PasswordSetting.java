package com.example.lgx.pypi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PasswordSetting extends AppCompatActivity
{
    static final String PASSWORD_FILE = "password.txt";

    final String passwordSettingSuccess = "비밀번호가 성공적으로 저장되었습니다.";
    final String wrongPassword = "비밀번호가 일치하지 않습니다.";

    String password;
    String newPassword;

    EditText passwordEdit;
    Button confirmButton;
    TextView infoText;

    int count = 0;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordsetting);

        password = new String();
        newPassword = new String();

        loadPassword();

        infoText = (TextView) findViewById( R.id.infoText );
        passwordEdit = (EditText) findViewById( R.id.passwordEdit );

        Button cancelButton = (Button) findViewById( R.id.cancelButton );
        cancelButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick( View v )
                    {
                        finish();
                    }
                }
        );

        confirmButton = (Button) findViewById( R.id.confirmButton );
        View.OnClickListener listener = new View.OnClickListener() {

            public void onClick( View v ) {

                String input = passwordEdit.getText().toString();
                switch ( count )
                {
                    case 0:
                        Toast.makeText(getApplicationContext(),"password = " + password, Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"input = " + input, Toast.LENGTH_LONG).show();
                        if( !password.equals(input) ) {
                            Toast.makeText(getApplicationContext(), wrongPassword, Toast.LENGTH_LONG).show();
                            break;
                        }

                        setInfoText("변경할 비밀번호를 입력하세요.");
                        passwordEdit.setText("");
                        count++;
                        break;

                    case 1:
                        newPassword = passwordEdit.getText().toString();
                        setInfoText("변경할 비밀번호를 다시 입력하세요.");
                        passwordEdit.setText("");
                        count++;
                        break;

                    case 2:
                        if( !newPassword.equals(input) ){
                            Toast.makeText(getApplicationContext(), wrongPassword, Toast.LENGTH_LONG).show();
                            return;
                        }

                        password = newPassword;
                        savePassword();
                        finish();   // Activity 종료
                        break;
                }
            }
        };

        confirmButton.setOnClickListener( listener );

        TextWatcher inputWatcher = new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = passwordEdit.getText().toString();
                if ( input.length() > 0 )
                    confirmButton.setEnabled(true);
                else
                    confirmButton.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };
        passwordEdit.addTextChangedListener(inputWatcher);

    }

    void setInfoText(String text) {
        infoText.setText(text);
    }

    void savePassword()
    {
        try
        {
            FileOutputStream os = openFileOutput( PASSWORD_FILE, Context.MODE_PRIVATE );
            BufferedWriter buw = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));
            buw.write( password );
            buw.close();
            os.close();
        } catch( Exception e ){
            Log.e("File", "에러=" + e);
        }

        Toast.makeText(getApplicationContext(),passwordSettingSuccess, Toast.LENGTH_LONG).show();
    }

    void loadPassword()
    {   // 최초 실행 시 비밀번호 0000으로 설정 후 리턴; 다음 실행 부터는 저장된 값 읽어오기
        String path = new String( "/data/data/com.example.lgx.pypi/files/" + PASSWORD_FILE );// File Context.getFileStreamPath(String name) 파일 경로 얻는 api

        File files = new File( path );

        if ( !files.exists() )
        {
            notifyFirstExcutePgm();
            return;
        }

        try
        {
            FileInputStream fin = openFileInput(PASSWORD_FILE);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            password = br.readLine();
            Toast.makeText(getApplicationContext(), "password = " + password, Toast.LENGTH_LONG).show();

            if ( password=="0000" )
            {
                notifyFirstExcutePgm();
                return;
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    void notifyFirstExcutePgm()
    {
        String firstStartToast = "최초 실행 시 비밀번호는 0000입니다.";

        Toast.makeText(getApplicationContext(), firstStartToast, Toast.LENGTH_LONG).show();
        password = "0000";

    }
}

class PasswordFile{

}