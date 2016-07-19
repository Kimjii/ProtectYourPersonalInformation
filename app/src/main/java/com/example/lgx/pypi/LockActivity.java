package com.example.lgx.pypi;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class LockActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        ActionBar actionBar = this.getActionBar();
        actionBar.hide();

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED       //  잠금화면 위에 액태비티를 띄움
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);  //  기존 잠금화면을 없앰..
    }

}

