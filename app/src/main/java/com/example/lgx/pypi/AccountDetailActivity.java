package com.example.lgx.pypi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by LGX on 2016-12-05.
 */
public class AccountDetailActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountdetail);
        setTitle( "계정 조회" );

        Intent intent = getIntent();

        TextView showTitle = (TextView)findViewById( R.id.showTitleTextView );
        TextView showAcc = (TextView)findViewById( R.id.showAccountTextView );
        TextView showPass = (TextView)findViewById( R.id.showPasswordTextView );
        TextView showWebadd = (TextView)findViewById( R.id.showAddressTextView );

        int index = intent.getExtras().getInt( "ID" );
        String cursorStr = AccountManageFragment.dbHelper.search( index );
        String splitStr[] = cursorStr.split( ", " );

        showTitle.setText( splitStr[1] );
        showAcc.setText( splitStr[2] );
        showPass.setText( splitStr[3] );
        showWebadd.setText( splitStr[4] );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_account_update)
        {
            Intent addIntent = new Intent( this, AccountAddActivity.class );
            startActivity( addIntent );

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
