package com.example.lgx.pypi;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class LockActivity extends AppCompatActivity
{
    static final String PASSWORD_FILE = "password.txt";
    Button confirmButton;
    Button cancelButton;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED       //  잠금화면 위에 액태비티를 띄움
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  //  기존 잠금화면을 없앰..

        password = new String();
        loadPassword();

        Calendar calendar = Calendar.getInstance( );
        final String[] week = { "Sun.", "Mon.", "Tue.", "Wed.", "Thu", "Fri.", "Sat." };
        final String[] month = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

        TextView currentDate = (TextView)findViewById(R.id.currentDate);
        currentDate.setText( week[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", "
                        + month[calendar.get(Calendar.MONTH) - 1] + calendar.get(Calendar.DAY_OF_MONTH) + ", "
                        + calendar.get(Calendar.YEAR) );

        final EditText passwordField = (EditText)findViewById(R.id.PasswordField);
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = passwordField.getText().toString();
                if (input.length() > 0)
                    confirmButton.setEnabled(true);
                else
                    confirmButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };
        passwordField .addTextChangedListener(inputWatcher);

        confirmButton = (Button)findViewById(R.id.lockActivity_confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( password.equals( passwordField.getText().toString() ) )
                    finish();
                else
                    passwordField.setText("");
            }
        });

        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( actionId == EditorInfo.IME_ACTION_DONE )
                {
                    confirmButton.callOnClick();
                }
                return false;
            }
        });

        cancelButton = (Button)findViewById(R.id.lockActivity_cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordField.setText("");
                Intent cameraIntent = new Intent( LockActivity.this, TakePictureActivity.class );
                startActivity( cameraIntent );
            }
        });

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

