package com.example.lgx.pypi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LGX on 2016-12-05.
 */
public class DBHelper extends SQLiteOpenHelper
{
    public DBHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version )
    {
        super( context, name, factory, version );
    }

    @Override
    public void onCreate( SQLiteDatabase database )
    {
        database.execSQL( "CREATE TABLE ACCOUNTBOOK ( _id INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, ACCOUNT TEXT, PASSWORD TEXT, WEBADDRESS TEXT );" );
    }

    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion )
    {

    }

    public void insert( String title,  String account, String password, String webAddress )
    {
        SQLiteDatabase database = getWritableDatabase();

        database.execSQL( "INSERT INTO ACCOUNTBOOK VALUES( null, '" + title + "', '" + account + "', '" + password + "', '" + webAddress + "');" );
        database.close();
    }

    public void update( String title, String password )
    {
        SQLiteDatabase database = getWritableDatabase();

        database.execSQL( "UPDATE ACCOUNTBOOK SET password = '" + password + "' WHERE title = '" + title + "';" );
        database.close();
    }

    public String search( int value )
    {
        SQLiteDatabase database = getWritableDatabase();
        String result = "";

        Cursor cursor = database.rawQuery( "SELECT * FROM ACCOUNTBOOK WHERE _id = '" + String.valueOf( value ) + "'", null );
        while ( cursor.moveToNext() )
        {
            result += cursor.getString( 0 ) + ", " + cursor.getString( 1 )
                    + ", " + cursor.getString( 2 ) + ", " + cursor.getString( 3 )
                    + ", " + cursor.getString( 4 ) + "\n" ;
        }
        return result;
    }

    public void delete( int value )
    {
        SQLiteDatabase database = getWritableDatabase();

        database.execSQL( "DELETE FROM ACCOUNTBOOK WHERE _id = '" + String.valueOf( value ) + "';" );
        database.close();
    }
}
