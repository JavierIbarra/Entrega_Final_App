package com.example.jaiba.asistencia.User;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jaiba.asistencia.Fragment.AssistanceFragment;
import com.example.jaiba.asistencia.Fragment.ProfileFragment;
import com.example.jaiba.asistencia.Interface.IFragment;
import com.example.jaiba.asistencia.LoginActivity;
import com.example.jaiba.asistencia.R;
import com.example.jaiba.asistencia.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFragment,
        Response.Listener<JSONObject>,Response.ErrorListener{

    TextView textViewEmail;
    TextView textViewTipo;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;
    int hora_entrada;
    int minuto_entrada;
    int hora_salida;
    int minuto_salida;

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

        request= Volley.newRequestQueue(this);
        cargarWebService();
    }

    private void cargarWebService() {

        progress=new ProgressDialog(this);
        progress.setMessage("Consultando...");
        progress.show();

        SharedPreferences preferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE);
        String ID_EMPRESA = preferences.getString("id_empresa","");

        String url="http://javieribarra.cl/wsJSON.php?id_empresa="+ID_EMPRESA+"&horario=1";

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        VolleySingleton.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        progress.hide();

        JSONArray json = response.optJSONArray("Horario");
        JSONObject jsonObject = null;

        try {
            jsonObject = json.getJSONObject(0);
            hora_entrada = (jsonObject.optInt("hora_entrada"));
            minuto_entrada = (jsonObject.optInt("minuto_entrada"));
            hora_salida = (jsonObject.optInt("hora_salida"));
            minuto_salida=(jsonObject.optInt("minuto_salida"));

            NotificationActive(hora_entrada,minuto_entrada);
            NotificationActive(hora_salida,minuto_salida);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progress.hide();
        Toast.makeText(this,"No se pudo Consultar "+error.toString(),Toast.LENGTH_SHORT).show();
        Log.i("ERROR",error.toString());
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
            SharedPreferences preferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE);
            String email = preferences.getString("email", "");

            miFragment=new ProfileFragment();
            fragmentSeleccionado=true;

            Bundle args = new Bundle();
            args.putString("email", email);
            miFragment.setArguments(args);
        }

        if (fragmentSeleccionado==true){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_user,miFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void NotificationActive(int hora, int minuto){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,hora);
        c.set(Calendar.MINUTE,minuto);
        c.set(Calendar.SECOND,0);

        startAlarm(c);
    }

    public void startAlarm(Calendar c){
        AlarmManager alarmManager =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlertReciever.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,1,intent,0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
