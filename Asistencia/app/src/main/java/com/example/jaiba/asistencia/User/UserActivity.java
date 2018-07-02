package com.example.jaiba.asistencia.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.jaiba.asistencia.Fragment.AssistanceFragment;
import com.example.jaiba.asistencia.Interface.IFragment;
import com.example.jaiba.asistencia.LoginActivity;
import com.example.jaiba.asistencia.R;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFragment{

    TextView textViewEmail;
    TextView textViewTipo;

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("Login", Context.MODE_PRIVATE);

        if (preferences.getString("validacion","")==""){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        Cambiar(hView);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment miFragment=new AssistanceFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_user,miFragment).commit();
    }

    private void Cambiar(View view) {
        textViewEmail=(TextView)view.findViewById(R.id.textViewEmail);
        textViewTipo=(TextView)view.findViewById(R.id.textViewTipo);
        SharedPreferences preferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        textViewEmail.setText(preferences.getString("email",""));
        textViewTipo.setText("Usuario");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences preferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor Obj_editor = preferences.edit();
            Obj_editor.putString("validacion","");
            Obj_editor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent,1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment f =null;
        Fragment miFragment=null;
        boolean fragmentSeleccionado=false;

        if (id == R.id.nav_camera) {
            miFragment=new AssistanceFragment();
            fragmentSeleccionado=true;
        } else if (id == R.id.nav_profile) {

        }

        if (fragmentSeleccionado==true){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_user,miFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
