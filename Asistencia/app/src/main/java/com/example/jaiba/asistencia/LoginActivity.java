package com.example.jaiba.asistencia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.jaiba.asistencia.Admin.AdminActivity;
import com.example.jaiba.asistencia.User.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{


    TextView email;
    TextView password;
    Button btnLogin;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progress;
    int Validacion;
    String ID_Empresa;
    int Administrador;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(TextView)findViewById(R.id.Email);
        password=(TextView)findViewById(R.id.Password);
        btnLogin= (Button)findViewById(R.id.buttonLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarWebService();
            }
        });
    }

    private void cargarWebService() {

        boolean validar_email=validarEmail(email.getText().toString());

        if (validar_email && password.length()>=4){

            progress=new ProgressDialog(this);
            progress.setMessage("Consultando...");
            progress.show();

            String url="http://javieribarra.cl/wsJSON.php?email_trabajador="
                    + email.getText().toString() + "&password_trabajador=" + password.getText().toString();

            jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
            VolleySingleton.getIntanciaVolley(this).addToRequestQueue(jsonObjectRequest);
        }

        else{
            Toast.makeText(this, "Email o contraseña invalidos", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        progress.hide();
        Toast.makeText(this,"No se pudo Consultar "+error.toString(),Toast.LENGTH_SHORT).show();
        Log.i("ERROR",error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        progress.hide();

        JSONArray json=response.optJSONArray("Trabajador");
        JSONObject jsonObject=null;

        try {
            jsonObject=json.getJSONObject(0);
            Validacion=(jsonObject.optInt("numero"));
            ID_Empresa=(jsonObject.optString("id_empresa"));
            Administrador=(jsonObject.optInt("administrador"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Validacion==1){

            SharedPreferences preferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor Obj_editor = preferences.edit();
            Obj_editor.putString("validacion","true");
            Obj_editor.putString("id_empresa",ID_Empresa);
            Obj_editor.putString("email",email.getText().toString());


            if (Administrador==1){
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivityForResult(intent,1);
                Obj_editor.putString("admin","Administrador");
            }
            else{
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivityForResult(intent,1);
                Obj_editor.putString("admin","Usuario");
            }
            Obj_editor.commit();
        }
        else{
            Toast.makeText(this,"Email o contraseña invalidos", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
