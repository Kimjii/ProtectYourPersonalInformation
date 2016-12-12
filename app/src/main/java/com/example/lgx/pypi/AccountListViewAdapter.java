package com.example.lgx.pypi;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by LGX on 2016-12-05.
 */
public class AccountListViewAdapter extends CursorAdapter
{
    int _idList[] = new int[ 300 ];
    int index = 0;

    @SuppressWarnings( "deprecation" )
    public AccountListViewAdapter( Context context, Cursor c )
    {
        super(context, c);
    }

    @Override
    public void bindView( View view, Context context, Cursor cursor )
    {
        TextView titleTextView = (TextView)view.findViewById( android.R.id.text1 );
        TextView accountTextView = (TextView)view.findViewById( android.R.id.text2 );

        String title = cursor.getString( cursor.getColumnIndex( "TITLE" ) );
        String account = cursor.getString( cursor.getColumnIndex( "ACCOUNT" ) );

        titleTextView.setText( title );
        accountTextView.setText( account );

        _idList[index++] = cursor.getInt( cursor.getColumnIndex( "_id" ) );
    }

    @Override
    public View newView( Context context, Cursor cursor, ViewGroup parent )
    {
        LayoutInflater inflater = LayoutInflater.from( context );
        View v = inflater.inflate( android.R.layout.simple_list_item_2, parent, false );

        return v;
    }

    public int getID( int position )
    {
        return _idList[position];
    }

}
