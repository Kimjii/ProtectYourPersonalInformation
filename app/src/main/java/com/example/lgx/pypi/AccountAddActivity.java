package com.example.lgx.pypi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by LGX on 2016-12-05.
 */
public class AccountAddActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountadd);
        setTitle( "계정 저장" );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_add, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_accountadd)
        {
            final EditText titleEdit = (EditText)findViewById( R.id.titleEditText );
            final EditText accountEdit = (EditText)findViewById( R.id.accountEditText );
            final EditText passwordEdit = (EditText)findViewById( R.id.accountpassEditText );
            final EditText webAddressEdit = (EditText)findViewById( R.id.webAddressEditText );

            String title = titleEdit.getText().toString();
            String account = accountEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            String webAddress = webAddressEdit.getText().toString();

            AccountManageFragment.dbHelper.insert( title, account, password, webAddress );

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
