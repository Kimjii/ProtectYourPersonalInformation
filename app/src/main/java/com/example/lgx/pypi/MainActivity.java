package com.example.lgx.pypi;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/*
    PROTECT YOUR PERSONAL INFORMATION !
 */

public class MainActivity extends AppCompatActivity
{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );

        // Set up the ViewPager with the sections adapter.
        mViewPager = ( ViewPager ) findViewById( R.id.container );
        mViewPager.setAdapter( mSectionsPagerAdapter );

        TabLayout tabLayout = ( TabLayout ) findViewById( R.id.tabs );
        tabLayout.setupWithViewPager( mViewPager );
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings )
        {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter ( FragmentManager fm )
        {
            super( fm );
        }

        @Override
        public Fragment getItem ( int position )
        {
            Fragment fragment = null;

            switch ( position )
            {
                case 0:
                    fragment = new LockManageFragment();
                    break;

                case 1:
                    fragment = new AppManageFragment();
                    break;

                case 2:
                    fragment = new PasswordManageFragment();
                    break;

                case 3:
                        /* admin receiver 내부에 있음*/
                    fragment = new AdminReceiver.MissingManageFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount ()
        {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle ( int position ) {
            switch (position) {
                case 0:
                    return "잠금 관리";
                case 1:
                    return "어플 관리";
                case 2:
                    return "암호 관리";
                case 3:
                    return "분실 관리";
            }
            return null;
        }
    }

}
