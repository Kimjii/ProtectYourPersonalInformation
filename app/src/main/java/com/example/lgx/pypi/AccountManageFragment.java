package com.example.lgx.pypi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by LGX on 2016-12-05.
 */
public class AccountManageFragment extends Fragment
{
    static DBHelper dbHelper;
    Cursor cursor;
    SQLiteDatabase db;
    AccountListViewAdapter adapter;
    ListView accountListView;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.fragment_accountmanage, null );

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
                adapter.notifyDataSetChanged();

                return true;
            }

        } );

        return view;
    }

    public void showAccountListView ()
    {
        cursor = db.rawQuery( "SELECT * FROM ACCOUNTBOOK", null );
        adapter = new AccountListViewAdapter( getActivity(), cursor );

        accountListView.setAdapter( adapter );
    }

}
