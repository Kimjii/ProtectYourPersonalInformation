package com.example.lgx.pypi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LGX on 2016-12-05.
 */
public class AccountManageFragment extends Fragment
{
    CommunicationManager communicationManager = new CommunicationManager();//

    static DBHelper dbHelper;
    SQLiteDatabase db;
    AccountListViewAdapter adapter = null;
    ListView accountListView;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_accountmanage, null );

        // communication related
        communicationManager.bindService( getActivity() );
        communicationManager.connect();
        // communication related

        dbHelper = new DBHelper( view.getContext(), "ACCOUNTBOOK.db", null, 1 );
        db = dbHelper.getWritableDatabase();
        accountListView = ( ListView ) view.findViewById( R.id.accountManageListView );

        showAccountListView();

        /* FAB 클릭 리스너 선언 - ListView에 추가(DB 추가) */
        FloatingActionButton fab = ( FloatingActionButton ) view.findViewById( R.id.fab );
        fab.setOnClickListener( new Button.OnClickListener()
        {
            @Override
            public void onClick ( View view )
            {
                Intent addIntent = new Intent( getActivity(), AccountAddActivity.class );
                startActivity( addIntent );
                showAccountListView();
            }
        } );

        /* ListView Select Item 이벤트 선언 - ListView에 수정(DB 수정) */
        accountListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick ( AdapterView<?> parent, View view, int position, long id )
            {
                Intent detailIntent = new Intent( getActivity(), AccountDetailActivity.class );
                detailIntent.putExtra( "ID", adapter.getID(position) );
                startActivity( detailIntent );
            }
        } );

        /* ListView Long Select Item 이벤트 선언 - ListView에 삭제(DB 삭제) */
        accountListView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick ( AdapterView<?> parent, View view, int position, long id )
            {
                dbHelper.delete( adapter.getID( position ) );
                Toast.makeText( getContext(), "계정이 삭제되었습니다.", Toast.LENGTH_SHORT ).show();
                showAccountListView();

                return true;
            }

        } );

        return view;
    }

    public void showAccountListView ()
    {
        Cursor cursor = db.rawQuery( "SELECT * FROM ACCOUNTBOOK", null );

        if( adapter != null )
            adapter.getCursor().close();

        adapter = new AccountListViewAdapter( getActivity(), cursor );

        accountListView.setAdapter( adapter );
        sendAccountInfo();//
    }

    public void sendAccountInfo(){//
        JSONArray jsonArray = SQLiteToJSONArray();
        String sendData = "3-" + jsonArray.toString();
        communicationManager.send(sendData, this.getContext() );
    }

    public JSONArray SQLiteToJSONArray(){//
        String query = "SELECT * FROM ACCOUNTBOOK";
        Cursor cursor = db.rawQuery( query, null );

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while( cursor.isAfterLast() == false ){
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0; i<totalColumn; i++){
                if( cursor.getColumnName(i) != null){
                    try{
                        if( cursor.getString(i) != null ){
                            Log.d("SQLiteToJSON", cursor.getString(i));
                            rowObject.put( cursor.getColumnName(i), cursor.getString(i) );
                        }
                        else{
                            rowObject.put( cursor.getColumnName(i), "" );
                        }
                    }catch ( Exception e ){
                        Log.d("SQLiteToJSON", e.getMessage() );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("SQLiteToJSON", resultSet.toString() );

        return resultSet;
    }

}
