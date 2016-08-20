package com.gaurang.doctorover;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaurangpc.doctor.R;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    General_Info general_info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ContentResolver.setMasterSyncAutomatically(true);

        MainFragment mainFragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainer, mainFragment);
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        general_info = new General_Info(this);

        // Navigation Drawer Name and Phone , Email
        TextView userNametv = (TextView) navigationView.findViewById(R.id.navHeadUsername);
        TextView userEmailtv = (TextView) navigationView.findViewById(R.id.navHeaduserEmail);
        TextView userPhonetv = (TextView) navigationView.findViewById(R.id.navHeaduserPhone);

        userNametv.setText(general_info.getUserLoggedName());
        userEmailtv.setText(general_info.getUserLoggedEmail());
        userPhonetv.setText(general_info.getUserLoggedPhone());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment currentVisibleFragment =getVisibleFragment();
            fragmentTransaction.detach(currentVisibleFragment).attach(currentVisibleFragment).commit();

//            Toast.makeText(this, "Refresh Btn Click",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
//    public void setActionBarTitle(String title){
//        setTitle(title);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
        if (id == R.id.mainFragment) {
            MainFragment mainFragment = new MainFragment();
            fragmentTransaction.setCustomAnimations(0,0);
            fragmentTransaction.replace(R.id.mainFragmentContainer, mainFragment);
            fragmentTransaction.commit();

        }else if (id == R.id.addFriend) {
            AddFriend addFriendFragment = new AddFriend();
            fragmentTransaction.replace(R.id.mainFragmentContainer, addFriendFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.addGroup) {
            AddGroup addGroupFragment = new AddGroup();
            fragmentTransaction.replace(R.id.mainFragmentContainer, addGroupFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.editGroup) {
            EditGroup editGroupFragment = new EditGroup();
            fragmentTransaction.replace(R.id.mainFragmentContainer, editGroupFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.logOut) {
            SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            sharedPref.edit().clear().commit();

            General_Info general_info = new General_Info(this);
            general_info.deleteFileForPatientData();

            Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.abc_slide_in_top , R.anim.abc_slide_out_bottom);
            startActivity(intent, activityOptions.toBundle());
        } else if (id == R.id.nav_about) {

            AboutFragment aboutFragment = new AboutFragment();
            fragmentTransaction.replace(R.id.mainFragmentContainer, aboutFragment);
            fragmentTransaction.commit();
        } else if(id == R.id.nav_how) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.putExtra("launchNecessary", true);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
